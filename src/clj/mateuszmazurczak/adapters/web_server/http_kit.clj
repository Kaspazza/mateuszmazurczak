(ns mateuszmazurczak.adapters.web-server.http-kit
  "Webserver state component, hold the webserver instance"
  (:require
   [org.httpkit.server :as http-kit]))

(defn start-server
  "Generate the server, based on the given handler.
  Options are optional, default values are
  - dont-block [true] tells server not to block the thread
  - port: should not be used to enable multiple web servers in the repl"
  [handler
   {:keys [http-port]
    :as opts}]
  (try (->> {:port http-port}
            (merge opts)
            (http-kit/run-server handler))
       (catch Exception e (ex-info "Unable to start server" {:error e}))))
