(ns mateuszmazurczak.ports.database
  "Database port - provides database operations interface.
   
   Uses Function namespace hybrid approach:
   - This namespace acts as the port defining API functions
   - Internally chooses appropriate adapter (currently Datomic)
   - Integrant handles configuration and dependency injection
   - Serves as Application Service layer"
  (:require
   [mateuszmazurczak.adapters.database.datalevin :as adapter]
   [mateuszmazurczak.utils.validation            :as validation]))

;;DB component API
(defn start-database
  "Start database connection."
  [config]
  {:pre [(map? config)]}
  (try (adapter/start config)
       (catch Exception e
         (throw (ex-info "Failed to start database"
                         {:type ::database-start-failed
                          :config config
                          :cause e}
                         e)))))

(defn stop-database
  "Stop database connection."
  [conn]
  {:pre [conn]}
  (try (adapter/stop conn)
       (catch Exception e
         (throw (ex-info "Failed to stop database"
                         {:type ::database-stop-failed
                          :conn conn
                          :cause e}
                         e)))))

;;Migrations API
(defn run-migrations!
  "Run pending database migrations."
  [conn migrations logger]
  {:pre [conn (validation/valid-collection? migrations) logger]}
  (try (adapter/run-migrations! conn migrations logger)
       (catch Exception e
         (throw (ex-info "Failed to run migrations"
                         {:type ::migrations-failed
                          :migration-count (count migrations)
                          :cause e}
                         e)))))

(defn get-applied-migrations
  "Get list of applied migration IDs."
  [conn]
  (adapter/get-applied-migrations conn))

(defn get-current-schema-version
  "Get current schema version."
  [conn]
  (adapter/get-current-schema-version conn))

;;Execution API
(defn transact!
  "Execute transaction on database."
  [conn tx-data]
  {:pre [conn (validation/valid-collection? tx-data)]}
  (try (adapter/transact! conn tx-data)
       (catch Exception e
         (throw (ex-info "Failed to execute transaction"
                         {:type ::transaction-failed
                          :tx-data tx-data
                          :cause e}
                         e)))))

(defn query
  "Execute query on database."
  [conn query & args]
  {:pre [conn query]}
  (try (apply adapter/query conn query args)
       (catch Exception e
         (throw (ex-info "Failed to execute query"
                         {:type ::query-failed
                          :query query
                          :args args}
                         e)))))

(defn find-entity "Find entity by id." [conn entity-id] (adapter/entity conn entity-id))

(defn pull-entity
  "Pull entity data by pattern."
  [conn pattern entity-id]
  (adapter/pull conn pattern entity-id))
