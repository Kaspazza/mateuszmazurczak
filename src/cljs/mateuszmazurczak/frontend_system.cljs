(ns mateuszmazurczak.frontend-system
  "Frontend Integrant system configuration"
  (:require
   ["@sentry/react"                           :as Sentry]
   ["react-router-dom"                        :refer (useLocation
                                                      useNavigationType
                                                      createRoutesFromChildren
                                                      matchRoutes)]
   [day8.re-frame.tracing                     :refer [fn-traced]]
   [integrant.core                            :as ig]
   [mateuszmazurczak.config                   :as conf]
   [mateuszmazurczak.i18n                     :as mm-i18n]
   [mateuszmazurczak.i18n.translate           :as mm-i18n-translate]
   [mateuszmazurczak.logging                  :as log]
   [mateuszmazurczak.logging.telemere         :as t]
   [mateuszmazurczak.navigation.core          :as nav]
   [mateuszmazurczak.navigation.router.reitit :as router-reitit]
   [mateuszmazurczak.navigation.routes        :as mm-fe-routes]
   [re-frame.core                             :as rf]
   [react                                     :as react]))

(defmethod ig/init-key :frontend/router
  [_ {:keys [routes logger]}]
  (log/log! logger 
            {:id ::router-started
             :msg "Router started"})
  (let [router (router-reitit/create-router routes)]
    (nav/set-router! router)
    router))

(defmethod ig/halt-key! :frontend/router
  [_ _router]
  (nav/set-router! nil))

(defmethod ig/init-key :frontend/history
  [_ {:keys [router _app-db logger]}]
  (log/log! logger 
            {:id ::history-started
             :msg "History started"})
  (let [history (nav/init-history! router)]
    (nav/set-history! history)
    history))

(defmethod ig/halt-key! :frontend/history
  [_ history]
  (when history
    (nav/stop-history! history)
    (nav/set-history! nil)))

(def default-db
  "Default value for front end state"
  {:name "mateuszmazurczak"
   :current-route {:panel-id :panels/pending}
   :lang (mm-i18n-translate/language-strategy)})

(rf/reg-event-db ::initialize-db
                 (fn-traced [_ _]
                            ;; Intentionally not using
                            ;; previous value of db, as it is
                            ;; an init
                            default-db))

(rf/reg-event-db ::store-translator
                 (fn-traced [db [_ translator]]
                            (assoc db :translator translator)))

(defmethod ig/init-key :frontend/app-db
  [_ {:keys [init-db-event translator logger]}]
  (log/log! logger 
            {:id ::app-db-initialized
             :msg "App-db initialized"})
  (rf/clear-subscription-cache!)
  (rf/dispatch-sync [init-db-event])
  ;; Store translator in app-db
  (rf/dispatch-sync [::store-translator translator]))

(defmethod ig/halt-key! :frontend/app-db
  [_ _]
  (rf/clear-subscription-cache!))

(defn- init-sentry!
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

(defmethod ig/init-key :frontend/error-tracking
  [_ {:keys [dsn traced-website env logger]}]
  (when-not dsn
    (log/log! logger 
              {:level :warn
               :id ::error-tracking-missing-dsn
               :msg "dsn is missing in error tracking initialization"}))
  (when-not env
    (log/log! logger 
              {:level :warn
               :id ::error-tracking-missing-env
               :msg "env is missing in error tracking initialization"}))
  (log/log! logger 
            {:id ::error-tracking-initialized
             :msg "Error tracking initialized"})
  (init-sentry! {:dsn dsn
                 :traced-website traced-website
                 :env env}))

(defmethod ig/halt-key! :frontend/error-tracking
  [_ _]
  nil)

(defmethod ig/init-key :frontend/logging
  [_ {:keys [level]}]
  (let [logger (t/make-logger {:level level})]
    (log/init! logger {:level level})
    (log/log! logger 
              {:id ::frontend-logging-started
               :msg "Frontend logging system initialized"})
    logger))

(defmethod ig/halt-key! :frontend/logging
  [_ logger]
  (log/log! logger 
            {:id ::frontend-logging-stopped
             :msg "Frontend logging system stopped"}))

(defmethod ig/init-key :frontend/translator
  [_ {:keys [debug? logger]}]
  (log/log! logger 
            {:id ::translator-started
             :msg (str "Translator started with debug=" debug?)})
  (mm-i18n/create-translator debug?))

(defmethod ig/halt-key! :frontend/translator
  [_ _]
  nil)

(def frontend-config
  {:frontend/logging {:level (if (= "development" conf/ENV) :debug :info)}
   :frontend/error-tracking {:dsn conf/LOG_SENTRY_DNS
                             :traced-website #"^https://mateuszmazurczak\.com/"
                             :env conf/ENV
                             :logger (ig/ref :frontend/logging)}
   :frontend/translator {:debug? (= "development" conf/ENV)
                         :logger (ig/ref :frontend/logging)}
   :frontend/app-db {:init-db-event ::initialize-db
                     :translator (ig/ref :frontend/translator)
                     :logger (ig/ref :frontend/logging)}
   :frontend/router {:routes mm-fe-routes/routes
                     :logger (ig/ref :frontend/logging)}
   :frontend/history {:router (ig/ref :frontend/router)
                      :app-db (ig/ref :frontend/app-db)
                      :logger (ig/ref :frontend/logging)}})

(defonce system (atom nil))

(defn start-system!
  []
  (when-not @system (reset! system (ig/init frontend-config))))

(defn stop-system! [] (when @system (ig/halt! @system) (reset! system nil)))

(defn restart-system! [] (stop-system!) (start-system!))
