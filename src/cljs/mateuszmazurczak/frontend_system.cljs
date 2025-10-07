(ns mateuszmazurczak.frontend-system
  "Frontend Integrant system configuration"
  (:require
   [day8.re-frame.tracing                     :refer [fn-traced]]
   [integrant.core                            :as ig]
   [mateuszmazurczak.analytics                :as analytics]
   [mateuszmazurczak.config                   :as conf]
   [mateuszmazurczak.error-tracking           :as error-tracking]
   [mateuszmazurczak.i18n.adapters.tempura    :as i18n-tempura]
   [mateuszmazurczak.i18n.dict.resources      :as mm-i18n-dict-res]
   [mateuszmazurczak.i18n.dict.text           :as mm-i18n-dict-txt]
   [mateuszmazurczak.i18n.translate           :as mm-i18n-translate]
   [mateuszmazurczak.integrant-utils          :as ig-utils]
   [mateuszmazurczak.logging                  :as log]
   [mateuszmazurczak.logging.telemere         :as t]
   [mateuszmazurczak.navigation.core          :as nav]
   [mateuszmazurczak.navigation.router.reitit :as router-reitit]
   [mateuszmazurczak.navigation.routes        :as mm-fe-routes]
   [re-frame.core                             :as rf]))

(defmethod ig/init-key :frontend/router
  [_ {:keys [routes logger]}]
  (log/log! logger
            {:id ::router-started
             :level :info
             :msg "Router started"})
  (let [router (router-reitit/create-router routes)]
    (nav/set-router! router)
    router))

(defmethod ig/halt-key! :frontend/router [_ _router] (nav/set-router! nil))

(defmethod ig/init-key :frontend/history
  [_ {:keys [router _app-db logger]}]
  (log/log! logger
            {:id ::history-started
             :level :info
             :msg "History started"})
  (let [history (nav/init-history! router)]
    (nav/set-history! history)
    history))

(defmethod ig/halt-key! :frontend/history
  [_ history]
  (when history (nav/stop-history! history) (nav/set-history! nil)))

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

(rf/reg-event-db ::system-failed
                 (fn-traced [db [_ error]]
                            (assoc db
                                   :current-route {:panel-id
                                                   :panels/system-error}
                                   :system-error error)))

(defmethod ig/init-key :frontend/app-db
  [_ {:keys [init-db-event translator logger]}]
  (log/log! logger
            {:id ::app-db-initialized
             :level :info
             :msg "App-db initialized"})
  (rf/clear-subscription-cache!)
  (rf/dispatch-sync [init-db-event])
  (rf/dispatch-sync [::store-translator translator]))

(defmethod ig/halt-key! :frontend/app-db [_ _] (rf/clear-subscription-cache!))

(defmethod ig/init-key :frontend/error-tracking
  [_ opts]
  (let [logger (:logger opts)]
    (ig-utils/optional-component
     (fn []
       (log/log! logger
                 {:id ::error-tracking-initializing
                  :level :info
                  :msg "Initializing error tracking..."})
       (error-tracking/init! opts)
       (log/log! logger
                 {:id ::error-tracking-initialized
                  :level :info
                  :msg "Error tracking initialized"}))
     logger
     :frontend/error-tracking)))

(defmethod ig/halt-key! :frontend/error-tracking [_ _] nil)

(defmethod ig/init-key :frontend/analytics
  [_ {:keys [api-key api-host person-profiles logger]}]
  (ig-utils/optional-component
   (fn []
     (when-not api-key
       (log/log! logger
                 {:level :warn
                  :id ::analytics-missing-api-key
                  :msg "api-key is missing in analytics initialization"}))
     (when-not api-host
       (log/log! logger
                 {:level :warn
                  :id ::analytics-missing-api-host
                  :msg "api-host is missing in analytics initialization"}))
     (log/log! logger
               {:id ::analytics-initialization
                :level :debug
                :msg "Analytics starting..."})
     (let [analytics (analytics/init! {:api-key api-key
                                       :api-host api-host
                                       :person-profiles person-profiles})]
       (log/log! logger
                 {:id ::analytics-initialized
                  :level :info
                  :msg "Analytics started"})
       analytics))
   logger
   :frontend/analytics))

