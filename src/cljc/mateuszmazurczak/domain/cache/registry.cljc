(ns mateuszmazurczak.domain.cache.registry
  "Registry of persistence domains - defines what parts of frontend db to persist.
   
   Each domain specifies:
   - :key - localStorage key name (includes version)
   - :path - Path in app-db to extract/restore value"
  (:require
   [mateuszmazurczak.domain.state.registry :as state-reg]))

(def registry
  "Registry of all persistence version-keys and their domains.
   
   Each version-key (e.g., :app-db, :user-data) defines:
   - :version - Current version number (increment on breaking changes)
   - :version-cache-path - localStorage key for version tracking
   - :domains - Map of domain-id to domain config
   
   Domain types:
   
   :app-db domains:
   - Automatically synced with app-db state
   - Have :path (location in app-db)
   - Cleared when version changes
   
   :user-data domains:
   - Manually managed via cache-service
   - No :path (not in app-db)
   - Preserved across app-db version changes
   
   Each domain specifies:
   - :key - localStorage key name (includes version)
   - :path - (optional, app-db only) Path in app-db to extract/restore value
   
   Adding a new version-key (e.g., :qr-data):
   Just add it to this registry and all persistence code automatically works:
   {:qr-data {:version 1
              :version-cache-path \"mateuszmazurczak-qr-data-version\"
              :domains {:qr-codes {:key \"mateuszmazurczak-qr-v1-codes\"}}}}"
  (let [app-db-version 3
        user-data-version 2]
    {:app-db {:version app-db-version
              :version-cache-path "mateuszmazurczak-cache-version"
              :domains {:route {:key (str "mateuszmazurczak-v" app-db-version "-route")
                                :path state-reg/*current-route-path*}
                        :lang {:key (str "mateuszmazurczak-v" app-db-version "-lang")
                               :path state-reg/*lang-path*}
                        :theme {:key (str "mateuszmazurczak-v" app-db-version "-theme")
                                :path state-reg/*theme-path*}}}
     :user-data
     {:version user-data-version
      :version-cache-path "mateuszmazurczak-user-data-version"
      :domains
      {:aoc-votes {:key (str "mateuszmazurczak-user-v" user-data-version "-aoc-votes")}
       :aoc-consents {:key (str "mateuszmazurczak-user-v" user-data-version "-aoc-consents")}
       :aoc-solution-ids {:key
                          (str "mateuszmazurczak-user-v" user-data-version "-aoc-solution-ids")}
       :admin-key {:key (str "mateuszmazurczak-user-v" user-data-version "-admin-key")}}}}))

(def migrations
  "Migration functions for version upgrades.
   
   Map structure: {version-key {target-version {domain-id migration-fn}}}
   - version-key: :app-db, :user-data, or any version-key from registry
   - target-version: Version number to migrate to
   - domain-id: Domain identifier from registry
   - migration-fn: (fn [old-value] new-value"
  {:app-db {;; Version 2 migrations (example - currently no migrations needed)
            ;; 2 {:lang identity}
            ;; Version 3 migrations (example - currently no migrations needed)
            ;; 3 {:route identity :theme identity}
           }
   :user-data {;; Version 2: Migrate from unversioned keys to registry-based versioned keys
               ;; V1 had version tracking but data was still in old unversioned keys
               ;; (aoc-consents, aoc-votes, etc.) due to hardcoded key names in code.
               ;; This migration moves data from unversioned to v2 versioned keys.
               ;; The migration function returns identity since data structure is unchanged,
               ;; the actual key migration happens in state_persistence.cljs
               2 {:aoc-votes identity
                  :aoc-consents identity
                  :aoc-solution-ids identity
                  :admin-key identity}}})


