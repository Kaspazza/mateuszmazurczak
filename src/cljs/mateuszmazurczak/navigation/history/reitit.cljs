(ns mateuszmazurczak.navigation.history.reitit
  "Adapter for reitit history
  See [reitit history](https://github.com/metosin/reitit/blob/master/modules/reitit-frontend/src/reitit/frontend/history.cljs) for details"
  (:require
   [clojure.string              :as str]
   [reitit.frontend.controllers :as reitit-fe-controllers]
   [reitit.frontend.history     :as reitit-fe-history]))

(defonce controllers-state (atom nil))

(defn apply-controllers
  "Apply controllers to a route match"
  [old-controllers new-match]
  (reitit-fe-controllers/apply-controllers old-controllers new-match))

(defn update-match-fragment
  "Adds fragment in the case when match is coming from ring handler"
  [match]
  (let [window-hash (str (.. js/window -location -hash))]
    (if (or (some? (:fragment match)) (str/blank? window-hash))
      match
      (assoc match :fragment (subs window-hash 1)))))

(defn process-route-change
  "Process a route change event with controllers and fragment handling"
  [match dispatch-fn]
  (let [match (update-match-fragment match)
        match-with-controllers
        (when match
          (swap! controllers-state (fn [old-state]
                                     (assoc match
                                            :controllers
                                            (apply-controllers (:controllers
                                                                old-state)
                                                               match)))))
        transformed-route
        (when match-with-controllers
          {:route-name (get-in match-with-controllers [:data :name])
           :panel-id
           (get-in match-with-controllers [:data :panel-id] :panels/not-found)
           :path-parameters (:path-params match-with-controllers)
           :query-parameters (:query-params match-with-controllers)})]
    ;; Dispatch route change event with processed and transformed match
    (dispatch-fn transformed-route)))

(defn start-history!
  "Start history with controller management"
  [router on-route-change-fn]
  (reitit-fe-history/start! router
                            (fn [match _history]
                              (process-route-change match on-route-change-fn))
                            {:use-fragment false}))

(defn stop-history! "Stop history" [history] (reitit-fe-history/stop! history))

(defn navigate
  "Navigate to URL, with optional history preservation"
  [history route-name {:keys [path-params query-params replace]}]
  (reitit-fe-history/navigate history
                              route-name
                              {:replace replace
                               :path-params path-params
                               :query-params query-params}))
(defn set-query!
  "Change only query-params"
  [history query-params replace]
  (reitit-fe-history/set-query history query-params {:replace replace}))

(defn href
  "Generate URL for a route"
  [history route-name path-params query-params]
  (reitit-fe-history/href history route-name path-params query-params))
