(ns mateuszmazurczak.adapters.error-tracking.logging
  "Logging adapter - logs uncaught exceptions via logging system"
  (:require
   [mateuszmazurczak.ports.logging :as log]))

(defn init!
  "Install global uncaught exception handler that logs via logging system.
   
   Config map:
   - :logger - logger instance to use for logging uncaught exceptions
   
   Returns nil."
  [{:keys [logger]
    :as config}]
  {:pre [(map? config) (some? logger)]}
  (Thread/setDefaultUncaughtExceptionHandler
   (reify
    Thread$UncaughtExceptionHandler
      (uncaughtException [_ thread ex]
        (log/error! logger
                    {:error ex
                     :id ::uncaught-exception
                     :data {:thread-name (.getName thread)
                            :thread-id (.getId thread)}}))))
  nil)
