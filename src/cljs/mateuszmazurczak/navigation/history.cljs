(ns mateuszmazurczak.navigation.history
  "Implement a `fe-history/History` instance to manage browser history for spa"
  (:require
   [clojure.string                               :as str]
   [mateuszmazurczak.navigation.history.protocol :as mm-nav-history]
   [mateuszmazurczak.navigation.history.reitit   :as mm-nav-history-reitit]
   [mateuszmazurczak.navigation.router           :as mm-fe-router]
   [mount.core                                   :refer [defstate]]
   [re-frame.core                                :as rf]))

(defn update-match-fragment
  "Adds fragment in the case when match is comming from ring handler.
   https://github.com/ring-clojure/ring/issues/207"
  [match]
  (let [window-hash (str (.. js/window -location -hash))]
    (if (or (some? (:fragment match)) (str/blank? window-hash))
      match
      (assoc match :fragment (subs window-hash 1)))))

(defstate history
          :start (try (mm-nav-history-reitit/make-history
                       (:router @mm-fe-router/router)
                       (fn [match _history]
                         (let [match (update-match-fragment match)]
                           (rf/dispatch [::mm-fe-router/new-route-match
                                         match]))))
                      (catch :default e
                        (ex-info "History component did not start" {:e e})))
          :stop (mm-nav-history/stop! @history))

(defn init! [] (mm-nav-history/init! @history))

(defn href
  ([] (mm-nav-history/href @history nil nil nil))
  ([route-name] (mm-nav-history/href @history route-name nil nil))
  ([route-name path-param]
   (mm-nav-history/href @history route-name path-param nil))
  ([route-name path-param query-param]
   (mm-nav-history/href @history route-name path-param query-param)))

(defn href-delta
  "Search for an hyperlink relatively to the current
  So all nil values set below will be replaced with the value of the current match
  Params:
  * `route-name` name of the route to navigate, replaced with current route name if nil
  * `path-params` add the parameters in that map to the current parameter map
  * `query-params` add the parameters in that map to the current query parameters"
  ([route-name path-params query-params]
   (mm-nav-history/href-delta
    @history
    @(rf/subscribe [:mateuszmazurczak.navigation.router/route-match])
    route-name
    path-params
    query-params))
  ([route-name path-params]
   (mm-nav-history/href-delta
    @history
    @(rf/subscribe [:mateuszmazurczak.navigation.router/route-match])
    route-name
    path-params
    {}))
  ([route-name]
   (mm-nav-history/href-delta
    @history
    @(rf/subscribe [:mateuszmazurczak.navigation.router/route-match])
    route-name
    {}
    {})))

(rf/reg-fx ::history-change
           (fn [[href]] (mm-nav-history/navigate! @history href)))
