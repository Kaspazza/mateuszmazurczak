(ns mateuszmazurczak.domain.cache.registry
  "Registry of persistence domains - defines what parts of frontend db to persist.
   
   Each domain specifies:
   - :key - localStorage key name (includes version)
   - :path - Path in app-db to extract/restore value"
  (:require
   [mateuszmazurczak.domain.state.registry :as state-reg]))

(def version-cache-path
  "localStorage key for storing app-db version number"
  "mateuszmazurczak-cache-version")

(def user-data-version-cache-path
  "localStorage key for storing user-data version number"
  "mateuszmazurczak-user-data-version")

(def app-db-version
  "App-db cache version. Increment when making breaking changes to app-db synced domains"
  3)

(def user-data-version
  "User data cache version. Increment ONLY when making breaking changes to user data structure"
  1)

(def domains
  "Registry of all persistence domains - defines what gets cached to localStorage.
   
   Structured by version-key (:app-db or :user-data) for clear separation:
   
   :app-db domains:
   - Automatically synced with app-db state
   - Have :path (location in app-db)
   - Cleared when app-db-version changes
   
   :user-data domains:
   - Manually managed via cache-service
   - No :path (not in app-db)
   - Preserved across app-db version changes
   
   Each domain specifies:
   - :key - localStorage key name (includes version)
   - :path - (optional, app-db only) Path in app-db to extract/restore value
   
   Example:
   {:app-db {:route {:key \"mateuszmazurczak-v3-route\"
                     :path [:current-route]}}
    :user-data {:aoc-votes {:key \"mateuszmazurczak-user-v1-aoc-votes\"}}}"
  {:app-db {:route {:key (str "mateuszmazurczak-v" app-db-version "-route")
                    :path state-reg/*current-route-path*}
            :lang {:key (str "mateuszmazurczak-v" app-db-version "-lang")
                   :path state-reg/*lang-path*}
            :theme {:key (str "mateuszmazurczak-v" app-db-version "-theme")
                    :path state-reg/*theme-path*}}
   :user-data {:aoc-votes {:key (str "mateuszmazurczak-user-v" user-data-version "-aoc-votes")}
               :aoc-consents {:key (str "mateuszmazurczak-user-v" user-data-version "-aoc-consents")}
               :aoc-solution-ids {:key (str "mateuszmazurczak-user-v" user-data-version "-aoc-solution-ids")}
               :admin-key {:key (str "mateuszmazurczak-user-v" user-data-version "-admin-key")}}})

(def migrations
  "Migration functions for version upgrades.
   
   Map structure: {version-key {target-version {domain-id migration-fn}}}
   - version-key: :app-db or :user-data
   - target-version: Version number to migrate to
   - domain-id: Domain identifier from registry
   - migration-fn: (fn [old-value] new-value)
   
   Example:
   {:app-db {2 {:lang (fn [old-lang] (keyword old-lang))}}
    :user-data {2 {:aoc-votes (fn [old-votes] (set old-votes))}}}"
  {:app-db {;; Version 2 migrations (example - currently no migrations needed)
            ;; 2 {:lang identity}
            ;; Version 3 migrations (example - currently no migrations needed)
            ;; 3 {:route identity :theme identity}
           }
   :user-data {;; Version 2 migrations (when we increment user-data-version to 2)
               ;; 2 {:aoc-votes identity
               ;;    :aoc-consents identity
               ;;    :aoc-solution-ids identity
               ;;    :admin-key identity}
              }})


