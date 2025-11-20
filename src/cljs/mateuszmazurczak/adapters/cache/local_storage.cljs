(ns mateuszmazurczak.adapters.cache.local-storage
  "LocalStorage adapter for cache port.
   
   Uses Transit for serialization to properly handle ClojureScript data structures
   (keywords, UUIDs, dates, etc.)."
  (:require
   [cognitect.transit :as transit]))

;; =============================================================================
;; Transit Serialization
;; =============================================================================

(def ^:private transit-writer "Transit writer instance for JSON format" (transit/writer :json))

(def ^:private transit-reader "Transit reader instance for JSON format" (transit/reader :json))

(defn- serialize
  "Serialize value to transit JSON string"
  [value]
  (transit/write transit-writer value))

(defn- deserialize
  "Deserialize transit JSON string to value"
  [string]
  (transit/read transit-reader string))

;; =============================================================================
;; LocalStorage Adapter Implementation
;; =============================================================================

(defn get-item
  "Get item from localStorage.
   
   Arguments:
   - key: Cache key (string)
   
   Returns: Deserialized value or nil if not found/error"
  [key]
  (try (when-let [stored (.getItem js/localStorage key)] (deserialize stored))
       (catch :default e (.warn js/console (str "Failed to load from localStorage: " key) e) nil)))

(defn set-item!
  "Set item in localStorage.
   
   Arguments:
   - key: Cache key (string)
   - value: Any serializable value
   
   Returns: true on success, false on failure"
  [key value]
  (try (let [serialized (serialize value)]
         (.setItem js/localStorage key serialized)
         true)
       (catch :default e (.warn js/console (str "Failed to save to localStorage: " key) e) false)))

(defn remove-item!
  "Remove item from localStorage.
   
   Arguments:
   - key: Cache key (string)"
  [key]
  (try (.removeItem js/localStorage key)
       (catch :default e (.warn js/console (str "Failed to remove from localStorage: " key) e))))

(defn clear!
  "Clear all items from localStorage."
  []
  (try (.clear js/localStorage)
       (catch :default e (.warn js/console "Failed to clear localStorage" e))))

;; =============================================================================
;; Adapter Instance
;; =============================================================================

(def adapter
  "LocalStorage adapter implementation"
  {:get-item get-item
   :set-item! set-item!
   :remove-item! remove-item!
   :clear! clear!})
