(ns mateuszmazurczak.system
  (:require
   [integrant.core                       :as ig]
   [mateuszmazurczak.database            :as database]
   [mateuszmazurczak.endpoint.router     :as mm-endpoint-router]
   [mateuszmazurczak.error-tracking.core :as error-tracking]
   [mateuszmazurczak.logging             :as log]
   [mateuszmazurczak.web-server          :as web-server]))

(defmethod ig/init-key ::error-tracking
  [_
   {:keys [dsn env logger]
    :as _opts}]
  (when-not dsn
    (log/log! logger
              {:id ::error-tracking-missing-param
               :msg "dsn is missing in init-error-tracking!"}))
  (when-not env
    (log/log! logger
              {:id ::error-tracking-missing-param
               :msg "env is missing in init-error-tracking!"}))
  (log/log! logger
            {:id ::error-tracking
             :msg "Starting error tracking..."})
  (error-tracking/init-error-tracking! {:dsn dsn
                                        :env env}))

(defmethod ig/init-key ::logging
  [_
   {:keys [level inst]
    :as _opts}]
  (log/init! inst {:level level})
  (log/log! inst
            {:id ::log-started
             :msg "Started log"})
  inst)

(defmethod ig/init-key ::http-server
  [_
   {:keys [handler logger http-port]
    :as _opts}]
  (try (log/log! logger
                 {:level :debug
                  :id ::http-server
                  :msg "Started http-server"})
       (web-server/start-server handler {:http-port http-port})
       (log/log! logger
                 {:id ::http-server-started
                  :msg (str "Started!!! on http://localhost:" http-port)})
       (catch Throwable e
         (ex-info "Unexpected error during web server starting" {:error e}))))

(defmethod ig/halt-key! ::http-server
  [_ server]
  (try (.stop server)
       (catch Throwable e
         (ex-info "Unexepected error when closing http-server"
                  {:error e
                   :data {:http-server server}}))))


(defmethod ig/init-key ::handler
  [_
   {:keys [routes]
    :as opts}]
  (mm-endpoint-router/get-app routes))

(defmethod ig/init-key ::db-conn
  [_
   {:keys [db-uri logger]
    :as _opts}]
  (try (database/start-db {:uri db-uri
                           :logger logger})
       (catch Throwable e
         (ex-info "Unexpected error during database starting" {:error e}))))

(defmethod ig/halt-key! ::db-conn
  [_ database]
  (try (prn "Stopping db...")
       (cond
         (instance? datomic.peer.Connection database)
         (try (database/stop-db database)
              (catch Throwable e
                (println "Error when closing DB:" (.getMessage e))
                (ex-info "Unexpected error when closing http-server"
                         {:error e
                          :data {:db database}})))
         (instance? Throwable database)
         (println
          "DB was never started successfully, nothing to stop. Exception:"
          (pr-str database))
         :else (println
                "DB state is not a connection or exception, nothing to stop."))
       (catch Throwable e
         (ex-info "Unexepected error when closing database" {:error e}))))
