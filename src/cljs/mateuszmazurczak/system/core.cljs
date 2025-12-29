(ns mateuszmazurczak.system.core
  "Frontend Integrant system configuration"
  (:require
   [integrant.core                                     :as ig]
   [mateuszmazurczak.adapters.cache.local-storage      :as local-storage]
   [mateuszmazurczak.adapters.cache.state-persistence  :as state-persistence]
   [mateuszmazurczak.adapters.events.reframe.core      :as events-reframe]
   [mateuszmazurczak.adapters.i18n.tempura             :as i18n-tempura]
   [mateuszmazurczak.adapters.logging.telemere         :as t]
   [mateuszmazurczak.adapters.navigation.reitit-router :as router-reitit]
   [mateuszmazurczak.adapters.state.reframe.watch      :as watch-reframe]
   [mateuszmazurczak.domain.i18n.dict.resources        :as mm-i18n-dict-res]
   [mateuszmazurczak.domain.i18n.dict.text             :as mm-i18n-dict-txt]
   [mateuszmazurczak.frontend-i18n                     :as fi18n]
   [mateuszmazurczak.ports.analytics                   :as analytics]
   [mateuszmazurczak.ports.cache                       :as cache]
   [mateuszmazurczak.ports.error-tracking              :as error-tracking]
   [mateuszmazurczak.ports.events                      :as events]
   [mateuszmazurczak.ports.logging                     :as log]
   [mateuszmazurczak.ports.navigation                  :as nav]
   [mateuszmazurczak.ports.state                       :as state]
   [mateuszmazurczak.ports.theme                       :as theme]
   [mateuszmazurczak.system.config                     :as conf]
   [mateuszmazurczak.system.integrant-utils            :as ig-utils]))

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
  [_ {:keys [router _app-db _events logger]}]
  (log/log! logger
            {:id ::history-started
             :level :info
             :msg "History started"})
  (let [history (nav/init-history! router events/dispatch!)]
    (nav/set-history! history)
    history))

(defmethod ig/halt-key! :frontend/history
  [_ history]
  (when history (nav/stop-history! history) (nav/set-history! nil)))

(defmethod ig/init-key :cache.adapter/local-storage
  [_ {:keys [logger]}]
  (log/log! logger
            {:id ::cache-adapter-initialized
             :level :info
             :msg "LocalStorage cache adapter initialized"})
  local-storage/adapter)

(defmethod ig/init-key :frontend/cache
  [_ {:keys [adapter logger]}]
  (log/log! logger
            {:id ::cache-initializing
             :level :info
             :msg "Initializing cache..."})
  (cache/set-adapter! adapter)
  (cache/set-persistence-ops! state-persistence/ops)
  (log/log! logger
            {:id ::cache-initialized
             :level :info
             :msg "Cache initialized"})
  (let [persisted-state (cache/load-persisted logger)]
    (log/log! logger
              {:id ::cache-loaded-persisted
               :level :info
               :msg "Loaded persisted state"
               :data {:has-data (seq persisted-state)}})
    persisted-state))

(defmethod ig/halt-key! :frontend/cache
  [_ _]
  (cache/set-adapter! nil)
  (cache/set-persistence-ops! nil)
  nil)

(defmethod ig/init-key :events.adapter/reframe
  [_ {:keys [logger]}]
  (log/log! logger
            {:id ::events-adapter-initializing
             :level :info
             :msg "Initializing re-frame events adapter..."})
  (events-reframe/init!)
  (log/log! logger
            {:id ::events-adapter-initialized
             :level :info
             :msg "Re-frame events adapter initialized"})
  {:handlers events-reframe/handlers
   :register-fn events-reframe/register-fns
   :dispatch-fn (events-reframe/get-dispatch-fn)})

(defmethod ig/halt-key! :events.adapter/reframe [_ _] nil)

(defmethod ig/init-key :frontend/events
  [_ {:keys [logger adapter]}]
  (log/log! logger
            {:id ::events-initialized
             :level :info
             :msg "Frontend events initialized"})
  (events/wire! (:handlers adapter) (:register-fn adapter))
  (events/set-dispatch! (:dispatch-fn adapter))
  (events/get-dispatch-fn))

(defmethod ig/halt-key! :frontend/events [_ _] (events/set-dispatch! nil) nil)

(defmethod ig/init-key :watch.adapter/reframe
  [_ {:keys [logger]}]
  (log/log! logger
            {:id ::watch-adapter-initializing
             :level :info
             :msg "Initializing re-frame watch adapter..."})
  (watch-reframe/init!)
  (log/log! logger
            {:id ::watch-adapter-initialized
             :level :info
             :msg "Re-frame watch adapter initialized"})
  {:watch watch-reframe/watch
   :watch-fn (watch-reframe/get-watch-fn)})

(defmethod ig/halt-key! :watch.adapter/reframe [_ _] nil)

(defmethod ig/init-key :frontend/state
  [_ {:keys [translator logger watch-adapter persisted-cache]}]
  (log/log! logger
            {:id ::state-watch-wiring
             :level :info
             :msg "Wiring state watch..."})
  (state/wire-watch! (:watch watch-adapter) (:watch-fn watch-adapter))
  (log/log! logger
            {:id ::state-watch-wired
             :level :info
             :msg "State watch wired"})
  (let [initial-state (state/initial-state translator logger (fi18n/language-strategy))
        merged-state (merge initial-state persisted-cache)
        theme-value (get merged-state :theme)]
    (log/log! logger
              {:id ::state-initialized
               :level :info
               :msg "Frontend state initialized"
               :data {:has-translator (some? translator)
                      :has-cached-data (seq persisted-cache)
                      :theme theme-value}})
    (state/init-app-db! merged-state)
    (when theme-value
      (theme/apply-theme! theme-value)
      (log/log! logger
                {:id ::theme-applied-on-init
                 :level :info
                 :msg "Theme applied to DOM on initialization"
                 :data {:theme theme-value}}))))

(defmethod ig/halt-key! :frontend/state [_ _] (state/set-watch-fn! nil) (state/reset-app-db!) nil)

(defmethod ig/init-key :frontend/error-tracking
  [_ opts]
  (let [logger (:logger opts)]
    (ig-utils/optional-component (fn []
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

(defmethod ig/init-key :logging.adapter/telemere [_ {:keys [level]}] (t/make-logger {:level level}))

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
  (i18n-tempura/make-translator debug? mm-i18n-dict-txt/dict mm-i18n-dict-res/dict))



(defmethod ig/init-key :frontend/i18n
  [_ {:keys [translator-adapter state-adapter logger]}]
  (log/log! logger
            {:id ::frontend-i18n-started
             :level :info
             :msg "Frontend i18n initialized"
             :data {:translator translator-adapter
                    :state-adapter state-adapter}})
  ;; Returns nil - just ensures both adapters are initialized
  nil)

(defmethod ig/halt-key! :frontend/i18n [_ _] nil)

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
    (let [sys (ig/init (conf/frontend-config))]
      (reset! system sys)
      sys)))

(defn stop-system! [] (when @system (ig/halt! @system) (reset! system nil)))

(defn restart-system! [] (stop-system!) (start-system!))
