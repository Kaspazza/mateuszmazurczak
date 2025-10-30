(ns mateuszmazurczak.domain.database.migrations
  "Database migrations namespace - defines migration structure and registry."
  ;;TODO this is not really domain, to think about where to place it
  (:require
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

;; Malli schemas for validation
(def ^:private migration-id-pattern #"^20\d{6}-\d{6}-[a-z0-9-]+$")

(def MigrationId
  "Schema for migration ID format: YYYYMMDD-HHMMSS-description"
  [:re migration-id-pattern])

(def MigrationFunction
  "Schema for migration up/down functions"
  [:fn (fn [f] (and (fn? f) (= 2 (.. ^clojure.lang.AFunction f getRequiredArity))))])

(def Migration
  "Schema for a single migration map"
  [:map {:closed true}
   [:migration/id MigrationId]
   [:migration/description
    [:string {:min 1
              :max 200}]]
   [:migration/up MigrationFunction]
   [:migration/down MigrationFunction]
   [:migration/checksum [:int {:min 0}]]])

(def ^:private MigrationRegistry "Schema for the entire migration registry" [:sequential Migration])



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

;; Migration registry - chronologically ordered
(def migrations
  "Registry of all database migrations in chronological order.
   Each migration should have a unique ID in format: YYYYMMDD-HHMMSS-description"
  [;; Examples of different migration patterns:
   ;; 1. Simple attribute addition (works with both Datomic and Datalevin)
   #_(create-migration "20241201-120000-add-comment-reactions"
                       "Add reaction support to comments"
                       (fn [conn _logger]
                         ;; Return transaction data - will use d/transact!
                         [{:db/ident :comment/reactions
                           :db/valueType :db.type/string
                           :db/doc "JSON string of reaction types to counts"}])
                       (fn [conn _logger]
                         (throw (ex-info "Cannot rollback schema additions"
                                         {:migration-id "20241201-120000-add-comment-reactions"}))))
   ;; 2. Datalevin-specific: Using update-schema for complex changes
   #_(create-migration "20241202-130000-rename-comment-content"
                       "Rename comment/content to comment/text for better naming"
                       (fn [conn _logger]
                         ;; Return map for d/update-schema - Datalevin only
                         {:schema-update {:comment/text {:doc "Comment text content"
                                                         :attr :string}}
                          :rename-map {:comment/content :comment/text}})
                       (fn [conn _logger]
                         ;; Reverse the rename
                         {:rename-map {:comment/text :comment/content}}))
   ;; 3. Data migration with schema change
   #_(create-migration
      "20241203-140000-normalize-author-names"
      "Split author names into first/last"
      (fn [conn _logger]
        ;; First add new attributes
        (d/update-schema conn
                         {:author/first-name {:attr :string}
                          :author/last-name {:attr :string}})
        ;; Then migrate existing data
        (let [authors (d/q '[:find ?e ?name :where [?e :author/name ?name]] (d/db conn))]
          (doseq [[author-id full-name] authors]
            (let [[first-name last-name] (clojure.string/split full-name #"\s+" 2)]
              (d/transact! conn
                           [[:db/add author-id :author/first-name (or first-name "")]
                            [:db/add author-id :author/last-name (or last-name "")]]))))
        ;; Return nil since we handled everything
        nil)
      (fn [conn _logger]
        (throw (ex-info "Cannot rollback data migration"
                        {:migration-id "20241203-140000-normalize-author-names"}))))
   ;; TODO I will need to choose one schema for migrations to be analyzed and applied by adapters, but that will be done when I change the schema
  ])

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
       (catch #?(:clj Exception
                 :cljs :default)
         e
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
       (catch #?(:clj Exception
                 :cljs :default)
         e
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
       (catch #?(:clj Exception
                 :cljs :default)
         e
         (throw (ex-info "Migration registry validation failed"
                         {:type ::registry-validation-error
                          :total-migrations (count migrations)
                          :cause e}
                         e)))))

(defn create-migration
  "Create a migration map with required metadata.
   Uses Malli for comprehensive validation."
  [id description up-fn down-fn]
  (let [migration {:migration/id id
                   :migration/description description
                   :migration/up up-fn
                   :migration/down down-fn
                   :migration/checksum (hash (str up-fn down-fn))}]
    (try (validation/validate-data Migration migration "migration creation")
         migration
         (catch #?(:clj Exception
                   :cljs :default)
           e
           (throw (ex-info "Failed to create migration"
                           {:type ::create-migration-error
                            :migration-id id
                            :migration migration
                            :cause e}
                           e))))))



