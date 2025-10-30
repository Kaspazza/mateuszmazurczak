(ns mateuszmazurczak.adapters.events.reframe.i18n
  "Re-frame adapter for i18n events.
   
   Implements i18n events from events/registry.cljc.
   This is INTERNAL adapter code - UI components should dispatch via events/dispatch!"
  (:require
   [mateuszmazurczak.domain.i18n.language :as i18n-lang]
   [re-frame.core                         :as rf]))

(rf/reg-fx ::set-cookie
           (fn [[_key lang-id]]
             ;; Calls back to frontend-i18n port
             ;; We use runtime require to avoid circular dependency
             (let [frontend-i18n (js/require "mateuszmazurczak.frontend_i18n")
                   save-fn (.-save_lang_cookie_BANG_ frontend-i18n)]
               (save-fn lang-id))))

(def handlers
  "I18n event handlers exported as data.
   
   Each handler is a pure function that will be registered with re-frame
   by the wiring namespace. The registry metadata determines whether it's
   registered as :db or :fx handler.
   
   Handler signature:
   - :db handlers: (fn [db event-vec] new-db)
   - :fx handlers: (fn [cofx event-vec] effects-map)"
  {:i18n/change-lang
   (fn [{:keys [db]} [_ lang-evt]]
     (let [lang (-> lang-evt
                    .-target
                    .-value
                    i18n-lang/ui-str-to-id)]
       {:db (assoc db :lang lang)
        ::set-cookie ["lang" lang]
        :fx [[:dispatch [:nav/change-query-parameters! {:lang lang}]]]}))})

(defn init!
  "Initialize i18n effects.
   
   Note: Effects (rf/reg-fx) are registered at namespace load time above.
   This function exists for explicit initialization if needed."
  []
  ;; Effects already registered by loading the ns
  nil)
