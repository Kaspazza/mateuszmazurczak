(ns mateuszmazurczak.system.components
  (:require
   [integrant.core                                :as ig]
   [mateuszmazurczak.adapters.http.router         :as mm-http-router]
   [mateuszmazurczak.adapters.i18n.tempura        :as i18n-tempura]
   [mateuszmazurczak.adapters.logging.telemere    :as t]
   [mateuszmazurczak.adapters.web-server.http-kit :as web-server]
   [mateuszmazurczak.domain.database.migrations   :as migrations]
   [mateuszmazurczak.domain.i18n.dict.resources   :as mm-i18n-dict-res]
   [mateuszmazurczak.domain.i18n.dict.text        :as mm-i18n-dict-txt]
   [mateuszmazurczak.ports.database               :as database]
   [mateuszmazurczak.ports.error-tracking         :as error-tracking]
   [mateuszmazurczak.ports.logging                :as log]))

(defmethod ig/init-key :logging.adapter/telemere [_ opts] (t/make-logger {:level (:level opts)}))

(defmethod ig/init-key :i18n.adapter/tempura
  [_ {:keys [debug?]}]
  (i18n-tempura/make-translator debug? mm-i18n-dict-txt/dict mm-i18n-dict-res/dict))

(defmethod ig/init-key :sys/logging
  [_
   {:keys [level adapter]
    :as _opts}]
  (log/init! adapter {:level level})
  (log/log! adapter
            {:id ::log-started
             :level :info
             :msg "Started log"})
  adapter)

(defmethod ig/init-key :sys/translator
  [_ {:keys [adapter logger]}]
  (log/log! logger
            {:id ::translator-started
             :level :info
             :msg "Translator started"})
  adapter)

(defmethod ig/init-key :sys/error-tracking
  [_ opts]
  (let [logger (:logger opts)]
    (log/log! logger
              {:id ::error-tracking-init
               :level :debug
               :msg "Initializing error tracking..."})
    (error-tracking/init! opts)
    (log/log! logger
              {:id ::error-tracking-started
               :level :info
               :msg "Error tracking initialized"})))

(defmethod ig/init-key :sys/http-server
  [_
   {:keys [handler logger http-port]
    :as _opts}]
  (try (log/log! logger
                 {:level :debug
                  :id ::http-server
                  :msg "Started http-server"})
       (let [server (web-server/start-server handler {:http-port http-port})]
         (log/log! logger
                   {:id ::http-server-started
                    :level :info
                    :msg (str "Started!!! on http://localhost:" http-port)})
         server)
       (catch Throwable e
         (log/error! logger
                     {:error e
                      :id ::http-server-start-failed
                      :data {:http-port http-port}})
         (throw (ex-info "Failed to start HTTP server"
                         {:type ::http-server-start-failed
                          :http-port http-port}
                         e)))))

(defmethod ig/halt-key! :sys/http-server
  [_ server]
  (try (.stop server)
       (catch Throwable e
         (throw (ex-info "Failed to stop HTTP server"
                         {:type ::http-server-stop-failed
                          :server server}
                         e)))))


(defmethod ig/init-key :sys/handler
  [_
   {:keys [routes translator logger database admin-api-key]
    :as _opts}]
  (mm-http-router/get-app routes translator logger database admin-api-key))

(defmethod ig/init-key :sys/db-conn
  [_
   {:keys [db-uri logger]
    :as _opts}]
  (log/log! logger
            {:id ::start-db
             :level :info
             :msg (str "Starting db..." db-uri)})
  (try (let [conn (database/start-database {:uri db-uri
                                            :logger logger})
             applied-migrations (database/get-applied-migrations conn)
             applied-ids (set (map :migration/id applied-migrations))
             pending-migrations (migrations/get-pending-migrations applied-ids)]
         (database/run-migrations! conn pending-migrations logger)
         (log/log! logger
                   {:id ::start-db-success
                    :level :info
                    :msg (str "Started db with " (count applied-migrations)
                              " applied migrations!!! " db-uri)})
         conn)
       (catch Throwable e
         (log/error! logger
                     {:error e
                      :id ::failed-starting-db
                      :data {:uri db-uri}})
         (throw (ex-info "Failed to start database system"
                         {:type ::database-system-start-failed
                          :db-uri db-uri}
                         e)))))


