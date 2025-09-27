(ns mateuszmazurczak.core
  "Gather all components to start production app"
  (:require
   [integrant.core                    :as ig]
   [mateuszmazurczak.config           :as config]
   [mateuszmazurczak.logging          :as logging]
   [mateuszmazurczak.logging.telemere :as t]
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
  (tap> "hello")
  (def logger (t/make-logger {:level :trace}))
  (logging/init! logger {:level :trace})
  state/config
  (logging/log! logger
                {:level :debug
                 :id ::login
                 :data {:user-id 1234}
                 :msg "what's up"})
  ;
)
