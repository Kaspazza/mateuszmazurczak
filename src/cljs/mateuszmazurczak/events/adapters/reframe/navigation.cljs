(ns mateuszmazurczak.events.adapters.reframe.navigation
  "Re-frame adapter for navigation events.
   
   Implements navigation events from events/registry.cljc.
   This is INTERNAL adapter code - UI components should dispatch via events/dispatch!"
  (:require
   [mateuszmazurczak.navigation.core :as nav-core]
   [mateuszmazurczak.utils.dom       :as utils-dom]
   [re-frame.core                    :as rf]))

;; =============================================================================
;; Subscriptions
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
;; Effects 
;; =============================================================================

(rf/reg-fx ::handle-fragment-scroll
           (fn [fragment]
             (if fragment
               (let [element-id (->> fragment
                                     js/CSS.escape
                                     (str "#"))]
                 (utils-dom/delay-page-load
                  #(utils-dom/on-element-exist
                    element-id
                    (fn [el]
                      ;; Wait one more frame to ensure all layout is stable
                      (js/requestAnimationFrame
                       (.scrollIntoView el
                                        (clj->js {:behavior "smooth"
                                                  :block "start"})))))))
               (.scrollTo js/window
                          (clj->js {:top 0
                                    :left 0
                                    :behavior "smooth"})))))

(rf/reg-fx ::change-query-parameters
           (fn [[query-params]]
             (nav-core/change-query-parameters! query-params)))

(rf/reg-fx ::navigate
           (fn [[route-name path-params query-params]]
             (nav-core/navigate! route-name
                                 {:path-parameters path-params
                                  :query-parameters query-params})))

(rf/reg-fx ::navigate-no-history
           (fn [[route-name path-params query-params]]
             (nav-core/navigate! route-name
                                 {:path-parameters path-params
                                  :query-parameters query-params}
                                 false)))

;; =============================================================================
;; Event Handlers (implementing events/registry.cljs contract)
;; =============================================================================

(def handlers
  "Navigation event handlers exported as data.
   
   Each handler is a pure function that will be registered with re-frame
   by the wiring namespace. The registry metadata determines whether it's
   registered as :db or :fx handler.
   
   Handler signature:
   - :db handlers: (fn [db event-vec] new-db)
   - :fx handlers: (fn [cofx event-vec] effects-map)"
  {:nav/navigate (fn [_cofx [_ route-name path-params query-params]]
                   {::navigate [route-name path-params query-params]})
   :nav/change-query-parameters! (fn [_cofx [_ query-params]]
                                   {::change-query-parameters [query-params]})
   :nav/navigate-no-history (fn [_ [_ route-name path-params query-params]]
                              {::navigate-no-history
                               [route-name path-params query-params]})
   :nav/route-changed
   (fn [{:keys [db]} [_ route-data]]
     (let [route-lang (get-in route-data [:query-parameters :lang])]
       (merge {:db (assoc db :current-route route-data)
               ::handle-fragment-scroll (:fragment route-data)}
              (when-not route-lang
                {::change-query-parameters [{:lang (:lang db)}]}))))})

;; =============================================================================
;; Initialization (for effects and subscriptions)
;; =============================================================================

(defn init!
  "Initialize navigation effects and subscriptions.
   
   Note: Effects (rf/reg-fx) are registered at namespace load time above.
   Subscriptions are also registered at namespace load time at the top.
   This function exists for explicit initialization if needed."
  []
  ;; Effects and subscriptions already registered by compiling ns
  nil)
