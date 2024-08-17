(ns mateuszmazurczak.fe.history
  "Implement a `fe-history/History` instance to manage browser history for spa"
  (:require
   [automaton-core.log              :as core-log]
   [automaton-web.events-proxy      :as web-events-proxy]
   [automaton-web.fe.history        :as web-fe-history]
   [automaton-web.fe.history.reitit :as fe-history-reitit]
   [clojure.string                  :as str]
   [day8.re-frame.tracing           :refer-macros [fn-traced]]
   [mateuszmazurczak.fe.router      :as mateuszmazurczak-fe-router]
   [mount.core                      :refer [defstate]]))

(defn update-match-fragment
  "Adds fragment in the case when match is comming from ring handler.
   https://github.com/ring-clojure/ring/issues/207"
  [match]
  (let [window-hash (str (.. js/window -location -hash))]
    (if (or (some? (:fragment match)) (str/blank? window-hash))
      match
      (assoc match :fragment (subs window-hash 1)))))

(defstate history
          :start (try (fe-history-reitit/make-history
                       (:router @mateuszmazurczak-fe-router/router)
                       (fn [match _history]
                         (let [match (update-match-fragment match)]
                           (web-events-proxy/dispatch [::new-route-match
                                                       match]))))
                      (catch :default e
                        (core-log/error
                         (ex-info "History component did not start" {:e e}))))
          :stop (web-fe-history/stop! @history))

(defn init!
  "See `automaton-web.fe.history.pushy/init!`"
  []
  (web-fe-history/init! @history))

(defn href
  "See `automaton-web.fe.history.pushy/href`"
  ([] (web-fe-history/href @history nil nil nil))
  ([route-name] (web-fe-history/href @history route-name nil nil))
  ([route-name path-param]
   (web-fe-history/href @history route-name path-param nil))
  ([route-name path-param query-param]
   (web-fe-history/href @history route-name path-param query-param)))

(defn href-delta
  "See `automaton-web.fe.history.pushy/href-delta`"
  [match route-name path-param query-param]
  (web-fe-history/href-delta @history match route-name path-param query-param))

(web-events-proxy/reg-event-db ::new-route-match
                               (fn-traced [db [_ match]]
                                          (assoc db :route-match match)))
