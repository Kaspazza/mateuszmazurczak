(ns mateuszmazurczak.frontend-core
  "Entry point for customer app frontend"
  (:require
   ["@sentry/react"                     :as Sentry]
   ["react-router-dom"                  :refer (useLocation
                                                useNavigationType
                                                createRoutesFromChildren
                                                matchRoutes)]
   [mateuszmazurczak.configuration      :as mm-conf]
   [mateuszmazurczak.events.db          :as mm-evts-db]
   [mateuszmazurczak.main               :as lm]
   [mateuszmazurczak.navigation.history :as mateuszmazurczak-fe-history]
   [mateuszmazurczak.navigation.panels]
   [mount.core                          :as mount]
   [re-frame.core                       :as rf]
   [react                               :as react]
   [reagent.dom.client                  :as rdc]))

(defn init-sentry!
  "Initialize sentry for react, which is recording react errors that happens inside the components and enables to send events.
  'development' as an environment is ignored, so no event is sent from it."
  [{:keys [dsn traced-website env]}]
  (.init Sentry
         #js {:dsn dsn
              :environment env
              :integrations #js [(.reactRouterV6BrowserTracingIntegration
                                  Sentry
                                  #js {:useEffect react/useEffect
                                       :useLocation useLocation
                                       :useNavigationType useNavigationType
                                       :createRoutesFromChildren
                                       createRoutesFromChildren
                                       :matchRoutes matchRoutes})
                                 (.replayIntegration Sentry)]
              :replaysSessionSampleRate 0
              :replaysOnErrorSampleRate 0
              :tracesSampleRate 1.0
              :tracePropagationTargets #js ["localhost" traced-website]}))

(defn init-error-tracking!
  "Initializes error tracking, currently focused on sentry."
  [{:keys [dsn traced-website env]}]
  (when-not dsn (prn "dsn is missing in init-error-tracking!"))
  (when-not env (prn "env is missing in init-error-tracking!"))
  (init-sentry! {:dsn dsn
                 :traced-website traced-website
                 :env env}))

(defn render-id
  [app-id component]
  (let [el (js/document.getElementById app-id)
        root (rdc/create-root el)]
    (rdc/render root component)
    root))

(defn client-app-db-init!
  "To be called during cust-app init to init re-frame
  Params:
  * `init-db-evt` keyword of the event to init the db"
  [init-db-evt]
  (rf/clear-subscription-cache!)
  (rf/dispatch-sync [init-db-evt]))

(defonce *root (atom nil))

(defn ^:after-load re-render
  []
  (rf/clear-subscription-cache!)
  (.unmount @*root)
  (reset! *root (render-id "app" [lm/main-component])))

(defn mount-root
  []
  (try (reset! *root (render-id "app" [lm/main-component]))
       (catch :default e (ex-info "Mount error" {:error e}))))

;;TODO add realtime with updates to refresh page on-click
;;TODO add logs
;;TODO add deploy task with deploy to droplet
;;TODO add versioning
;;TODO add auth
;;TODO cleanup - go over files and re-check
(defn ^:export init!
  []
  (try (init-error-tracking! {:dsn (mm-conf/read-param
                                    [:log :sentry :frontend :dsn])
                              :traced-website #"^https://mateuszmazurczak\.com/"
                              :env (mm-conf/read-param [:env])})
       (client-app-db-init! ::mm-evts-db/initialize-db) ;; What is done before will be lost in the state
       (mount-root)
       (mount/start)
       (mateuszmazurczak-fe-history/init!) ;; Should be done after init-db so
       ;; history will update to right panel
       ;; when ok
       (catch :default e (ex-info "App init has failed" e))))
