(ns mateuszmazurczak.repl.entry-point
  "REPL entry point"
  (:require
   [clojure.java.io                :as io]
   [integrant.core                 :as ig]
   [integrant.repl                 :refer [go halt init prep reset]]
   [integrant.repl.state           :as state]
   [mateuszmazurczak.configuration :as conf]
   [mateuszmazurczak.core          :as mateuszmazurczak-core]
   [mateuszmazurczak.portal        :as mp]
   [nrepl.server                   :refer
                                   [default-handler start-server stop-server]]
   [portal.api                     :as p])
  (:gen-class))

(defn default-port
  "Port where the server is started"
  []
  (conf/read-param [:dev :portal-port] 8351))

(defn app-name
  "Application name as displayed in the portal"
  []
  (conf/read-param [:app-name] "Non defined"))

(declare portal-logs)

(defn on-load
  []
  (p/eval-str portal-logs (slurp (io/resource "custom_viewer.cljs"))))

(defn portal-start
  "Starts portal app and opens logs viewer.
   Params:
   * port (optional) defaults to `default-port`, defines what port portal should be started."
  ([] (portal-start (default-port)))
  ([port]
   (def portal-logs
     (p/open {:window-title "Logs Viewer"
              :on-load on-load
              :value mp/filtered-logs}))
   (add-tap #'mp/submit)
   (tap> (format "Portal server has started for app `%s` on port %d"
                 (app-name)
                 port))))

(defn portal-stop
  "Close portal app"
  ([] (p/clear) (p/close))
  ([portal-server] (p/clear) (p/close portal-server)))

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

(defn try-require [ns] (try (require-ns ns) ns (catch Exception _ nil)))

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

(defn get-nrepl-port-parameter [] (conf/read-param [:dev :clj-nrepl-port] 8000))

(defn get-active-nrepl-port
  "Retrieve the nrepl port, available for REPL"
  []
  (:nrepl-port @repl))

(defn- stop-repl
  "Stop the repl"
  []
  (stop-server (:repl @repl))
  (reset! repl {}))

(defn- start-repl*
  [middlewares]
  (let [repl-port (get-nrepl-port-parameter)]
    (reset! repl {:nrepl-port repl-port
                  :repl (do (prn "-> Repl port is available on: " repl-port)
                            (start-server :port repl-port
                                          :handler (apply default-handler
                                                          middlewares)))})
    (portal-start)
    (.addShutdownHook
     (Runtime/getRuntime)
     (Thread. #(do
                 (prn "SHUTDOWN in progress, stop repl on port `" repl-port "`")
                 (shutdown-agents)
                 (stop-repl)
                 (portal-stop)
                 (println "SHUTDOWN ends successfully"))))))

(defn start-repl
  "Start repl, setup and catch errors
  Params:
  * `mdws` List of middlewares"
  [args mdws main-fn]
  (try (start-repl* mdws)
       (integrant.repl/set-prep! #(ig/expand mateuszmazurczak-core/config))
       (when-not (force-option? args) (main-fn))
       :started
       (catch Exception e
         (ex-info "Failed to start, relaunch with -force option" {:error e})
         nil)))


(defn -main
  "Main entry point for repl"
  [& args]
  (start-repl args (default-middleware) go))

(comment
  state/system
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
