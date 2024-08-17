(ns mateuszmazurczak.web-server
  "Webserver state component, hold the webserver instance"
  (:require
   [automaton-core.log       :as core-log]
   [automaton-web.web-server :as server]
   [mateuszmazurczak.endpoint.router  :as mateuszmazurczak-router]
   [mount.core               :refer [defstate]]))

(defstate http-server
          :start (try (core-log/info "Starting http-server")
                      (server/start-server mateuszmazurczak-router/get-app {})
                      (catch Throwable e
                        (core-log/fatal
                         (ex-info "Unexpected error during web server starting"
                                  {:error e}))))
          :stop (try (server/stop-server @http-server)
                     (catch Throwable e
                       (core-log/fatal
                        (ex-info "Unexepected error when closing http-server"
                                 {:error e
                                  :data {:http-server @http-server}})))))
