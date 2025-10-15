(ns mateuszmazurczak.config
  (:require
   [integrant.core                     :as ig]
   [mateuszmazurczak.navigation.routes :as mm-fe-routes]))

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
   :i18n.adapter/reframe {:logger (ig/ref :frontend/logging)}
   :frontend/i18n {:translator-adapter (ig/ref :i18n.adapter/tempura)
                   :state-adapter (ig/ref :i18n.adapter/reframe)
                   :logger (ig/ref :frontend/logging)}
   :frontend/translator {:adapter (ig/ref :i18n.adapter/tempura)
                         :logger (ig/ref :frontend/logging)}
   :events.adapter/reframe {:logger (ig/ref :frontend/logging)}
   :frontend/events {:adapter (ig/ref :events.adapter/reframe)
                     :logger (ig/ref :frontend/logging)}
   :frontend/state {:translator (ig/ref :frontend/translator)
                    :i18n (ig/ref :frontend/i18n)
                    :logger (ig/ref :frontend/logging)}
   :nav.adapter/reframe {:logger (ig/ref :frontend/logging)}
   :frontend/router {:routes mm-fe-routes/routes
                     :logger (ig/ref :frontend/logging)}
   :frontend/history {:router (ig/ref :frontend/router)
                      :app-db (ig/ref :frontend/state)
                      :events (ig/ref :frontend/events)
                      :nav-adapter (ig/ref :nav.adapter/reframe)
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
   :i18n.adapter/reframe {:logger (ig/ref :frontend/logging)}
   :frontend/i18n {:translator-adapter (ig/ref :i18n.adapter/tempura)
                   :state-adapter (ig/ref :i18n.adapter/reframe)
                   :logger (ig/ref :frontend/logging)}
   :frontend/translator {:adapter (ig/ref :i18n.adapter/tempura)
                         :logger (ig/ref :frontend/logging)}
   :events.adapter/reframe {:logger (ig/ref :frontend/logging)}
   :frontend/events {:adapter (ig/ref :events.adapter/reframe)
                     :logger (ig/ref :frontend/logging)}
   :frontend/state {:translator (ig/ref :frontend/translator)
                    :i18n (ig/ref :frontend/i18n)
                    :logger (ig/ref :frontend/logging)}
   :nav.adapter/reframe {:logger (ig/ref :frontend/logging)}
   :frontend/router {:routes mm-fe-routes/routes
                     :logger (ig/ref :frontend/logging)}
   :frontend/history {:router (ig/ref :frontend/router)
                      :app-db (ig/ref :frontend/state)
                      :events (ig/ref :frontend/events)
                      :nav-adapter (ig/ref :nav.adapter/reframe)
                      :logger (ig/ref :frontend/logging)}})

(def frontend-config
  (if (= "development" ENV) development-config production-config))
