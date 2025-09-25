(ns mateuszmazurczak.database.adapters.datalevin
  "Datalevin adapter implementation for database operations."
  (:require
   [datalevin.core                   :as d]
   [mateuszmazurczak.database.schema :as schema]
   [mateuszmazurczak.database.utils  :as db-utils]
   [mateuszmazurczak.logging         :as log]))

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
  (let [schema (build-datalevin-schema) conn (d/get-conn url schema)] conn))

(defn start
  "Start Datalevin database connection with retry mechanism."
  [{:keys [uri logger]}]
  (try (db-utils/retry 5 1000 (partial connect-db uri) logger)
       (catch Exception e
         (log/error! logger
                     {:error e
                      :id ::database-start-failed
                      :data {:uri uri}})
         (ex-info "Unable to start db" {:error e}))))

(defn stop "Stop Datalevin database connection." [conn] (d/close conn))

(defn transact!
  "Execute transaction on Datalevin database."
  [conn tx-data]
  (d/transact! conn tx-data))

(defn query
  "Execute query on Datalevin database."
  [conn query & args]
  (apply d/q query (d/db conn) args))

(defn entity
  "Get entity by id from Datalevin database."
  [conn entity-id]
  (d/entity (d/db conn) entity-id))

(defn pull
  "Pull entity data by pattern from Datalevin database."
  [conn pattern entity-id]
  (d/pull (d/db conn) pattern entity-id))
