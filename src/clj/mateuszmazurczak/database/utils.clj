(ns mateuszmazurczak.database.utils
  "Common database utilities shared across adapters."
  (:require
   [mateuszmazurczak.logging :as log]))

(defn retry
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