(ns mateuszmazurczak.core
  "Gather all components to start production app"
  (:require
   [integrant.core           :as ig]
   [mateuszmazurczak.config  :as config]
   [mateuszmazurczak.logging :as logging]
   [mateuszmazurczak.system])
  (:gen-class))

(defn -main
  "Main entry point for application. Environment determined by which mateuszmazurczak.config is loaded."
  [& _args]
  (try (let [system-config (config/system-config)
             full-config (config/load-config)
             system (ig/init system-config)
             logger (:sys/logging system)]
         (logging/log!
          logger
          {:id ::application-started
           :level :info
           :msg (str "Application started successfully with environment: "
                     (:env full-config))
           :data {:env (:env full-config)}})
         system)
       (catch Throwable e
         ;; At this point logging system might not be initialized yet
         (println "Application startup failed:" (.getMessage e))
         (.printStackTrace e)
         (System/exit 1))))





(comment
  (require '[integrant.repl.state :as state] '[aero.core])
  (aero.core/read-config "env/development/config.edn")
  state/config
  (tap> "hello")
  ;
)
