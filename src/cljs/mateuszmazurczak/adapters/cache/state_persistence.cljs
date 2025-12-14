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

(defn- migrate-domain
  "Migrate a single domain from old version to new version.
   
   Arguments:
   - domain-id: Keyword identifying the domain
   - old-version: Previous version number
   - target-version: Target version number
   - old-value: Value from old version
   
   Returns: Migrated value or nil if migration fails"
  [domain-id old-version target-version old-value]
  (try (loop [current-version (inc old-version)
              value old-value]
         (if (> current-version target-version)
           ;; Migration complete
           value
           ;; Apply migration for current-version if exists
           (if-let [migration-fn (get-in cache-registry/migrations [current-version domain-id])]
             (recur (inc current-version) (migration-fn value))
             ;; No migration defined - assume data structure is compatible
             (recur (inc current-version) value))))
       (catch :default e
         ;; Migration failed - log and return nil
         (js/console.error (str "Migration failed for domain " domain-id
                                " from v" old-version
                                " to v" target-version)
                           e)
         nil)))

(defn- check-version-and-migrate!
  "Check app-db version and migrate data if needed.
   
   Migration strategy:
   1. If versions match → return true (no migration needed)
   2. If old version < current → attempt migration
   3. If migration succeeds → save migrated data, update version, return true
   4. If migration fails OR old version > current → clear cache, return false
   
   Returns: true if version valid or migration succeeded, false if invalidated"
  []
  (let [stored-version (cache/get-item cache-registry/version-cache-path)
        current-version cache-registry/version]
    (cond
      ;; Version matches - no migration needed
      (= stored-version current-version) true
      ;; Old version - attempt migration
      (and stored-version (< stored-version current-version))
      (let [migration-success? (atom true)]
        (doseq [[domain-id {:keys [key]}] cache-registry/domains]
          (when-let [old-value (cache/get-item key)]
            (if-let [migrated-value
                     (migrate-domain domain-id stored-version current-version old-value)]
              ;; Migration succeeded - save to NEW key
              (cache/set-item! key migrated-value)
              ;; Migration failed - mark as failed
              (reset! migration-success? false))))
        (if @migration-success?
          (do (cache/set-item! cache-registry/version-cache-path current-version) true)
          ;; Migration failed - clear cache
          (do (doseq [[_domain-id {:keys [key]}] cache-registry/domains] (cache/remove-item! key))
              (cache/set-item! cache-registry/version-cache-path current-version)
              false)))
      ;; Version mismatch (downgrade or no stored version) - clear cache
      :else (do (doseq [[_domain-id {:keys [key]}] cache-registry/domains] (cache/remove-item! key))
                (cache/set-item! cache-registry/version-cache-path current-version)
                false))))

(defn- migrate-user-data
  "Migrate user data from old version to new version.
   
   Arguments:
   - key-id: Keyword identifying the user data (e.g., :aoc-votes)
   - old-version: Previous version number
   - target-version: Target version number
   - old-value: Value from old version
   
   Returns: Migrated value or nil if migration fails"
  [key-id old-version target-version old-value]
  (try (loop [current-version (inc old-version)
              value old-value]
         (if (> current-version target-version)
           ;; Migration complete
           value
           ;; Apply migration for current-version if exists
           (if-let [migration-fn (get-in cache-registry/user-data-migrations
                                         [current-version key-id])]
             (recur (inc current-version) (migration-fn value))
             ;; No migration defined - assume data structure is compatible
             (recur (inc current-version) value))))
       (catch :default e
         ;; CRITICAL: User data migration failed - log error
         (js/console.error (str "CRITICAL: User data migration failed for "
                                key-id
                                " from v"
                                old-version
                                " to v"
                                target-version
                                ". User data will be lost!")
                           e)
         nil)))

(defn- check-user-data-version-and-migrate!
  "Check user-data version and migrate data if needed.
   
   CRITICAL: User data contains important information (votes, consents, uploads).
   Migration failures result in data loss.
   
   Migration strategy:
   1. If versions match → return true (no migration needed)
   2. If old version < current → attempt migration
   3. If migration succeeds → save migrated data, update version, return true
   4. If migration fails OR old version > current → clear cache, return false
   
   Returns: true if version valid or migration succeeded, false if invalidated"
  []
  (let [stored-version (cache/get-item cache-registry/user-data-version-cache-path)
        current-version cache-registry/user-data-version]
    (cond
      ;; Version matches - no migration needed
      (= stored-version current-version) true
      ;; Old version - attempt migration
      (and stored-version (< stored-version current-version))
      (let [migration-success? (atom true)]
        (doseq [[key-id {:keys [key]}] cache-registry/user-data-keys]
          (when-let [old-value (cache/get-item key)]
            (if-let [migrated-value
                     (migrate-user-data key-id stored-version current-version old-value)]
              ;; Migration succeeded - save to NEW key
              (cache/set-item! key migrated-value)
              ;; Migration failed - mark as failed and log
              (do (js/console.error (str "User data migration failed for " key-id))
                  (reset! migration-success? false)))))
        (if @migration-success?
          (do (cache/set-item! cache-registry/user-data-version-cache-path current-version) true)
          ;; Migration failed - clear cache (data loss!)
          (do (js/console.warn "User data migration failed. Clearing user data.")
              (doseq [[_key-id {:keys [key]}] cache-registry/user-data-keys]
                (cache/remove-item! key))
              (cache/set-item! cache-registry/user-data-version-cache-path current-version)
              false)))
      ;; Version mismatch (downgrade or no stored version) - clear cache
      :else (do (doseq [[_key-id {:keys [key]}] cache-registry/user-data-keys]
                  (cache/remove-item! key))
                (cache/set-item! cache-registry/user-data-version-cache-path current-version)
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
  "Load all persisted domains from cache with automatic migration support.
   
   Migration flow:
   1. Check user-data version and migrate if needed (votes, consents, etc.)
   2. Check app-db version and migrate if needed (route, lang, theme)
   3. Load migrated data into app-db
   
   Migration strategy:
   - If old version < current version: attempt sequential migrations
   - If migration succeeds: data is preserved and updated
   - If migration fails: data is cleared (logged to console)
   
   Returns a map that can be merged with initial-state.
   Returns empty map if migration failed or no persisted data.
   
   Example return:
   {:current-route {:panel-id :home}
    :lang :pl}"
  []
  ;; Check and migrate user-data version first (independent of app-db version)
  (check-user-data-version-and-migrate!)
  ;; Then check and migrate app-db version and load domains
  (if (check-version-and-migrate!)
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
