(ns mateuszmazurczak.adapters.cache.state-persistence
  "State persistence layer - handles saving/loading app-db to/from cache.
   
   Uses editscript to efficiently detect which domains changed,
   then saves only changed domains to separate localStorage keys.
   
   This is a cache mechanism:
   - Backend is source of truth
   - On version mismatch, cache is invalidated
   - Gracefully handles errors (falls back to empty state)"
  (:require
   [clojure.string                              :as str]
   [editscript.core                             :as e]
   [mateuszmazurczak.domain.cache.registry      :as cache-registry]
   [mateuszmazurczak.ports.cache                :as cache]
   [mateuszmazurczak.ports.logging              :as log]
   [mateuszmazurczak.ui.components.notification :as notification]))

(defonce ^{:private true
           :doc "Last persisted app-db snapshot for diff comparison"}
         last-snapshot
  (atom nil))

(defn- migrate-domain
  "Migrate a single domain from old version to new version.
   
   Arguments:
   - version-key: :app-db or :user-data
   - domain-id: Keyword identifying the domain
   - old-version: Previous version number
   - target-version: Target version number
   - old-value: Value from old version
   - logger: Logger instance for structured logging
   
   Returns: Migrated value or nil if migration fails"
  [version-key domain-id old-version target-version old-value logger]
  (try (loop [current-version (inc old-version)
              value old-value]
         (if (> current-version target-version)
           value
           (if-let [migration-fn (get-in cache-registry/migrations
                                         [version-key current-version domain-id])]
             (recur (inc current-version) (migration-fn value))
             (recur (inc current-version) value))))
       (catch :default e
         (let [critical? (= version-key :user-data)]
           (log/log! logger
                     {:level (if critical? :error :warn)
                      :id ::migration-failed-domain
                      :msg "Domain migration failed"
                      :data {:version-key version-key
                             :domain-id domain-id
                             :from-version old-version
                             :to-version target-version
                             :critical? critical?
                             :error (ex-message e)}})
           (when critical?
             (notification/show-error
              "Data migration failed"
              {:description
               (str
                "Your saved data (votes, solutions) could not be migrated. "
                "This may be due to a browser update. "
                "All your posted solutions and votes are safe, just cosmetic data may be lost.")})))
         nil)))

(defn- migrate-from-legacy-keys!
  "One-time migration from old unversioned localStorage keys to new versioned keys.
   
   This handles the transition from the old key naming scheme to the new versioned scheme.
   Uses current registry to determine target key names (works with any version).
   
   Old keys → New keys (current version):
   - \"aoc-consents\" → current registry key for :aoc-consents
   - \"aoc-votes\" → current registry key for :aoc-votes
   - \"aoc-solution-ids\" → current registry key for :aoc-solution-ids
   - \"admin-key\" → current registry key for :admin-key
   
   Returns: map with :migrated? boolean and :migrated-keys vector of old-key names"
  []
  (let [legacy-mappings
        {"aoc-consents" (get-in cache-registry/registry [:user-data :domains :aoc-consents :key])
         "aoc-votes" (get-in cache-registry/registry [:user-data :domains :aoc-votes :key])
         "aoc-solution-ids" (get-in cache-registry/registry
                                    [:user-data :domains :aoc-solution-ids :key])
         "admin-key" (get-in cache-registry/registry [:user-data :domains :admin-key :key])}
        migrated-keys (atom [])]
    (doseq [[old-key new-key] legacy-mappings]
      (when-let [old-value (cache/get-item old-key)]
        (cache/set-item! new-key old-value)
        (cache/remove-item! old-key)
        (swap! migrated-keys conj old-key)))
    (let [result {:migrated? (seq @migrated-keys)
                  :migrated-keys @migrated-keys}]
      (when (:migrated? result)
        (notification/show-success "Data migrated"
                                   {:description
                                    "Your saved data has been migrated to the new format"}))
      result)))

