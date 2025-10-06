(ns mateuszmazurczak.error-tracking.adapters.sentry
  "Sentry adapter for error tracking (CLJS)"
  (:require
   ["@sentry/react"              :as Sentry]
   ["react"                      :as react]
   ["react-router-dom"           :refer (createRoutesFromChildren
                                         matchRoutes
                                         useLocation
                                         useNavigationType)]
   [mateuszmazurczak.validation :as validation]))

(defn- keyword->level
  "Converts keyword into Sentry event level string"
  [level]
  (case level
    :debug "debug"
    :info "info"
    :warning "warning"
    :error "error"
    :fatal "fatal"
    "info"))

;;; Public API

(defn init!
  "Initialize Sentry with configuration"
  [{:keys [dsn env traced-website] :as config}]
  {:pre [(map? config)
         (validation/valid-non-empty-string? dsn)
         (validation/valid-non-empty-string? env)]}
  (try (.init Sentry
              #js {:dsn dsn
                   :environment env
                   :integrations
                   #js [(.reactRouterV6BrowserTracingIntegration
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
                   :tracePropagationTargets
                   #js ["localhost"
                        (if (regexp? traced-website)
                          traced-website
                          #"^https://mateuszmazurczak\.com/")]})
       (catch :default e
         (throw (ex-info "Failed to initialize Sentry error tracking"
                         {:config (dissoc config :dsn)} ; Don't log DSN for security
                         e)))))

(defn capture-error!
  "Capture error in Sentry"
  [{:keys [message level data silent?] :or {silent? false} :as error-data}]
  {:pre [(map? error-data)]}
  (try
    (if silent?
      ;; Add as breadcrumb (won't show in Sentry until next real error)
      (.addBreadcrumb Sentry
                      #js {:message (str message)
                           :level (keyword->level level)
                           :data (clj->js data)})
      ;; Send as event (appears immediately in Sentry)
      (.captureMessage Sentry
                       (str message)
                       #js {:level (keyword->level level)
                            :extra (clj->js data)}))
    nil
    (catch :default e
      (throw (ex-info "Failed to capture error in Sentry"
                      {:error-data error-data}
                      e)))))
