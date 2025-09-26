(ns mateuszmazurczak.database
  "Database port - provides database operations interface.
   
   Uses Function namespace hybrid approach:
   - This namespace acts as the port defining API functions
   - Internally chooses appropriate adapter (currently Datomic)
   - Integrant handles configuration and dependency injection
   - Serves as Application Service layer"
  (:require
   [mateuszmazurczak.database.adapters.datalevin :as adapter]))

;;DB component API
(defn start-database
  "Start database connection."
  [config]
  (adapter/start config))

(defn stop-database "Stop database connection." [conn] (adapter/stop conn))

;;Migrations API
(defn run-migrations!
  "Run pending database migrations."
  [conn migrations logger]
  (adapter/run-migrations! conn migrations logger))

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
  (adapter/transact! conn tx-data))

(defn query
  "Execute query on database."
  [conn query & args]
  (apply adapter/query conn query args))

(defn find-entity
  "Find entity by id."
  [conn entity-id]
  (adapter/entity conn entity-id))

(defn pull-entity
  "Pull entity data by pattern."
  [conn pattern entity-id]
  (adapter/pull conn pattern entity-id))
