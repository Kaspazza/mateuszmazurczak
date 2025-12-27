(ns mateuszmazurczak.ports.cache
  "Cache port for persisting application data.
   
   Provides an abstraction over client-side storage mechanisms.
   Used for caching application state between sessions.
   
   On version mismatch or errors, cache is invalidated and rebuilt from scratch.")

(defonce ^{:private true
           :doc
           "The cache adapter instance with get-item, set-item!, remove-item!, clear! functions"}
         cache-adapter
  (atom nil))

(defn set-adapter!
  "Set the cache adapter implementation.
   
   Arguments:
   - adapter: Map with {:get-item fn :set-item! fn :remove-item! fn :clear! fn}"
  [adapter]
  (reset! cache-adapter adapter))

(defn get-item
  "Get item from cache by key.
   
   Arguments:
   - key: Cache key (string)
   
   Returns: Deserialized value or nil if not found/error"
  [key]
  (when-let [adapter @cache-adapter] ((:get-item adapter) key)))

(defn set-item!
  "Set item in cache.
   
   Arguments:
   - key: Cache key (string)
   - value: Any serializable value
   
   Returns: true on success, false on failure (quota exceeded, etc.)"
  [key value]
  (when-let [adapter @cache-adapter] ((:set-item! adapter) key value)))

(defn remove-item!
  "Remove item from cache by key.
   
   Arguments:
   - key: Cache key (string)"
  [key]
  (when-let [adapter @cache-adapter] ((:remove-item! adapter) key)))

(defn clear!
  "Clear all items from cache.
   
   Warning: This removes ALL items in cache."
  []
  (when-let [adapter @cache-adapter] ((:clear! adapter))))

;; =============================================================================
;; Persistence Operations
;; =============================================================================

(defonce ^{:private true
           :doc "Persistence operations: persist-changed!, load-persisted, save-domain!"}
         persistence-ops
  (atom nil))

(defn set-persistence-ops!
  "Set persistence operations implementation.
   
   Arguments:
   - ops: Map with {:persist-changed! fn :load-persisted fn :save-domain! fn}"
  [ops]
  (reset! persistence-ops ops))

(defn persist-changed!
  "Persist changed domains from app-db.
   
   Detects which domains changed since last snapshot,
   then saves only those domains.
   
   Arguments:
   - db: Current app-db value
   
   This is typically called on route change."
  [db]
  (when-let [ops @persistence-ops] ((:persist-changed! ops) db)))

(defn load-persisted
  "Load all persisted domains from cache.
   
   Arguments:
   - logger: Logger instance for structured logging during migration
   
   Returns a map that can be merged with initial-state.
   Returns empty map if version mismatch or no persisted data.
   
   Example return:
   {:current-route {...}
    :lang :pl}"
  [logger]
  (if-let [ops @persistence-ops]
    ((:load-persisted ops) logger)
    {}))

(defn save-domain!
  "Explicitly save a specific domain immediately (bypass change detection).
   
   Arguments:
   - db: Current app-db value
   - domain-id: Keyword identifying the domain (e.g., :route)
   
   Returns: true on success, false on failure
   
   Use this for critical saves that shouldn't wait for route change.
   Example: (cache/save-domain! @re-frame.db/app-db :route)"
  [db domain-id]
  (when-let [ops @persistence-ops] ((:save-domain! ops) db domain-id)))