(defn- check-version-and-migrate!
  "Check version and migrate data for a specific version-key.
   
   Arguments:
   - version-key: :app-db or :user-data
   - stored-version-path: localStorage key for version tracking
   - current-version: Current version number
   - logger: Logger instance for structured logging
   
   Migration strategy:
   1. If versions match → return {:valid? true}
   2. If old version < current → attempt migration
   3. If migration succeeds → return {:valid? true, :migrated? true}
   4. If migration fails OR old version > current → return {:valid? false}
   5. If no stored version AND version-key is :user-data → attempt legacy migration
   
   Returns: map with :valid? boolean, optional :migrated? and :legacy-migration details"
  [version-key stored-version-path current-version logger]
  (let [stored-version (cache/get-item stored-version-path)
        domains (get-in cache-registry/registry [version-key :domains])]
    (log/log! logger
              {:level :info
               :id ::version-check
               :msg "Checking cache version"
               :data {:version-key version-key
                      :stored-version stored-version
                      :current-version current-version}})
    (cond
      ;; Version matches - no migration needed
      (= stored-version current-version) (do (log/log!
                                              logger
                                              {:level :info
                                               :id ::version-match
                                               :msg "Cache version matches - no migration needed"
                                               :data {:version-key version-key
                                                      :version current-version}})
                                             {:valid? true})
      ;; Old version - attempt migration
      (and stored-version (< stored-version current-version))
      (let [migration-success? (atom true)
            legacy-result (atom nil)]
        (log/log! logger
                  {:level :info
                   :id ::migrating-version
                   :msg "Migrating cache to new version"
                   :data {:version-key version-key
                          :from stored-version
                          :to current-version}})
        ;; Special case: user-data v1→v2 requires legacy key migration
        ;; because v1 had version tracking but code used hardcoded key names,
        ;; so data was still in old unversioned keys (aoc-consents, aoc-votes, etc.)
        (when (and (= version-key :user-data) (= stored-version 1) (= current-version 2))
          (log/log! logger
                    {:level :info
                     :id ::legacy-key-migration
                     :msg "v1→v2 migration requires legacy key migration"})
          (reset! legacy-result (migrate-from-legacy-keys!))
          (when (:migrated? @legacy-result)
            (log/log! logger
                      {:level :info
                       :id ::legacy-keys-migrated
                       :msg "Legacy keys migrated successfully"
                       :data {:keys (:migrated-keys @legacy-result)}})))
        (doseq [[domain-id {:keys [key]}] domains]
          (let [old-key (str/replace-first key
                                           (str "-v" current-version "-")
                                           (str "-v" stored-version "-"))]
            (when-let [old-value (cache/get-item old-key)]
              (if-let [migrated-value (migrate-domain version-key
                                                      domain-id
                                                      stored-version
                                                      current-version
                                                      old-value
                                                      logger)]
                (do (cache/set-item! key migrated-value) (cache/remove-item! old-key))
                (reset! migration-success? false)))))
        (if @migration-success?
          (do (cache/set-item! stored-version-path current-version)
              {:valid? true
               :migrated? true
               :legacy-migration @legacy-result})
          (do (log/log! logger
                        {:level :warn
                         :id ::migration-failed
                         :msg "Cache migration failed - clearing cache"
                         :data {:version-key version-key}})
              (when (= version-key :user-data)
                (notification/show-warning "Cache cleared"
                                           {:description
                                            "Unable to migrate saved data. Starting fresh."}))
              (doseq [[_domain-id {:keys [key]}] domains] (cache/remove-item! key))
              (cache/set-item! stored-version-path current-version)
              {:valid? false})))
      (and (nil? stored-version) (= version-key :user-data))
      (do (log/log! logger
                    {:level :info
                     :id ::no-version-legacy-migration
                     :msg "No stored version - attempting legacy key migration"})
          (let [legacy-result (migrate-from-legacy-keys!)]
            (when (:migrated? legacy-result)
              (log/log! logger
                        {:level :info
                         :id ::legacy-keys-migrated
                         :msg "Legacy keys migrated successfully"
                         :data {:keys (:migrated-keys legacy-result)}}))
            (cache/set-item! stored-version-path current-version)
            {:valid? true
             :legacy-migration legacy-result}))
      :else
      (do (log/log! logger
                    {:level :warn
                     :id ::version-mismatch
                     :msg "Cache version mismatch"
                     :data {:version-key version-key
                            :stored-version stored-version
                            :current-version current-version}})
          (let [legacy-result
                (when (and (= version-key :user-data) (= stored-version 3) (= current-version 2))
                  (log/log! logger
                            {:level :info
                             :id ::downgrade-legacy-migration
                             :msg "Downgrade detected - attempting legacy migration"})
                  (migrate-from-legacy-keys!))]
            (when (:migrated? legacy-result)
              (log/log! logger
                        {:level :info
                         :id ::legacy-keys-migrated
                         :msg "Legacy keys migrated during downgrade"
                         :data {:keys (:migrated-keys legacy-result)}}))
            (doseq [[_domain-id {:keys [key]}] domains] (cache/remove-item! key))
            (cache/set-item! stored-version-path current-version)
            {:valid? false
             :legacy-migration legacy-result})))))



