(ns mateuszmazurczak.repl.entry-point
  "REPL entry point"
  (:require
   [aero.core]
   [integrant.core           :as ig]
   [integrant.repl           :refer [go halt init prep reset]]
   [integrant.repl.state     :as state]
   [mateuszmazurczak.configuration]
   [mateuszmazurczak.logging :as log]
   [mateuszmazurczak.system]
   [nrepl.server             :refer [default-handler start-server stop-server]])
  (:gen-class))



(defn require-ns
  "Require the namespace of the body-fn
  Params:
  * `f` is a function full qualified symbol. It could be a string"
  [f]
  (some-> f
          symbol
          namespace
          symbol
          require))

(defn try-require
  [ns]
  (try (require-ns ns)
       ns
       (catch Exception e
         ;; This is expected to fail for optional dependencies, so we don't log it as error
         nil)))

(defn- force-option?
  [args]
  (and args
       (seq args)
       (filter some? (map #(contains? #{"-f" "--force"} %) args))))

(defn default-middleware
  []
  (let [cider-middlewares (try-require 'cider.nrepl/cider-middleware)
        nrepl-middleware (try-require 'refactor-nrepl.middleware/wrap-refactor)]
    (cond-> []
      cider-middlewares (concat @(resolve cider-middlewares))
      nrepl-middleware (conj nrepl-middleware)
      true vec)))
(def nrepl-port-filename "Name of the `.nrepl-port` file" ".nrepl-port")

(def repl "Store the repl instance in the atom" (atom {}))


(defn get-active-nrepl-port
  "Retrieve the nrepl port, available for REPL"
  []
  (:nrepl-port @repl))

(defn- stop-repl
  "Stop the repl"
  []
  (stop-server (:repl @repl))
  (reset! repl {}))



(defn start-repl
  "Start repl, setup and catch errors
  Params:
  * `mdws` List of middlewares"
  [args mdws main-fn]
  (try (let [conf (aero.core/read-config "env/development/config.edn")
             nrepl-port (get-in conf [:dev :clj-nrepl-port])
             app-name (get conf :app-name)]
         (spit nrepl-port-filename nrepl-port)
         ;; For now, start REPL without logger (use println), then get logger after system init
         (println "-> Starting REPL on port:" nrepl-port)
         (reset! repl {:nrepl-port nrepl-port
                       :repl (start-server :port nrepl-port
                                           :handler (apply
                                                     default-handler
                                                     (default-middleware)))})
         (println "-> REPL started successfully on port:" nrepl-port)
         (.addShutdownHook
          (Runtime/getRuntime)
          (Thread. #(do (println "SHUTDOWN in progress, stopping REPL on port:"
                                 nrepl-port)
                        (shutdown-agents)
                        (stop-repl)
                        (println "SHUTDOWN completed successfully"))))
         (integrant.repl/set-prep! #(ig/expand (:system conf))))
       (when-not (force-option? args)
         (main-fn)
         (when-let [logger (:sys/logging state/system)]
           (log/log! logger
                     {:id ::repl-system-integration-complete
                      :level :info
                      :msg "REPL and system integration completed"
                      :data {:port (get-active-nrepl-port)}})))
       :started
       (catch Exception e
         ;; At this point we might not have logger available yet
         (println "Failed to start REPL, relaunch with -force option. Error:"
                  (.getMessage e))
         (.printStackTrace e)
         (throw e))))


(defn -main
  "Main entry point for repl"
  [& args]
  (start-repl args (default-middleware) go))

(comment
  (require '[mateuszmazurczak.system :as sys])
  ;; (ig/halt! state/system [::sys/db-conn])
  state/config
  (prep)
  (init)
  ;;Start
  (go)
  ;;halt
  (halt)
  ;;reset
  (reset)
  ;
)
