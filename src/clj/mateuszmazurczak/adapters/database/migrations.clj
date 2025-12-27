(ns mateuszmazurczak.adapters.database.migrations
  "Database migrations for Datalevin adapter"
  (:require
   [datalevin.core                    :as d]
   [mateuszmazurczak.domain.pages.aoc :as aoc-domain]
   [mateuszmazurczak.utils.validation :as validation]))

(def migration-schema
  "Schema for tracking applied migrations in the database"
  {:migration/id {:doc "Unique migration identifier (timestamp + description)"
                  :attr :string
                  :unique :identity}
   :migration/applied-at {:doc "Timestamp when migration was applied"
                          :attr :instant}
   :migration/checksum {:doc "MD5 hash of migration content for integrity check"
                        :attr :string}})

(def ^:private migration-id-pattern #"^20\d{6}-\d{6}-[a-z0-9-]+$")

(def MigrationId
  "Schema for migration ID format: YYYYMMDD-HHMMSS-description"
  [:re migration-id-pattern])

(def Migration
  "Schema for a single migration map"
  [:map {:closed true}
   [:migration/id MigrationId]
   [:migration/description
    [:string {:min 1
              :max 200}]]
   [:migration/up fn?]
   [:migration/down fn?]
   [:migration/checksum :string]])

(def ^:private MigrationRegistry "Schema for the entire migration registry" [:sequential Migration])

(defn create-migration
  "Create a migration map with required metadata.
   Uses Malli for comprehensive validation."
  [id description up-fn down-fn]
  (let [migration {:migration/id id
                   :migration/description description
                   :migration/up up-fn
                   :migration/down down-fn
                   ;; Generate string checksum from hash
                   :migration/checksum (str (hash (str up-fn down-fn)))}]
    (try (validation/validate-data Migration migration "migration creation")
         migration
         (catch Exception e
           (throw (ex-info "Failed to create migration"
                           {:type ::create-migration-error
                            :migration-id id
                            :migration migration
                            :cause e}
                           e))))))

(def migrations
  "Registry of all database migrations in chronological order.
   Each migration should have a unique ID in format: YYYYMMDD-HHMMSS-description"
  [(create-migration
    "20241206-000000-rename-github-profile-to-username"
    "Rename aoc-solution/github-profile to aoc-solution/github-username and parse URLs to usernames"
    (fn [conn _logger]
      (let [solutions (d/q '[:find ?e ?profile :where [?e :aoc-solution/github-profile ?profile]]
                           @conn)
            migrate-tx
            (mapv (fn [[eid profile-url]]
                    (let [username (aoc-domain/parse-github-username profile-url)]
                      (cond-> [[:db/retract eid :aoc-solution/github-profile profile-url]]
                        username (conj [:db/add eid :aoc-solution/github-username username]))))
                  solutions)
            flattened-tx (apply concat migrate-tx)]
        (when (seq flattened-tx) (d/transact! conn flattened-tx))
        nil))
    ;; Rollback is not done as Datalevin is embedded DB - can restore from backup if needed and it was done for specific data, and with not a lot of data it's easy to manage like that
    (fn [_conn _logger]
      (throw (ex-info "Rollback prohibited: Migration is one-way (URL → username parsing is lossy)"
                      {:migration-id "20241206-000000-rename-github-profile-to-username"
                       :rollback-prohibited true
                       :reason "Original GitHub URLs are not preserved during parsing"
                       :mitigation "Restore from database backup if rollback needed"}))))])

(defn- validate-migration-id-chronology
  "Validate that migration IDs are in chronological order."
  [migrations]
  (let [ids (map :migration/id migrations)
        sorted-ids (sort ids)]
    (when-not (= ids sorted-ids)
      (throw (ex-info "Migrations must be in chronological order"
                      {:type ::chronology-error
                       :expected-order sorted-ids
                       :actual-order ids})))))

(defn get-pending-migrations
  "Get list of migrations that haven't been applied yet.
   Validates inputs and ensures registry integrity."
  [applied-migration-ids]
  (when-not (coll? applied-migration-ids)
    (throw (ex-info "Applied migration IDs must be a collection"
                    {:type ::invalid-applied-ids
                     :provided applied-migration-ids})))
  (when-not (every? string? applied-migration-ids)
    (throw (ex-info "All migration IDs must be strings"
                    {:type ::invalid-migration-id-types
                     :provided applied-migration-ids})))
  (try (validation/validate-data MigrationRegistry migrations "migration registry")
       (validate-migration-id-chronology migrations)
       (let [applied-set (set applied-migration-ids)]
         (remove (fn [migration] (contains? applied-set (:migration/id migration))) migrations))
       (catch Exception e
         (throw (ex-info "Failed to get pending migrations"
                         {:type ::get-pending-migrations-error
                          :cause e
                          :applied-migration-ids applied-migration-ids}
                         e)))))

(defn validate-migration-integrity
  "Validate that applied migrations match their expected checksums.
   Also validates input data structure."
  [applied-migrations registry-migrations]
  (when-not (coll? applied-migrations)
    (throw (ex-info "Applied migrations must be a collection"
                    {:type ::invalid-applied-migrations
                     :provided applied-migrations})))
  (when-not (coll? registry-migrations)
    (throw (ex-info "Registry migrations must be a collection"
                    {:type ::invalid-registry-migrations
                     :provided registry-migrations})))
  (try (doseq [applied applied-migrations]
         (when-not (and (:migration/id applied) (:migration/checksum applied))
           (throw (ex-info "Invalid applied migration structure"
                           {:type ::invalid-applied-migration
                            :migration applied}))))
       (doseq [registry registry-migrations]
         (when-not (and (:migration/id registry) (:migration/checksum registry))
           (throw (ex-info "Invalid registry migration structure"
                           {:type ::invalid-registry-migration
                            :migration registry}))))
       (let [registry-by-id (group-by :migration/id registry-migrations)
             applied-by-id (group-by :migration/id applied-migrations)
             mismatches (atom [])]
         (doseq [[id applied-list] applied-by-id]
           (when-let [registry-migration (first (get registry-by-id id))]
             (let [applied-checksum (:migration/checksum (first applied-list))
                   expected-checksum (:migration/checksum registry-migration)]
               (when (not= applied-checksum expected-checksum)
                 (swap! mismatches conj
                   {:migration-id id
                    :applied-checksum applied-checksum
                    :expected-checksum expected-checksum})))))
         (when (seq @mismatches)
           (throw (ex-info "Migration integrity check failed - checksum mismatches detected"
                           {:type ::integrity-check-failed
                            :mismatches @mismatches
                            :total-mismatches (count @mismatches)}))))
       (catch Exception e
         (if (= (:type (ex-data e)) ::integrity-check-failed)
           (throw e)
           (throw (ex-info "Error during migration integrity validation"
                           {:type ::integrity-validation-error
                            :cause e
                            :applied-count (count applied-migrations)
                            :registry-count (count registry-migrations)}
                           e))))))

(defn validate-migration-registry!
  "Validate the entire migration registry for structural integrity.
   Throws descriptive errors for any issues found."
  []
  (try (validation/validate-data MigrationRegistry migrations "migration registry")
       (validate-migration-id-chronology migrations)
       (let [ids (map :migration/id migrations)
             unique-ids (set ids)]
         (when (not= (count ids) (count unique-ids))
           (let [duplicates (->> ids
                                 (frequencies)
                                 (filter #(> (second %) 1))
                                 (map first))]
             (throw (ex-info "Duplicate migration IDs found"
                             {:type ::duplicate-migration-ids
                              :duplicates duplicates})))))
       (let [checksums (map :migration/checksum migrations)
             unique-checksums (set checksums)]
         (when (not= (count checksums) (count unique-checksums))
           (let [duplicate-checksums (->> checksums
                                          (frequencies)
                                          (filter #(> (second %) 1))
                                          (map first))]
             (throw (ex-info "Duplicate migration checksums found - possible copy-paste error"
                             {:type ::duplicate-migration-checksums
                              :duplicate-checksums duplicate-checksums})))))
       (catch Exception e
         (throw (ex-info "Migration registry validation failed"
                         {:type ::registry-validation-error
                          :total-migrations (count migrations)
                          :cause e}
                         e)))))
