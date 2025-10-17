(ns mateuszmazurczak.i18n.adapters.reframe
  "Re-frame adapter for i18n - handles state management via re-frame.
  
  This is an INTERNAL adapter - UI components should NEVER require this namespace.
  Only frontend-i18n (the port) uses this adapter."
  (:require
   [mateuszmazurczak.i18n          :as i18n]
   [mateuszmazurczak.i18n.language :as i18n-lang]
   [re-frame.core                  :as rf]))

;; Subscriptions

(rf/reg-sub ::lang (fn [db _] (:lang db)))

(rf/reg-sub ::translator (fn [db _] (:translator db)))

(rf/reg-sub ::lang-str
            :<-
            [::lang]
            (fn [lang-id]
              (-> lang-id
                  i18n-lang/id-to-str)))

;; Public API for frontend-i18n port (NOT for UI components)

(defn reactive-tr
  "Reactive translation function using re-frame subscriptions.
  
  INTERNAL - called by frontend-i18n port, not by UI components directly.
  
  Params:
  * `tr-id` - translation key (keyword)
  * `params` - optional map of parameters for interpolation (defaults to nil)
  
  Returns: translated string (reactive)"
  ([tr-id] (reactive-tr tr-id nil))
  ([tr-id params]
   (let [lang @(rf/subscribe [::lang])
         translator @(rf/subscribe [::translator])]
     (when translator
       (if params
         (i18n/tr translator lang tr-id params)
         (i18n/tr translator lang tr-id))))))

(defn current-language
  "Get current language from re-frame state.
  
  INTERNAL - called by frontend-i18n port, not by UI components directly.
  
  Returns: language keyword (reactive)"
  []
  @(rf/subscribe [::lang]))

(defn current-language-str
  "Get current language as UI string from re-frame state.
  
  INTERNAL - called by frontend-i18n port, not by UI components directly.
  
  Returns: language string (reactive)"
  []
  @(rf/subscribe [::lang-str]))

;;TODO this may need to be changed to think about
(defn change-language!
  "Change language via re-frame event.
  
  INTERNAL - called by frontend-i18n port, not by UI components directly.
  
  Params:
  * `lang-evt` - DOM event from language selector"
  [lang-evt]
  (rf/dispatch [:i18n/change-lang lang-evt]))

;; Initialization

(defn init!
  "Initialize the re-frame adapter.
  
  This should be called during system startup to ensure all
  re-frame handlers are registered before UI renders.
  
  Returns: nil (pure side-effects)"
  []
  ;; All handlers are registered at namespace load time via rf/reg-*
  ;; This function exists for explicit initialization in integrant system
  nil)
