(ns mateuszmazurczak.adapters.state.reframe.watch
  "Re-frame adapter for state changes watch (subscriptions).
   
   Organization:
   - All rf/reg-sub calls define subscriptions at namespace load time
   - Exported `watch` set lists all implemented watch-ids for validation
   - `get-watch-fn` returns re-frame's subscribe function
   - `init!` provides explicit initialization hook for system wiring but it's not necessairily needed for reframe, this ns just needs to be compiled"
  (:require
   [clojure.set                                        :as set]
   [mateuszmazurczak.adapters.state.reframe.admin-page :as admin-page-watch]
   [mateuszmazurczak.adapters.state.reframe.aoc        :as aoc-watch]
   [mateuszmazurczak.adapters.state.reframe.theme      :as theme-watch]
   [mateuszmazurczak.application.home.page-data        :as home-page-data]
   [mateuszmazurczak.domain.i18n.language              :as i18n-lang]
   [mateuszmazurczak.domain.state.registry             :as state-registry]
   [mateuszmazurczak.ports.navigation                  :as nav-core]
   [re-frame.core                                      :as rf]))

;; =============================================================================
;; Navigation watch
;; =============================================================================

(rf/reg-sub :nav/current-route (fn [db _] (get-in db state-registry/*current-route-path*)))

(rf/reg-sub :nav/current-page
            :<-
            [:nav/current-route]
            (fn [current-route] (:page-id current-route)))

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
            (fn [current-route [_ route-name]] (= route-name (nav-core/route-name current-route))))

;; =============================================================================
;; Page watch
;; =============================================================================

(rf/reg-sub :home/raw-data (fn [db _] (get-in db state-registry/*home-page-path*)))

(rf/reg-sub :pages/home
            :<-
            [:home/raw-data]
            (fn [raw-data _]
              (home-page-data/prepare-ui-data raw-data)))

;; =============================================================================
;; State watch
;; =============================================================================

(rf/reg-sub :logger (fn [db _] (get-in db state-registry/*logger-path*)))

;; =============================================================================
;; i18n watch
;; =============================================================================

(rf/reg-sub :i18n/lang (fn [db _] (get-in db state-registry/*lang-path*)))

(rf/reg-sub :i18n/translator (fn [db _] (get-in db state-registry/*translator-path*)))

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
  (set/union #{:nav/current-route :nav/current-page :nav/path-params :nav/query-params
               :nav/active-route? :pages/home :home/raw-data :logger :i18n/lang :i18n/translator
               :i18n/lang-str}
             aoc-watch/watch
             admin-page-watch/watch
             theme-watch/watch))

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
