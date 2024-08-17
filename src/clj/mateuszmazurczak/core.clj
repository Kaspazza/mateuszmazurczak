(ns mateuszmazurczak.core
  "Gather all components to start production app"
  (:require
   [mount.core         :as mount]
   [mount.tools.graph  :as mount-graph]
   [automaton-core.log :as core-log]
   ;; [automaton-core.log.be-log   :as be-log]
   ;; [automaton-web.configuration :as web-conf]
   ;;List here all namespace to be mounted
   [mateuszmazurczak.web-server])
  (:gen-class))

(defn -main
  "Main entry point for production, running production handler"
  [& _args]
  (try
    #_(be-log/log-init! {:dsn (web-conf/read-param [:log :sentry :backend :dsn])
                         :env (name (web-conf/read-param [:env]))})
    (core-log/info "Start mateuszmazurczak")
    (core-log/trace "Component dependencies: " (mount-graph/states-with-deps))
    (mount/start)
    (catch Throwable e
      (core-log/fatal (ex-info "Unhandled exception" {:error e})))))

(comment
  (-main)
  ;
)
