(ns mateuszmazurczak.navigation.core
  "Public API for navigation in the application.
   This namespace encapsulates all routing and history implementation details,
   providing a clean interface for UI components."
  (:require
   [day8.re-frame.tracing               :refer [fn-traced]]
   [mateuszmazurczak.navigation.history :as history]
   [mateuszmazurczak.navigation.router  :as router] ;;
   [re-frame.core                       :as rf]))


(defn href
  "Generate a URL for the given route."
  ([] (href nil nil nil))
  ([route-name] (href route-name nil nil))
  ([route-name path-params] (href route-name path-params nil))
  ([route-name path-params query-params]
   (history/href route-name path-params query-params)))

(defn change-query-parameters!
  "Change only query-params"
  ([query-params] (change-query-parameters! query-params true))
  ([query-params preserve-history?]
   (history/change-query-params! query-params preserve-history?)))

(defn navigate!
  "Navigate to URL, with optional history preservation"
  ([route-name] (navigate! route-name {}))
  ([route-name params] (navigate! route-name params true))
  ([route-name {:keys [path-parameters query-parameters]} preserve-history?]
   (history/navigate! route-name
                      {:path-parameters path-parameters
                       :query-parameters query-parameters}
                      preserve-history?)))

(rf/reg-sub :nav/current-route (fn [db _] (:current-route db)))

(rf/reg-sub :nav/path-params
            :<-
            [:nav/current-route]
            (fn [current-route _] (router/current-path-params current-route)))

(rf/reg-sub :nav/query-params
            :<-
            [:nav/current-route]
            (fn [current-route _] (router/current-query-params current-route)))

(rf/reg-sub :nav/active-route?
            :<-
            [:nav/current-route]
            (fn [current-route [_ route-name]]
              (= route-name (router/current-route-name current-route))))

;;TODO query parameters with language should always stay in the browser path, they get removed rn - to be checked
;;TODO history and router could be kept together in core.cljs here - to think about
(rf/reg-fx ::change-query-parameters
           (fn [[query-params]] (change-query-parameters! query-params)))

(rf/reg-fx ::navigate
           (fn [[route-name path-params query-params]]
             (navigate! route-name
                        {:path-parameters path-params
                         :query-parameters query-params})))

(rf/reg-fx ::navigate-no-history
           (fn [[route-name path-params query-params]]
             (navigate! route-name
                        {:path-parameters path-params
                         :query-parameters query-params}
                        false)))

(rf/reg-event-fx :nav/navigate
                 (fn [cofx [_ route-name path-params query-params]]
                   (-> cofx
                       (assoc ::navigate
                              [route-name path-params query-params]))))

(rf/reg-event-fx :nav/change-query-parameters!
                 (fn [cofx [_ query-params]]
                   (-> cofx
                       (assoc ::change-query-parameters [query-params]))))

(rf/reg-event-fx :nav/navigate-no-history
                 (fn [_ [_ route-name path-params query-params]]
                   {::navigate-no-history
                    [route-name path-params query-params]}))

(rf/reg-event-fx :nav/route-changed
                 (fn-traced [{:keys [db]} [_ route-data]]
                            {:db (assoc db :current-route route-data)
                             :handle-fragment-scroll (:fragment route-data)}))
