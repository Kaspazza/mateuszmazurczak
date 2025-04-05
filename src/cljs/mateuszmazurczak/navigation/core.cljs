(ns mateuszmazurczak.navigation.core
  "Public API for navigation in the application.
   This namespace encapsulates all routing and history implementation details,
   providing a clean interface for UI components."
  (:require
   [day8.re-frame.tracing                      :refer [fn-traced]]
   [mateuszmazurczak.navigation.history.reitit :as history-reitit]
   [mateuszmazurczak.navigation.router.reitit  :as router-reitit]
   [mateuszmazurczak.navigation.routes         :as mm-fe-routes]
   [mount.core                                 :refer [defstate]]
   [re-frame.core                              :as rf]))

(defn- on-element-exist
  "Waits for `selector` element to appear in document and executes `on-exist-fn`"
  [selector on-exist-fn]
  (-> (new js/Promise
           (fn [resolve]
             (when (.querySelector js/document selector)
               (resolve (.querySelector js/document selector)))
             #_{:clj-kondo/ignore [:inline-def]}
             (def observer
               (new js/MutationObserver
                    (fn [_mutations]
                      (when (.querySelector js/document selector)
                        (.disconnect observer)
                        (resolve (.querySelector js/document selector))))))
             (.observe observer
                       (.-body js/document)
                       #js {:childList true
                            :subtree true})))
      (.then on-exist-fn)))

(defn init-history!
  "Initialize history tracking"
  [router]
  (history-reitit/start-history!
   router
   (fn [route-data] (rf/dispatch [:nav/route-changed route-data]))))

(defn stop-history!
  "Stop history tracking"
  [history]
  (history-reitit/stop-history! history))

(defstate router
          :start (try (js/console.log "Router started")
                      (router-reitit/create-router mm-fe-routes/routes)
                      (catch :default e
                        (js/console.error "Router failed to start" e)
                        nil))
          :stop (prn "stopped"))

(defstate history
          :start (try (js/console.log "History started")
                      (init-history! @router)
                      (catch :default e
                        (js/console.error "History component failed to start:"
                                          e)
                        nil))
          :stop (when @history (stop-history! @history)))

(defn route-name
  "Get the name of the current route"
  [current-route]
  (:route-name current-route))

(defn path-params
  "Get path parameters from current route"
  [current-route]
  (:path-parameters current-route))

(defn query-params
  "Get query parameters from current route"
  [current-route]
  (:query-parameters current-route))

(defn route-details
  "Find route data for a given path"
  [path]
  (when-let [match (router-reitit/match-by-path @router path)]
    {:route-name (router-reitit/route-name-from-match match)
     :panel-id (router-reitit/panel-id-from-match match)
     :path-parameters (router-reitit/path-params-from-match match)
     :query-parameters (router-reitit/query-params-from-match match)}))


(defn href
  "Generate a URL for the given route."
  ([] (href nil nil nil))
  ([route-name] (href route-name nil nil))
  ([route-name path-params] (href route-name path-params nil))
  ([route-name path-params query-params]
   (history-reitit/href @history route-name path-params query-params)))

(defn change-query-parameters!
  "Change only query-params"
  ([query-params] (change-query-parameters! query-params true))
  ([query-params preserve-history?]
   (history-reitit/set-query! @history query-params (not preserve-history?))))

(defn navigate!
  "Navigate to URL, with optional history preservation"
  ([route-name] (navigate! route-name {}))
  ([route-name params] (navigate! route-name params true))
  ([route-name {:keys [path-parameters query-parameters]} preserve-history?]
   (history-reitit/navigate @history
                            route-name
                            {:replace (not preserve-history?)
                             :path-params path-parameters
                             :query-params query-parameters})))


(rf/reg-sub :nav/current-route (fn [db _] (:current-route db)))

(rf/reg-sub :nav/current-panel
            :<-
            [:nav/current-route]
            (fn [current-route] (:panel-id current-route)))

(rf/reg-sub :nav/path-params
            :<-
            [:nav/current-route]
            (fn [current-route _] (path-params current-route)))

(rf/reg-sub :nav/query-params
            :<-
            [:nav/current-route]
            (fn [current-route _] (query-params current-route)))

(rf/reg-sub :nav/active-route?
            :<-
            [:nav/current-route]
            (fn [current-route [_ route-name]]
              (= route-name (route-name current-route))))

(rf/reg-fx ::handle-fragment-scroll
           (fn [fragment]
             (if fragment
               (on-element-exist (->> fragment
                                      js/CSS.escape
                                      (str "#"))
                                 #(.scrollIntoView %))
               (.scrollTo js/window 0 0))))

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
                   {::navigate [route-name path-params query-params]}))

(rf/reg-event-fx :nav/change-query-parameters!
                 (fn [cofx [_ query-params]]
                   {::change-query-parameters [query-params]}))

(rf/reg-event-fx :nav/navigate-no-history
                 (fn [_ [_ route-name path-params query-params]]
                   {::navigate-no-history
                    [route-name path-params query-params]}))

(rf/reg-event-fx
 :nav/route-changed
 (fn-traced [{:keys [db]} [_ route-data]]
            (let [route-lang (get-in route-data [:query-parameters :lang])]
              (merge {:db (assoc db :current-route route-data)
                      ::handle-fragment-scroll (:fragment route-data)}
                     (when-not route-lang
                       {::change-query-parameters [{:lang (:lang db)}]})))))