(defmethod ig/halt-key! :frontend/analytics [_ _] nil)

(defmethod ig/init-key :logging.adapter/telemere
  [_ {:keys [level]}]
  (t/make-logger {:level level}))

(defmethod ig/init-key :frontend/logging
  [_ {:keys [level adapter loki-endpoint]}]
  (log/init! adapter
             {:level level
              :loki-endpoint loki-endpoint})
  (log/log! adapter
            {:id ::frontend-logging-started
             :level :info
             :msg "Frontend logging system initialized"})
  adapter)

(defmethod ig/halt-key! :frontend/logging
  [_ logger]
  (log/log! logger
            {:id ::frontend-logging-stopped
             :level :info
             :msg "Frontend logging system stopped"}))

(defmethod ig/init-key :i18n.adapter/tempura
  [_ {:keys [debug?]}]
  (i18n-tempura/make-translator debug?
                                mm-i18n-dict-txt/dict
                                mm-i18n-dict-res/dict))

(defmethod ig/init-key :frontend/translator
  [_ {:keys [adapter logger]}]
  (log/log! logger
            {:id ::translator-started
             :level :info
             :msg "Translator started"})
  adapter)

(defmethod ig/halt-key! :frontend/translator [_ _] nil)

(def development-config
  {:logging.adapter/telemere {:level :debug}
   :frontend/logging {:level :debug
                      :adapter (ig/ref :logging.adapter/telemere)
                      :loki-endpoint conf/LOKI_ENDPOINT}
   :frontend/error-tracking {:logger (ig/ref :frontend/logging)}
   :i18n.adapter/tempura {:debug? true}
   :frontend/translator {:adapter (ig/ref :i18n.adapter/tempura)
                         :logger (ig/ref :frontend/logging)}
   :frontend/app-db {:init-db-event ::initialize-db
                     :translator (ig/ref :frontend/translator)
                     :logger (ig/ref :frontend/logging)}
   :frontend/router {:routes mm-fe-routes/routes
                     :logger (ig/ref :frontend/logging)}
   :frontend/history {:router (ig/ref :frontend/router)
                      :app-db (ig/ref :frontend/app-db)
                      :logger (ig/ref :frontend/logging)}})

(def production-config
  {:logging.adapter/telemere {:level :info}
   :frontend/logging {:level :info
                      :adapter (ig/ref :logging.adapter/telemere)
                      :loki-endpoint conf/LOKI_ENDPOINT}
   :frontend/error-tracking {:logger (ig/ref :frontend/logging)}
   :frontend/analytics {:api-key conf/POSTHOG_API_KEY
                        :api-host "https://eu.i.posthog.com"
                        :person-profiles "always"
                        :logger (ig/ref :frontend/logging)}
   :i18n.adapter/tempura {:debug? false}
   :frontend/translator {:adapter (ig/ref :i18n.adapter/tempura)
                         :logger (ig/ref :frontend/logging)}
   :frontend/app-db {:init-db-event ::initialize-db
                     :translator (ig/ref :frontend/translator)
                     :logger (ig/ref :frontend/logging)}
   :frontend/router {:routes mm-fe-routes/routes
                     :logger (ig/ref :frontend/logging)}
   :frontend/history {:router (ig/ref :frontend/router)
                      :app-db (ig/ref :frontend/app-db)
                      :logger (ig/ref :frontend/logging)}})

(def frontend-config
  (if (= "development" conf/ENV) development-config production-config))

(defonce system (atom nil))

(defn start-system!
  "Start the integrant system and dispatch failure event on error.
   
   Returns the initialized system on success, throws on critical failures."
  []
  (when-not @system
    (try (let [sys (ig/init frontend-config)]
           (reset! system sys)
           sys)
         (catch :default e
           ;; Dispatch failure event so UI shows error page instead of spinner.
           (rf/dispatch-sync [::system-failed e])
           (throw e)))))

(defn stop-system! [] (when @system (ig/halt! @system) (reset! system nil)))

(defn restart-system!
  []
  (stop-system!)
  (rf/dispatch-sync [::initialize-db])
  (start-system!))