(defn save-domain!
  "Save a single app-db synced domain to cache.
   
   Arguments:
   - db: Current app-db value
   - domain-id: Keyword identifying the domain (from registry)
   
   Returns: true on success, false on failure
   
   Note: Only works for domains with :path (app-db synced domains)"
  [db domain-id]
  (if-let [{:keys [key path]} (get-in cache-registry/registry [:app-db :domains domain-id])]
    (let [value (get-in db path)] (if value (cache/set-item! key value) false))
    false))

(defn- find-changed-domains
  "Use editscript to detect which app-db synced domains changed.
   
   Arguments:
   - old-db: Previous app-db snapshot
   - new-db: Current app-db value
   
   Returns: Set of domain-ids that changed
   
   Note: Only checks domains with :path (app-db synced domains)"
  [old-db new-db]
  (reduce-kv (fn [changed domain-id {:keys [path]}]
               (let [old-val (get-in old-db path)
                     new-val (get-in new-db path)
                     diff (e/diff old-val new-val)]
                 (if (seq (e/get-edits diff)) (conj changed domain-id) changed)))
             #{}
             (get-in cache-registry/registry [:app-db :domains])))

(defn persist-changed!
  "Detect and persist only app-db synced domains that changed since last snapshot.
   
   Arguments:
   - db: Current app-db value
   
   This is called on route change. Uses editscript to compute minimal diff.
   
   Note: Only persists domains with :path (app-db synced domains)
   "
  [db]
  (let [app-db-domains (keys (get-in cache-registry/registry [:app-db :domains]))
        old-db @last-snapshot]
    (reset! last-snapshot db)
    (if old-db
      (let [changed (find-changed-domains old-db db)]
        (when (seq changed) (doseq [domain changed] (save-domain! db domain))))
      (doseq [domain-id app-db-domains] (save-domain! db domain-id)))))

(defn load-persisted
  "Load all persisted domains from cache with automatic migration support.
   
   Arguments:
   - logger: Logger instance for structured logging
   
   This function automatically processes ALL version-keys in the registry.
   When you add a new version-key (e.g., :qr-data) to the registry,
   this function will automatically:
   1. Check its version and migrate if needed
   2. Load domains with :path into app-db
   
   Migration strategy:
   - If old version < current version: attempt sequential migrations
   - If migration succeeds: data is preserved and updated
   - If migration fails: data is cleared (logged via logger)
   
   Returns a map that can be merged with initial-state.
   Returns empty map if migration failed or no persisted data.
   
   Example return:
   {:current-route {:panel-id :home}
    :lang :pl}"
  [logger]
  (reduce-kv (fn [acc version-key {:keys [version version-cache-path domains]}]
               (let [migration-result
                     (check-version-and-migrate! version-key version-cache-path version logger)]
                 (if (:valid? migration-result)
                   (reduce-kv (fn [inner-acc _domain-id {:keys [key path]}]
                                (if path
                                  (if-let [stored-value (cache/get-item key)]
                                    (assoc-in inner-acc path stored-value)
                                    inner-acc)
                                  inner-acc))
                              acc
                              domains)
                   acc)))
             {}
             cache-registry/registry))

(def ops
  "Persistence operations implementation for port"
  {:persist-changed! persist-changed!
   :load-persisted load-persisted
   :save-domain! save-domain!})
