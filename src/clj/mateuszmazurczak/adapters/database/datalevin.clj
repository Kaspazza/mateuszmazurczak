(ns mateuszmazurczak.adapters.database.datalevin
  "Datalevin adapter implementation for database operations."
  (:require
   [datalevin.core                              :as d]
   [malli.core                                  :as m]
   [mateuszmazurczak.adapters.database.utils    :as db-utils]
   [mateuszmazurczak.domain.database.migrations :as migrations]
   [mateuszmazurczak.domain.database.schema     :as schema]
   [mateuszmazurczak.ports.logging              :as log])
  (:import [java.time Instant]))

(defn- build-datalevin-schema
  "Convert our schema format to Datalevin schema format."
  []
  (let [attrs (apply merge schema/entities)]
    (reduce-kv
     (fn [acc attr-kw attr-def]
       (let [datalevin-attr
             (cond-> {}
               (:attr attr-def) (assoc :db/valueType
                                       (case (:attr attr-def)
                                         :keyword :db.type/keyword
                                         :string :db.type/string
                                         :uuid :db.type/uuid
                                         :instant :db.type/instant
                                         :db.type/string))
               (:enum attr-def) (assoc :db/valueType :db.type/keyword)
               (:ref attr-def) (assoc :db/valueType :db.type/ref)
               (:ref-many attr-def) (assoc :db/valueType :db.type/ref
                                           :db/cardinality :db.cardinality/many)
               (:unique attr-def) (assoc :db/unique
                                         (case (:unique attr-def)
                                           :identity :db.unique/identity
                                           :value :db.unique/value
                                           :db.unique/value))
               (:index attr-def) (assoc :db/index true)
               (:doc attr-def) (assoc :db/doc (:doc attr-def)))]
         (assoc acc attr-kw datalevin-attr)))
     {}
     attrs)))



(defn- connect-db
  [url]
  (let [schema (build-datalevin-schema)
        conn (d/get-conn url schema)]
    ;; Datalevin automatically handles schema evolution via d/get-conn
    ;; For complex schema changes, use d/update-schema as needed
    conn))

(defn start
  "Start Datalevin database connection with retry mechanism."
  [{:keys [uri logger]}]
  (try (db-utils/retry 5 1000 (partial connect-db uri) logger)
       (catch Exception e
         (log/error! logger
                     {:error e
                      :id ::database-start-failed
                      :data {:uri uri}})
         (throw (ex-info "Unable to start Datalevin database"
                         {:type ::database-start-failed
                          :uri uri
                          :cause e}
                         e)))))

(defn stop "Stop Datalevin database connection." [conn] (d/close conn))

(defn transact!
  "Execute transaction on Datalevin database."
  [conn tx-data]
  {:pre [conn (coll? tx-data)]}
  (try (d/transact! conn tx-data)
       (catch Exception e
         (throw (ex-info "Transaction failed"
                         {:type ::transaction-failed
                          :tx-data tx-data
                          :cause e}
                         e)))))

(defn query
  "Execute query on Datalevin database."
  [conn query & args]
  {:pre [conn query]}
  (try (apply d/q query (d/db conn) args)
       (catch Exception e
         (throw (ex-info "Query failed"
                         {:type ::query-failed
                          :query query
                          :args args
                          :cause e}
                         e)))))

(defn entity
  "Get entity by id from Datalevin database."
  [conn entity-id]
  (d/entity (d/db conn) entity-id))

(defn pull
  "Pull entity data by pattern from Datalevin database."
  [conn pattern entity-id]
  (d/pull (d/db conn) pattern entity-id))

;; Migration tracking functions

(defn get-applied-migrations
  "Get list of applied migration IDs from the database."
  [conn]
  (->> (d/q '[:find
              ?id
              ?applied-at
              ?checksum
              :where
              [?e :migration/id ?id]
              [?e :migration/applied-at ?applied-at]
              [?e :migration/checksum ?checksum]]
            (d/db conn))
       (map (fn [[id applied-at checksum]]
              {:migration/id id
               :migration/applied-at applied-at
               :migration/checksum checksum}))))

(defn get-current-schema-version
  "Get current schema version (latest applied migration)."
  [conn]
  (->> (get-applied-migrations conn)
       (map :migration/applied-at)
       (sort)
       (last)))

(defn- apply-migration!
  "Apply a single migration to Datalevin database using its powerful update-schema capabilities."
  [conn migration logger]
  {:pre [conn
         (m/validate log/LoggerSchema logger)
         (m/validate migrations/Migration migration)]}
  (let [{:keys [migration/id migration/up migration/checksum]} migration]
    (try (log/log! logger
                   {:id ::migration-applying
                    :msg (str "Applying migration: " id)})
         ;; Run the migration - Datalevin migrations can use d/update-schema within the up function
         (when-let [result (up conn logger)]
           ;; Migration function can return:
           ;; - Transaction data for d/transact!
           ;; - Map with :schema-update, :del-attrs, :rename-map for d/update-schema
           ;; - nil if migration handles everything internally
           (cond
             ;; Schema update using Datalevin's update-schema
             (and (map? result)
                  (or (:schema-update result)
                      (:del-attrs result)
                      (:rename-map result)))
             (d/update-schema conn
                              (:schema-update result)
                              (:del-attrs result)
                              (:rename-map result))
             ;; Traditional transaction data
             (sequential? result) (when (seq result) (d/transact! conn result))
             ;; Migration handled everything internally
             :else nil))
         ;; Record migration as applied
         (d/transact! conn
                      [{:migration/id id
                        :migration/applied-at (Instant/now)
                        :migration/checksum checksum}])
         (log/log! logger
                   {:id ::migration-applied
                    :msg (str "Successfully applied migration: " id)})
         (catch Exception e
           (log/error! logger
                       {:error e
                        :id ::migration-failed
                        :data {:migration-id id}})
           (throw (ex-info (str "Migration failed: " id)
                           {:type ::migration-failed
                            :migration-id id
                            :cause e}
                           e))))))

(defn run-migrations!
  "Run all pending migrations on Datalevin database."
  [conn pending-migrations logger]
  {:pre [conn
         (m/validate log/LoggerSchema logger)
         (coll? pending-migrations)
         (every? #(m/validate migrations/Migration %) pending-migrations)]}
  (when (seq pending-migrations)
    (try (log/log! logger
                   {:id ::migrations-starting
                    :msg (str "Running "
                              (count pending-migrations)
                              " pending migrations")})
         (migrations/validate-migration-registry!)
         (let [applied-migrations (get-applied-migrations conn)]
           (migrations/validate-migration-integrity applied-migrations
                                                    migrations/migrations))
         (doseq [migration pending-migrations]
           (apply-migration! conn migration logger))
         (log/log!
          logger
          {:id ::migrations-completed
           :msg (str "Completed " (count pending-migrations) " migrations")})
         (catch Exception e
           (log/error! logger
                       {:error e
                        :id ::migrations-failed
                        :data {:pending-migrations-count (count
                                                          pending-migrations)}})
           (throw (ex-info "Failed to run migrations"
                           {:type ::migrations-failed
                            :pending-migrations-count (count pending-migrations)
                            :cause e}
                           e))))))
