(ns mateuszmazurczak.database
  (:require
   [datofu.all                       :as datofu-all]
   [datofu.migration                 :as datofu-migration]
   [datofu.schema.dsl                :as dsl]
   [datomic.api                      :as d]
   [mateuszmazurczak.database.schema :as schema]
   [mateuszmazurczak.logging         :as log]))

(defn entity-attr->txes
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

(defn initial-migration
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

(defn connect-db
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

(defn start-db
  "Generate the server, based on the given handler.
  Options are optional, default values are
  - dont-block [true] tells server not to block the thread
  - port: should not be used to enable multiple web servers in the repl"
  [{:keys [uri logger]}]
  (try (retry 5 5000 (partial connect-db uri) logger)
       (catch Exception e 
         (log/error! logger 
                     {:error e
                      :id ::database-start-failed
                      :data {:uri uri}})
         (ex-info "Unable to start db" {:error e}))))

