(ns mateuszmazurczak.adapters.cache.state-persistence
  "State persistence layer - handles saving/loading app-db to/from cache.
   
   Uses editscript to efficiently detect which domains changed,
   then saves only changed domains to separate localStorage keys.
   
   This is a cache mechanism:
   - Backend is source of truth
   - On version mismatch, cache is invalidated
   - Gracefully handles errors (falls back to empty state)"
  (:require
   [editscript.core                        :as e]
   [mateuszmazurczak.domain.cache.registry :as cache-registry]
   [mateuszmazurczak.ports.cache           :as cache]))

(defonce ^{:private true
           :doc "Last persisted app-db snapshot for diff comparison"}
         last-snapshot
  (atom nil))

(defn- check-version!
  "Check if cached version matches current version.
   If mismatch, clear all persisted domains and update version.
   
   Returns: true if version valid, false if invalidated"
  []
  (let [stored-version (cache/get-item cache-registry/version-cache-path)
        current-version cache-registry/version]
    (if (= stored-version current-version)
      true
      (do (doseq [[_domain-id {:keys [key]}] cache-registry/domains] (cache/remove-item! key))
          (cache/set-item! cache-registry/version-cache-path current-version)
          false))))

(defn save-domain!
  "Save a single domain to cache.
   
   Arguments:
   - db: Current app-db value
   - domain-id: Keyword identifying the domain (from registry)
   
   Returns: true on success, false on failure"
  [db domain-id]
  (if-let [{:keys [key path]} (get cache-registry/domains domain-id)]
    (let [value (get-in db path)] (if value (cache/set-item! key value) false))
    false))

(defn- find-changed-domains
  "Use editscript to detect which domains changed between old-db and new-db.
   
   Arguments:
   - old-db: Previous app-db snapshot
   - new-db: Current app-db value
   
   Returns: Set of domain-ids that changed"
  [old-db new-db]
  (reduce-kv (fn [changed domain-id {:keys [path]}]
               (let [old-val (get-in old-db path)
                     new-val (get-in new-db path)
                     diff (e/diff old-val new-val)]
                 (if (seq (e/get-edits diff)) (conj changed domain-id) changed)))
             #{}
             cache-registry/domains))

(defn persist-changed!
  "Detect and persist only domains that changed since last snapshot.
   
   Arguments:
   - db: Current app-db value
   
   This is called on route change. Uses editscript to compute minimal diff."
  [db]
  (if-let [old-db @last-snapshot]
    (let [changed (find-changed-domains old-db db)]
      (when (seq changed)
        (doseq [domain changed] (save-domain! db domain))
        (reset! last-snapshot db)))
    (do (doseq [domain-id (keys cache-registry/domains)] (save-domain! db domain-id))
        (reset! last-snapshot db))))

(defn load-persisted
  "Load all persisted domains from cache.
   
   Returns a map that can be merged with initial-state.
   Returns empty map if version mismatch or no persisted data.
   
   Example return:
   {:current-route {:panel-id :home}
    :lang :pl}"
  []
  (if (check-version!)
    (reduce-kv (fn [acc _domain-id {:keys [key path]}]
                 (if-let [stored-value (cache/get-item key)]
                   (assoc-in acc path stored-value)
                   acc))
               {}
               cache-registry/domains)
    {}))

(def ops
  "Persistence operations implementation for port"
  {:persist-changed! persist-changed!
   :load-persisted load-persisted
   :save-domain! save-domain!})
