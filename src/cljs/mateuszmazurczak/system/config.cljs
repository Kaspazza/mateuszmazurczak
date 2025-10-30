(ns mateuszmazurczak.system.config
  (:require
   [integrant.core                              :as ig]
   [mateuszmazurczak.adapters.navigation.routes :as mm-fe-routes]))

(goog-define ENV "")

(defn development? [] (= "development" ENV))

(defn production? [] (= "production" ENV))

(goog-define LOG_SENTRY_DNS "")
(goog-define POSTHOG_API_KEY "")
(goog-define LOKI_ENDPOINT "")

(def development-config
  {:logging.adapter/telemere {:level :debug}
   :frontend/logging {:level :debug
                      :adapter (ig/ref :logging.adapter/telemere)
                      :loki-endpoint LOKI_ENDPOINT}
   :frontend/error-tracking {:logger (ig/ref :frontend/logging)}
   :i18n.adapter/tempura {:debug? true}
   :frontend/translator {:adapter (ig/ref :i18n.adapter/tempura)
                         :logger (ig/ref :frontend/logging)}
   :events.adapter/reframe {:logger (ig/ref :frontend/logging)}
   :frontend/events {:adapter (ig/ref :events.adapter/reframe)
                     :logger (ig/ref :frontend/logging)}
   :watch.adapter/reframe {:logger (ig/ref :frontend/logging)}
   :frontend/state {:translator (ig/ref :frontend/translator)
                    :watch-adapter (ig/ref :watch.adapter/reframe)
                    :logger (ig/ref :frontend/logging)}
   :frontend/router {:routes mm-fe-routes/routes
                     :logger (ig/ref :frontend/logging)}
   :frontend/history {:router (ig/ref :frontend/router)
                      :app-db (ig/ref :frontend/state)
                      :events (ig/ref :frontend/events)
                      :logger (ig/ref :frontend/logging)}})

(def production-config
  {:logging.adapter/telemere {:level :info}
   :frontend/logging {:level :info
                      :adapter (ig/ref :logging.adapter/telemere)
                      :loki-endpoint LOKI_ENDPOINT}
   :frontend/error-tracking {:logger (ig/ref :frontend/logging)}
   :frontend/analytics {:api-key POSTHOG_API_KEY
                        :api-host "https://eu.i.posthog.com"
                        :person-profiles "always"
                        :logger (ig/ref :frontend/logging)}
   :i18n.adapter/tempura {:debug? false}
   :frontend/translator {:adapter (ig/ref :i18n.adapter/tempura)
                         :logger (ig/ref :frontend/logging)}
   :events.adapter/reframe {:logger (ig/ref :frontend/logging)}
   :frontend/events {:adapter (ig/ref :events.adapter/reframe)
                     :logger (ig/ref :frontend/logging)}
   :watch.adapter/reframe {:logger (ig/ref :frontend/logging)}
   :frontend/state {:translator (ig/ref :frontend/translator)
                    :watch-adapter (ig/ref :watch.adapter/reframe)
                    :logger (ig/ref :frontend/logging)}
   :frontend/router {:routes mm-fe-routes/routes
                     :logger (ig/ref :frontend/logging)}
   :frontend/history {:router (ig/ref :frontend/router)
                      :app-db (ig/ref :frontend/state)
                      :events (ig/ref :frontend/events)
                      :logger (ig/ref :frontend/logging)}})

(defn frontend-config
  []
  (if (development?) development-config production-config))
