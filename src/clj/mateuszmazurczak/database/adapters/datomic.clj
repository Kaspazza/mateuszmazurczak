(ns mateuszmazurczak.database.adapters.datomic
  "Datomic adapter implementation for database operations."
  (:require
   [datofu.all                       :as datofu-all]
   [datofu.migration                 :as datofu-migration]
   [datofu.schema.dsl                :as dsl]
   [datomic.api                      :as d]
   [mateuszmazurczak.database.schema :as schema]
   [mateuszmazurczak.logging         :as log]))

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

(defn- retry
  "Retries (f) up to n times with delay-ms between attempts."
  [n delay-ms f logger]
  (loop [attempt 1]
    (let [result (try {:success true
                       :value (f)}
                      (catch Exception e 
                        (if (< attempt n)
                          (log/log! logger
                                    {:level :warn
                                     :id ::database-retry-attempt-failed
                                     :msg (str "Database connection attempt " attempt " failed, retrying...")
                                     :data {:attempt attempt
                                            :max-attempts n
                                            :delay-ms delay-ms
                                            :error-message (.getMessage e)}})
                          (log/error! logger
                                      {:error e
                                       :id ::database-all-retry-attempts-failed
                                       :data {:attempt attempt
                                              :max-attempts n
                                              :delay-ms delay-ms}}))
                        {:success false
                         :error e}))]
      (if (:success result)
        (:value result)
        (if (< attempt n)
          (do (Thread/sleep delay-ms) (recur (inc attempt)))
          (throw (:error result)))))))

(defn- connect-db
  [uri]
  (let [;; Just to make sure db is created
        _created? (d/create-database uri)
        conn (d/connect uri)
        schema-tx (datofu-all/schema-tx)
        _initial-mig (datofu-migration/install-and-migrate!
                      conn
                      schema-tx
                      [(initial-migration)])]
    conn))

(defn start
  "Start Datomic database connection with retry mechanism."
  [{:keys [uri logger]}]
  (try (retry 5 5000 (partial connect-db uri) logger)
       (catch Exception e 
         (log/error! logger 
                     {:error e
                      :id ::database-start-failed
                      :data {:uri uri}})
         (ex-info "Unable to start db" {:error e}))))

(defn stop
  "Stop Datomic database connection."
  [conn]
  ;; Datomic connections don't need explicit closing
  ;; Connection pool is managed by Datomic
  nil)

(defn transact!
  "Execute transaction on Datomic database."
  [conn tx-data]
  @(d/transact conn tx-data))

(defn query
  "Execute query on Datomic database."
  [conn query & args]
  (apply d/q query (d/db conn) args))

(defn entity
  "Get entity by id from Datomic database."
  [conn entity-id]
  (d/entity (d/db conn) entity-id))

(defn pull
  "Pull entity data by pattern from Datomic database."
  [conn pattern entity-id]
  (d/pull (d/db conn) pattern entity-id))