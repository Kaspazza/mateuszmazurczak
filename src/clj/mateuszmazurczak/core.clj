(ns mateuszmazurczak.core
  "Gather all components to start production app"
  (:require
   [mateuszmazurczak.configuration       :as mm-conf] ;;List here all namespace to be mounted
   [mateuszmazurczak.web-server]
   [mount.core                           :as mount]
   [mateuszmazurczak.error-tracking.core :as error-tracking])
  (:gen-class))

(defn -main
  "Main entry point for production, running production handler"
  [& _args]
  (try (error-tracking/init-error-tracking!
        {:dsn (mm-conf/read-param [:log :sentry :backend :dsn])
         :env (name (mm-conf/read-param [:env]))})
       (mount/start)
       (catch Throwable e
         (prn "failed: " e)
         (ex-info "Unhandled exception" {:error e}))))

(comment
  (-main)
  ;
)
