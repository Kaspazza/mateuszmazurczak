(ns mateuszmazurczak.domain.cache.registry
  "Registry of persistence domains - defines what parts of app-db to persist.
   
   Each domain specifies:
   - :key - localStorage key name (includes version)
   - :path - Path in app-db to extract/restore value
   
   Add new domains here when you need to persist additional state."
  (:require
   [mateuszmazurczak.domain.state.registry :as state-reg]))

(def version
  "App-db cache version. Increment when making breaking changes to app-db structure.
   
   IMPORTANT: This version ONLY affects app-db state (route, lang, theme).
   User data (votes, consents, solution IDs, admin key) uses separate versioning
   to prevent data loss on app-db structure changes."
  3)

(def user-data-version
  "User data cache version. Increment ONLY when making breaking changes to user data structure.
   
   CRITICAL: Incrementing this version will delete ALL user data:
   - Votes on solutions
   - User-uploaded solution IDs
   - User consents ('I've solved it')
   - Admin authentication key
   
   Before incrementing, consider migration strategies or ask users to export data."
  1)

(def version-cache-path
  "localStorage key for storing app-db version number"
  "mateuszmazurczak-cache-version")

(def user-data-version-cache-path
  "localStorage key for storing user-data version number"
  "mateuszmazurczak-user-data-version")

(def lang-cache-path (str "mateuszmazurczak-v" version "-lang"))
(def route-cache-path (str "mateuszmazurczak-v" version "-route"))
(def theme-cache-path (str "mateuszmazurczak-v" version "-theme"))

;; User data keys - versioned separately from app-db
(def aoc-votes-key (str "mateuszmazurczak-user-v" user-data-version "-aoc-votes"))
(def aoc-consents-key (str "mateuszmazurczak-user-v" user-data-version "-aoc-consents"))
(def aoc-solution-ids-key (str "mateuszmazurczak-user-v" user-data-version "-aoc-solution-ids"))
(def admin-key-storage-key (str "mateuszmazurczak-user-v" user-data-version "-admin-key"))

(def domains
  "Map of domain-id to persistence configuration for APP-DB state.
   
   Domain IDs are keywords used to reference specific parts of state.
   Each domain is persisted to a separate localStorage key for granularity.
   These are cleared when `version` is bumped.
   
   Example:
   {:route {:key 'mateuszmazurczak-v1-route'
            :path [:current-route]}}"
  {:route {:key route-cache-path
           :path state-reg/*current-route-path*}
   :lang {:key lang-cache-path
          :path state-reg/*lang-path*}
   :theme {:key theme-cache-path
           :path state-reg/*theme-path*}})

(def user-data-keys
  "User data localStorage keys that should NOT be cleared on app-db version bumps.
   
   These keys use separate versioning (user-data-version) to prevent data loss.
   They are managed by application/aoc/cache_service.cljs, not by state_persistence.cljs.
   
   CRITICAL: Only increment user-data-version when absolutely necessary.
   These contain important user data:
   - :aoc-votes - User votes on solutions
   - :aoc-consents - User consents ('I've solved it')
   - :aoc-solution-ids - Solutions uploaded by user
   - :admin-key - Admin authentication key"
  {:aoc-votes {:key aoc-votes-key}
   :aoc-consents {:key aoc-consents-key}
   :aoc-solution-ids {:key aoc-solution-ids-key}
   :admin-key {:key admin-key-storage-key}})

;; =============================================================================
;; Migrations
;; =============================================================================

(def migrations
  "App-db migration functions for version upgrades.
   
   Map structure: {target-version {domain-id migration-fn}}
   Migration function signature: (fn [old-value] new-value)
   
   Example:
   {2 {:lang (fn [old-lang]
               ;; Migrate from string to keyword
               (keyword old-lang))}
    3 {:route (fn [old-route]
                ;; Migrate route structure
                (assoc old-route :new-field true))}}"
  {;; Version 2 migrations (example - currently no migrations needed)
   ;; 2 {:lang identity}
   ;; Version 3 migrations (example - currently no migrations needed)
   ;; 3 {:route identity :theme identity}
  })

(def user-data-migrations
  "User data migration functions for version upgrades.
   
   Map structure: {target-version {key-id migration-fn}}
   Migration function signature: (fn [old-value] new-value)
   
   IMPORTANT: These migrations are CRITICAL for preserving user data.
   Test thoroughly before deploying. If migration fails, data is lost.
   
   Example:
   {2 {:aoc-votes (fn [old-votes]
                    ;; Migrate from vector to set
                    (set old-votes))}}"
  {;; Version 2 migrations (when we increment user-data-version to 2)
   ;; 2 {:aoc-votes identity
   ;;    :aoc-consents identity
   ;;    :aoc-solution-ids identity
   ;;    :admin-key identity}
  })

(defn get-old-key
  "Build the old cache key for a given version and domain.
   
   Used during migrations to read data from previous version's key."
  [old-version domain-id domain-type]
  (case domain-type
    :app-db (str "mateuszmazurczak-v" old-version "-" (name domain-id))
    :user-data (str "mateuszmazurczak-user-v" old-version "-" (name domain-id))
    nil))


