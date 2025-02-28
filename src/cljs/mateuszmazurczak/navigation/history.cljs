(ns mateuszmazurczak.navigation.history
  "History managment in browser.
   This is based on [push-state](https://developer.mozilla.org/en-US/docs/Web/API/History/pushState) browser feature.
   This feature is useful to add links in the browsers history"
  (:require
   [mateuszmazurczak.navigation.history.reitit :as history-reitit]
   [mateuszmazurczak.navigation.router         :as nav-router]
   [mount.core                                 :refer [defstate]]
   [re-frame.core                              :as rf]))

(defn init!
  "Initialize history tracking"
  [router]
  (history-reitit/start-history!
   router
   (fn [route-data] (rf/dispatch [:nav/route-changed route-data]))))

(defn stop!
  "Stop history tracking"
  [history]
  (history-reitit/stop-history! history))

(defstate history
          :start (try (js/console.log "History started")
                      (init! @nav-router/router)
                      (catch :default e
                        (js/console.error "History component failed to start:"
                                          e)
                        nil))
          :stop (when @history (stop! @history)))

(defn navigate!
  "Navigate to URL, with optional history preservation"
  [route-name {:keys [path-parameters query-parameters]} preserve-history?]
  (history-reitit/navigate @history
                           route-name
                           {:replace (not preserve-history?)
                            :path-params path-parameters
                            :query-params query-parameters}))

(defn change-query-params!
  "Change only query-params"
  [query-params preserve-history?]
  (history-reitit/set-query! @history query-params (not preserve-history?)))

(defn href
  "Generate URL for a route"
  [route-name path-params query-params]
  (history-reitit/href @history route-name path-params query-params))
