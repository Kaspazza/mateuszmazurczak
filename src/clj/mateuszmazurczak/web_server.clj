(ns mateuszmazurczak.web-server
  "Webserver state component, hold the webserver instance"
  (:require
   [mateuszmazurczak.configuration   :as mm-conf]
   [mateuszmazurczak.endpoint.router :as mm-endpoint-router]
   [mount.core                       :refer [defstate]]
   [org.httpkit.server               :as http-kit]))

(defn start-server
  "Generate the server, based on the given handler.
  Options are optional, default values are
  - dont-block [true] tells server not to block the thread
  - port: should not be used to enable multiple web servers in the repl"
  [handler opts]
  (let [http-port (mm-conf/read-param [:http-server :port] 8080)]
    (println (str "Started!!! on http://localhost:" http-port))
    (try (->> {:port http-port}
              (merge opts)
              (http-kit/run-server handler))
         (catch Exception e (ex-info "Unable to start server" {:error e})))))

(defn stop-server [server] (server) server)

(defstate http-server
          :start (try (start-server mm-endpoint-router/get-app {})
                      (catch Throwable e
                        (ex-info "Unexpected error during web server starting"
                                 {:error e})))
          :stop (try (stop-server @http-server)
                     (catch Throwable e
                       (ex-info "Unexepected error when closing http-server"
                                {:error e
                                 :data {:http-server @http-server}}))))
