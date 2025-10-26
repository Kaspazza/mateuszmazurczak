(ns mateuszmazurczak.state.adapters.reframe.watch
  "Re-frame adapter for state changes watch (subscriptions).
   
   Organization:
   - All rf/reg-sub calls define subscriptions at namespace load time
   - Exported `watch` set lists all implemented watch-ids for validation
   - `get-watch-fn` returns re-frame's subscribe function
   - `init!` provides explicit initialization hook for system wiring but it's not necessairily needed for reframe, this ns just needs to be compiled"
  (:require
   [mateuszmazurczak.events            :as events]
   [mateuszmazurczak.frontend-i18n     :as fi18n]
   [mateuszmazurczak.i18n.language     :as i18n-lang]
   [mateuszmazurczak.navigation.core   :as nav-core]
   [mateuszmazurczak.pages.home.schema :as home-schema]
   [re-frame.core                      :as rf]))

;; =============================================================================
;; Navigation watch
;; =============================================================================

(rf/reg-sub :nav/current-route (fn [db _] (:current-route db)))

(rf/reg-sub :nav/current-panel
            :<-
            [:nav/current-route]
            (fn [current-route] (:panel-id current-route)))

(rf/reg-sub :nav/path-params
            :<-
            [:nav/current-route]
            (fn [current-route _] (nav-core/path-params current-route)))

(rf/reg-sub :nav/query-params
            :<-
            [:nav/current-route]
            (fn [current-route _] (nav-core/query-params current-route)))

(rf/reg-sub :nav/active-route?
            :<-
            [:nav/current-route]
            (fn [current-route [_ route-name]]
              (= route-name (nav-core/route-name current-route))))

;; =============================================================================
;; Page watch
;; =============================================================================

(rf/reg-sub :home/raw-data (fn [db _] (get-in db [:pages :home])))

(rf/reg-sub :panels/home
            :<-
            [:home/raw-data]
            (fn [raw-data _]
              (let [processed-data (-> raw-data
                                       fi18n/translate-tree
                                       events/dispatch-tree)
                    valid? (home-schema/valid-home-page-data? processed-data)]
                (if valid?
                  {:data processed-data
                   :valid? valid?}
                  {:data processed-data
                   :valid? valid?
                   :error {:id ::home-translation-failed
                           :data (home-schema/explain-home-page-data
                                  processed-data)
                           :actual-data processed-data}}))))

;; =============================================================================
;; State watch
;; =============================================================================

(rf/reg-sub :logger (fn [db _] (:logger db)))

;; =============================================================================
;; i18n watch
;; =============================================================================

(rf/reg-sub :i18n/lang (fn [db _] (:lang db)))

(rf/reg-sub :i18n/translator (fn [db _] (:translator db)))

(rf/reg-sub :i18n/lang-str
            :<-
            [:i18n/lang]
            (fn [lang-id]
              (-> lang-id
                  i18n-lang/id-to-str)))

;; =============================================================================
;; Adapter interface
;; =============================================================================

(def watch
  "Set of all watch-ids implemented by this adapter.
   
   Used by the port for validation during system wiring."
  #{:nav/current-route :nav/current-panel :nav/path-params :nav/query-params
    :nav/active-route? :panels/home :home/raw-data :logger :i18n/lang
    :i18n/translator :i18n/lang-str})

(defn get-watch-fn
  "Returns the re-frame subscribe function.
   
   This function takes a watch vector (e.g., [:nav/current-route])
   and returns a ratom that reactively updates when state changes.
   
   Returns: re-frame.core/subscribe"
  []
  rf/subscribe)

(defn init!
  "Initialize the re-frame watch adapter.
   
   All subscriptions (rf/reg-sub) are registered at namespace load time.
   This function exists for explicit initialization in integrant system.
   
   Returns: nil (pure side-effects)"
  []
  ;; All rf/reg-sub calls execute when namespace is loaded
  ;; This function provides explicit initialization hook for system wiring
  nil)
