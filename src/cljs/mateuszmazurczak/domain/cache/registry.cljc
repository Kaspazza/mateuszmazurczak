(ns mateuszmazurczak.domain.cache.registry
  "Registry of persistence domains - defines what parts of app-db to persist.
   
   Each domain specifies:
   - :key - localStorage key name (includes version)
   - :path - Path in app-db to extract/restore value
   
   Add new domains here when you need to persist additional state."
  (:require
   [mateuszmazurczak.domain.state.registry :as state-reg]))

(def version
  "Current cache version. Increment when making breaking changes to state structure.
   On version mismatch, all cached data is cleared and re-initialized."
  1)

(def domains
  "Map of domain-id to persistence configuration.
   
   Domain IDs are keywords used to reference specific parts of state.
   Each domain is persisted to a separate localStorage key for granularity.
   
   Example:
   {:route {:key 'mateuszmazurczak-v1-route'
            :path [:current-route]}}"
  {:route {:key (str "mateuszmazurczak-v" version "-route")
           :path state-reg/*current-route-path*}
   :lang {:key (str "mateuszmazurczak-v" version "-lang")
          :path state-reg/*lang-path*}
   :theme {:key (str "mateuszmazurczak-v" version "-theme")
           :path state-reg/*theme-path*}})

(def version-key "localStorage key for storing version number" "mateuszmazurczak-cache-version")
