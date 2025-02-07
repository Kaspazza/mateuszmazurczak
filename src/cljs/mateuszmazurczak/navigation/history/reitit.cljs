(ns mateuszmazurczak.navigation.history.reitit
  "Adapter for reitit history
  See [reitit history](https://github.com/metosin/reitit/blob/master/modules/reitit-frontend/src/reitit/frontend/history.cljs) for details"
  (:require
   [mateuszmazurczak.navigation.history.protocol :as mm-nav-history-protocol]
   [mateuszmazurczak.navigation.utils            :as mm-nav]
   [reitit.frontend.controllers                  :as reitit-fe-controllers]
   [reitit.frontend.history                      :as reitit-fe-history]))

(defrecord History [history]
  mm-nav-history-protocol/History
    (init! [_] nil)
    (navigate! [_ page] (mm-nav/navigate! page))
    (stop! [_] (reitit-fe-history/stop! history))
    (href-delta [_ match name path-params query-params]
      (reitit-fe-history/href history
                              (or name (get-in match [:data :name]))
                              (merge (:path-params match) path-params)
                              (merge (:query-params match) query-params)))
    (href [_ name path-params query-params]
      (reitit-fe-history/href history name path-params query-params)))

(defn make-history
  "Make an history (fe-history/History) instance for pushy.
  Decides what is send to backend, and so its routing or not.

  Params:
  * `router` router used by that history
  * `on-navigate` function called when a route changed. Takes two parameters, `match` and `history`"
  [router on-navigate]
  (->History
   (reitit-fe-history/start! router on-navigate {:use-fragment false})))

(defonce controllers-match-storage (atom nil))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn apply-controllers
  "To use it, this function must be executed in the `on-navigate` make-history function (or somewhere where navigation happens).
   After that :controllers can be added to routes
   More here: https://github.com/metosin/reitit/blob/master/doc/frontend/controllers.md"
  [new-match]
  (swap! controllers-match-storage (fn [old-match]
                                     (when new-match
                                       (assoc
                                        new-match
                                        :controllers
                                        (reitit-fe-controllers/apply-controllers
                                         (:controllers old-match)
                                         new-match))))))
