(ns mateuszmazurczak.navigation.adapters.reframe
  "Re-frame adapter for navigation - handles re-frame subscriptions."
  (:require
   [mateuszmazurczak.navigation.core :as nav-core]
   [re-frame.core                    :as rf]))

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

(defn init!
  "Initialize re-frame navigation adapter.
   Registers navigation-related subscriptions by loading this namespace.
   Event handlers are registered by events/adapters/reframe/navigation.cljs"
  []
  nil)
