(ns mateuszmazurczak.system
  (:require
   [integrant.core                       :as ig]
   [mateuszmazurczak.database            :as database]
   [mateuszmazurczak.endpoint.router     :as mm-endpoint-router]
   [mateuszmazurczak.error-tracking.core :as error-tracking]
   [mateuszmazurczak.i18n                :as i18n]
   [mateuszmazurczak.logging             :as log]
   [mateuszmazurczak.logging.telemere    :as t]
   [mateuszmazurczak.web-server          :as web-server]))

(defmethod ig/init-key :sys/error-tracking
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
                                        :env (name env)}))

(defmethod ig/init-key :sys/logging
  [_
   {:keys [level]
    :as _opts}]
  (let [inst (t/make-logger {:level level})]
    (log/init! inst {:level level})
    (log/log! inst
              {:id ::log-started
               :msg "Started log"})
    inst))

(defmethod ig/init-key :sys/http-server
  [_
   {:keys [handler logger http-port]
    :as _opts}]
  (try
    (log/log! logger
              {:level :debug
               :id ::http-server
               :msg "Started http-server"})
    (let [server (web-server/start-server handler {:http-port http-port})]
      (log/log! logger
                {:id ::http-server-started
                 :msg (str "Started!!! on http://localhost:" http-port)})
      server)
       (catch Throwable e
         (ex-info "Unexpected error during web server starting" {:error e}))))

(defmethod ig/halt-key! :sys/http-server
  [_ server]
  (try (.stop server)
       (catch Throwable e
         (ex-info "Unexepected error when closing http-server"
                  {:error e
                   :data {:http-server server}}))))


(defmethod ig/init-key :sys/handler
  [_
   {:keys [routes translator logger]
    :as _opts}]
  (mm-endpoint-router/get-app routes translator logger))

(defmethod ig/init-key :sys/db-conn
  [_
   {:keys [db-uri logger]
    :as _opts}]
  (log/log! logger
            {:id ::start-db
             :msg (str "Starting db..." db-uri)})
  (try (let [conn (database/start-db {:uri db-uri
                                      :logger logger})]
         (log/log! logger
                   {:id ::start-db-success
                    :msg (str "Started db!!! " db-uri)})
         conn)
       (catch Throwable e
         (log/error! logger
                     {:error e
                      :id ::failed-starting-db
                      :data {:uri db-uri}})
         (ex-info "Unexpected error during database starting" {:error e}))))

(defmethod ig/init-key :sys/translator
  [_ {:keys [debug? logger]}]
  (log/log! logger
            {:id ::translator-started
             :msg (str "Starting translator with debug=" debug?)})
  (i18n/create-translator debug?))
