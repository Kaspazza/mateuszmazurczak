(ns mateuszmazurczak.adapters.events.reframe.i18n
  "Re-frame adapter for i18n events.
   
   Implements i18n events from events/registry.cljc.
   This is INTERNAL adapter code - UI components should dispatch via events/dispatch!"
  (:require
   [mateuszmazurczak.domain.cache.registry :as cache-registry]
   [mateuszmazurczak.domain.i18n.language  :as i18n-lang]
   [mateuszmazurczak.domain.state.registry :as state-registry]
   [mateuszmazurczak.ports.cache           :as cache]
   [re-frame.core                          :as rf]))

(rf/reg-fx ::set-cookie (fn [[key lang-id]] (cache/set-item! key lang-id)))

(def handlers
  "I18n event handlers exported as data.
   
   Each handler is a pure function that will be registered with re-frame
   by the wiring namespace. The registry metadata determines whether it's
   registered as :db or :fx handler.
   
   Handler signature:
   - :db handlers: (fn [db event-vec] new-db)
   - :fx handlers: (fn [cofx event-vec] effects-map)"
  {:i18n/change-lang (fn [{:keys [db]} [_ lang-str]]
                       (let [lang (-> lang-str
                                      i18n-lang/ui-str-to-id)]
                         {:db (assoc-in db state-registry/*lang-path* lang)
                          ::set-cookie [(get-in cache-registry/domains [:app-db :lang :key]) lang]
                          :fx [[:dispatch [:nav/change-query-parameters! {:lang lang}]]]}))})
(defn init!
  "Initialize i18n effects.
   
   Note: Effects (rf/reg-fx) are registered at namespace load time above.
   This function exists for explicit initialization if needed."
  []
  ;; Effects already registered by loading the ns
  nil)
