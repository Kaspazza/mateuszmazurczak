(ns mateuszmazurczak.database
  "Database port - provides database operations interface.
   
   Uses Function namespace hybrid approach:
   - This namespace acts as the port defining API functions
   - Internally chooses appropriate adapter (currently Datomic)
   - Integrant handles configuration and dependency injection
   - Serves as Application Service layer"
  (:require
   [mateuszmazurczak.database.adapters.datomic :as datomic-adapter]))

;; Port API Functions - Basic database operations

(defn start-database
  "Start database connection."
  [config]
  (datomic-adapter/start config))

(defn stop-database
  "Stop database connection."
  [conn]
  (datomic-adapter/stop conn))

(defn transact!
  "Execute transaction on database."
  [conn tx-data]
  (datomic-adapter/transact! conn tx-data))

(defn query
  "Execute query on database."
  [conn query & args]
  (apply datomic-adapter/query conn query args))

(defn find-entity
  "Find entity by id."
  [conn entity-id]
  (datomic-adapter/entity conn entity-id))

(defn pull-entity
  "Pull entity data by pattern."
  [conn pattern entity-id]
  (datomic-adapter/pull conn pattern entity-id))

