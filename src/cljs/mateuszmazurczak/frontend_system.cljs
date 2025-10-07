(ns mateuszmazurczak.frontend-system
  "Frontend Integrant system configuration"
  (:require
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
   [mateuszmazurczak.navigation.router.reitit :as router-reitit]))

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

(defmethod ig/init-key :frontend/state
  [_ {:keys [translator logger]}]
  (log/log! logger
            {:id ::state-initialized
             :level :info
             :msg "Frontend state initialized"})
  {:name "mateuszmazurczak"
   :current-route {:panel-id :panels/pending}
   :lang (mm-i18n-translate/language-strategy)
   :translator translator})

(defmethod ig/halt-key! :frontend/state [_ _] nil)

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

(defonce system (atom nil))

(defn start-system!
  "Start the integrant system.
   
   Returns the initialized system on success, throws on critical failures.
   Callers are responsible for handling system initialization errors."
  []
  (when-not @system
    (let [sys (ig/init conf/frontend-config)]
      (reset! system sys)
      sys)))

(defn stop-system! [] (when @system (ig/halt! @system) (reset! system nil)))

(defn restart-system! [] (stop-system!) (start-system!))
