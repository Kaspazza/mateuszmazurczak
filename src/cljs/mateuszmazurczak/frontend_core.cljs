(ns mateuszmazurczak.frontend-core
  "Entry point for customer app frontend"
  (:require
   [automaton-core.log          :as core-log]
   ;; [automaton-web.configuration               :as web-conf]
   [automaton-web.duplex.router :as rt]
   [automaton-web.events-proxy  :as web-events-proxy]
   ;; [automaton-web.log.tracking.error-tracking :as error-tracking]
   [automaton-web.react-proxy   :as web-react]
   [mateuszmazurczak.fe.events  :as mateuszmazurczak-fe-events]
   [mateuszmazurczak.fe.history :as mateuszmazurczak-fe-history]
   [mateuszmazurczak.fe.panels.public]
   [mateuszmazurczak.main       :as lm]
   [mount.core                  :as mount]))

(defonce *root (atom nil))

(defn ^:after-load re-render
  []
  (web-events-proxy/clear-subscription-cache!)
  (.unmount @*root)
  (reset! *root (web-react/render-id "app" [lm/main-component])))

(defn mount-root
  []
  (try (reset! *root (web-react/render-id "app" [lm/main-component]))
       (catch :default e (core-log/error (ex-info "Mount error" {:error e})))))

(defn ^:export init!
  []
  (try #_(error-tracking/init-error-tracking!
          {:dsn (web-conf/read-param [:log :sentry :frontend :dsn])
           :traced-website #"^https://mateuszmazurczak\.com/"
           :env (web-conf/read-param [:env])})
       (core-log/info "Front end starting")
       (web-events-proxy/client-app-db-init!
        ::mateuszmazurczak-fe-events/initialize-db) ;; What is done before will be lost in the state
       (mount-root)
       (rt/start-router!)
       (mount/start)
       (mateuszmazurczak-fe-history/init!) ;; Should be done after init-db so
       ;; history will update to right panel
       ;; when ok
       (catch :default e (core-log/error (ex-info "App init has failed" e)))))
