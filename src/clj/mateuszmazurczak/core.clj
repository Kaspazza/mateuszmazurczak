(ns mateuszmazurczak.core
  "Gather all components to start production app"
  (:require
   [integrant.core                    :as ig]
   [mateuszmazurczak.configuration    :as mm-conf]
   [mateuszmazurczak.endpoint.routes  :as mm-endpoint-routes]
   [mateuszmazurczak.logging          :as logging]
   [mateuszmazurczak.logging.telemere :as t]
   [mateuszmazurczak.system           :as sys])
  (:gen-class))

(def config
  {::sys/error-tracking {:dsn (mm-conf/read-param [:log :sentry :backend :dsn])
                         :env (name (mm-conf/read-param [:env]))
                         :logger (ig/ref ::sys/logging)}
   ::sys/logging {:level (mm-conf/read-param [:log :level])
                  :inst (t/make-logger {:level :trace})}
   ::sys/handler {:routes mm-endpoint-routes/routes
                  :logger (ig/ref ::sys/logging)}
   ::sys/http-server {:http-port (mm-conf/read-param [:http-server :port] 8080)
                      :handler (ig/ref ::sys/handler)
                      :logger (ig/ref ::sys/logging)}
   ::sys/db-conn {:db-uri (mm-conf/read-param [:db :uri])
                  :logger (ig/ref ::sys/logging)}})

(defn -main
  "Main entry point for production, running production handler"
  [& _args]
  (try (ig/init config)
       (catch Throwable e
         (prn "failed: " (pr-str e))
         (ex-info "Unhandled exception" {:error e}))))

(comment
  (require '[integrant.repl.state :as state])
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
