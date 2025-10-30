(ns mateuszmazurczak.adapters.database.datomic
  "Datomic adapter implementation for database operations."
  (:require
   [datofu.all                                  :as datofu-all]
   [datofu.migration                            :as datofu-migration]
   [datofu.schema.dsl                           :as dsl]
   [datomic.api                                 :as d]
   [malli.core                                  :as m]
   [mateuszmazurczak.adapters.database.utils    :as db-utils]
   [mateuszmazurczak.domain.database.migrations :as migrations]
   [mateuszmazurczak.domain.database.schema     :as schema]
   [mateuszmazurczak.ports.logging              :as log]))

(defn- entity-attr->txes
  [kw m]
  (let [op (cond
             (:attr m) (partial dsl/attr kw)
             (:enum m) (partial dsl/to-one kw)
             (:ref m) (partial dsl/to-one kw)
             (:ref-many m) (partial dsl/to-many kw))
        extras (cond-> []
                 (:attr m) (conj (:attr m))
                 (:unique m) (conj (:unique m))
                 (:index m) (conj :index)
                 (:nohistory m) (conj :noHistory)
                 (:doc m) (conj (:doc m)))
        txes (cond-> [(apply op extras)]
               (:enum m) (into (map #(dsl/named %) (:enum m))))]
    txes))

(defn- initial-migration
  []
  (let [attrs (apply merge schema/entities)]
    {:type :schema
     :datofu.migration/id ::initial-schema
     :datofu.migration/tx (->> attrs
                               (mapcat #(apply entity-attr->txes %))
                               (vec))}))



(defn- connect-db
  [uri]
  (let [_created? (d/create-database uri)
        conn (d/connect uri)
        schema-tx (datofu-all/schema-tx)
        _initial-mig (datofu-migration/install-and-migrate! conn schema-tx [(initial-migration)])]
    conn))

(defn start
  "Start Datomic database connection with retry mechanism."
  [{:keys [uri logger]}]
  (try (db-utils/retry 5 5000 (partial connect-db uri) logger)
       (catch Exception e
         (log/error! logger
                     {:error e
                      :id ::database-start-failed
                      :data {:uri uri}})
         (throw (ex-info "Unable to start Datomic database"
                         {:type ::database-start-failed
                          :uri uri
                          :cause e}
                         e)))))

(defn stop
  "Stop Datomic database connection."
  [_conn]
  ;; Datomic connections don't need explicit closing
  ;; Connection pool is managed by Datomic
  nil)

(defn transact!
  "Execute transaction on Datomic database."
  [conn tx-data]
  {:pre [conn (coll? tx-data)]}
  (try @(d/transact conn tx-data)
       (catch Exception e
         (throw (ex-info "Transaction failed"
                         {:type ::transaction-failed
                          :tx-data tx-data
                          :cause e}
                         e)))))

(defn query
  "Execute query on Datomic database."
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
  "Get entity by id from Datomic database."
  [conn entity-id]
  (d/entity (d/db conn) entity-id))

(defn pull
  "Pull entity data by pattern from Datomic database."
  [conn pattern entity-id]
  (d/pull (d/db conn) pattern entity-id))

;; Migration functions

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
  "Apply a single migration to Datomic database."
  [conn migration logger]
  {:pre [conn (m/validate log/LoggerSchema logger) (m/validate migrations/Migration migration)]}
  (let [{:keys [migration/id migration/up migration/checksum]} migration]
    (try (log/log! logger
                   {:id ::migration-applying
                    :level :info
                    :msg (str "Applying migration: " id)})
         ;; Run the migration - Datomic handles schema changes through transactions
         (when-let [schema-changes (up conn logger)]
           (when (seq schema-changes) @(d/transact conn schema-changes)))
         ;; Record migration as applied
         @(d/transact conn
                      [{:migration/id id
                        :migration/applied-at (java.util.Date.)
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
  "Run all pending migrations on Datomic database."
  [conn pending-migrations logger]
  {:pre [conn
         (m/validate log/LoggerSchema logger)
         (coll? pending-migrations)
         (every? #(m/validate migrations/Migration %) pending-migrations)]}
  (when (seq pending-migrations)
    (try (log/log! logger
                   {:id ::migrations-starting
                    :msg (str "Running " (count pending-migrations) " pending migrations")})
         (migrations/validate-migration-registry!)
         (let [applied-migrations (get-applied-migrations conn)]
           (migrations/validate-migration-integrity applied-migrations migrations/migrations))
         (doseq [migration pending-migrations] (apply-migration! conn migration logger))
         (log/log! logger
                   {:id ::migrations-completed
                    :msg (str "Completed " (count pending-migrations) " migrations")})
         (catch Exception e
           (log/error! logger
                       {:error e
                        :id ::migrations-failed
                        :data {:pending-migrations-count (count pending-migrations)}})
           (throw (ex-info "Failed to run migrations"
                           {:type ::migrations-failed
                            :pending-migrations-count (count pending-migrations)
                            :cause e}
                           e))))))
