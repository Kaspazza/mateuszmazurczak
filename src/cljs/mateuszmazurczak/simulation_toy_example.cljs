(ns mateuszmazurczak.simulation-toy-example
  "Starting point of toy example"
  (:require
   [automaton-simulation-de.control        :as sim-de-control]
   [automaton-simulation-de.demo.control   :as sim-demo-control]
   [automaton-simulation-de.predicates     :as sim-pred]
   [automaton-simulation-de.transformation :as sim-trans]
   [automaton-web.components.input         :as web-input]
   [automaton-web.components.player        :as web-player]
   [automaton-web.components.table         :as web-table]
   [automaton-web.events-proxy             :as web-events-proxy]
   [cljs.reader]
   [clojure.string                         :as str]
   [day8.re-frame.tracing                  :refer-macros [fn-traced]]
   [reagent.core                           :as r]))

(def snapshot-it (r/atom 0))

(web-events-proxy/reg-sub :simulation-response
                          (fn [db _] (get-in db [::toy-example :comp-resp])))

(web-events-proxy/reg-sub :simulation-iteration-nb
                          (fn [db _] (get-in db [::toy-example :iteration-nb])))

(web-events-proxy/reg-sub :simulation-iteration-date
                          (fn [db _]
                            (get-in db [::toy-example :iteration-date])))

(web-events-proxy/reg-sub :simulation-iteration-state
                          (fn [db _]
                            (get-in db [::toy-example :iteration-state])))


(web-events-proxy/reg-event-fx
 :on-finish
 (fn-traced [{:keys [db]} [_ response]]
            {:db (assoc-in db [::toy-example :simulation-response] response)}))

(web-events-proxy/reg-event-fx
 :on-iteration
 (fn-traced
  [{:keys [db]} [_ comp-resp]]
  (let [{:automaton-simulation-de.simulation-engine/keys [iteration date state]
         :as _snapshot}
        (get-in comp-resp
                [:automaton-simulation-de.control/response
                 :automaton-simulation-de.simulation-engine/snapshot])
        state (-> state
                  (update-in [:automaton-simulation-de.rc/resource :m1]
                             dissoc
                             :automaton-simulation-de.rc/cache)
                  (update-in [:automaton-simulation-de.rc/resource :m2]
                             dissoc
                             :automaton-simulation-de.rc/cache)
                  (update-in [:automaton-simulation-de.rc/resource :m3]
                             dissoc
                             :automaton-simulation-de.rc/cache)
                  (update-in [:automaton-simulation-de.rc/resource :m4]
                             dissoc
                             :automaton-simulation-de.rc/cache))
        _ (reset! snapshot-it iteration)
        updated-db (-> db
                       (assoc-in [::toy-example :comp-resp] comp-resp)
                       (assoc-in [::toy-example :iteration-nb] iteration)
                       (assoc-in [::toy-example :iteration-date] date)
                       (assoc-in [::toy-example :iteration-state] state))]
    {:db updated-db})))

(web-events-proxy/reg-sub :simulation-player-pause
                          (fn [db _] (get-in db [::toy-example :pause?])))

(web-events-proxy/reg-event-db
 :set-simulation-pause
 (fn-traced [db [_ pause?]] (assoc-in db [::toy-example :pause?] pause?)))

(def rendering
  (sim-de-control/build-rendering-state {:computation
                                         (sim-de-control/make-computation
                                          (sim-demo-control/model-infinite)
                                          :direct)}))
(defn simulation-player
  [simulation-rendering-fn]
  [web-player/player
   {:play-fn (fn [_]
               (web-events-proxy/dispatch [:set-simulation-pause true])
               (sim-de-control/play! (simulation-rendering-fn)
                                     #(web-events-proxy/dispatch [:on-iteration
                                                                  %])))
    :pause? (web-events-proxy/subscribe-value [:simulation-player-pause])
    :pause-fn (fn [_]
                (sim-de-control/pause! (simulation-rendering-fn))
                (web-events-proxy/dispatch [:set-simulation-pause false]))
    :next-fn (fn [_]
               (let [next-iteration
                     (sim-de-control/move-x! (simulation-rendering-fn) 1)]
                 (web-events-proxy/dispatch [:on-iteration next-iteration])))
    :prev-fn (fn [_]
               (let [prev-iteration
                     (sim-de-control/move-x! (simulation-rendering-fn) -1)]
                 (web-events-proxy/dispatch [:on-iteration prev-iteration])))
    :fast-backward-fn
    (fn [_]
      (let [iteration (sim-de-control/rewind! (simulation-rendering-fn))]
        (web-events-proxy/dispatch [:on-iteration iteration])))
    :fast-forward-fn
    (fn [_]
      (let [iteration (sim-de-control/fast-forward! (simulation-rendering-fn))]
        (web-events-proxy/dispatch [:on-iteration iteration])))}])


(defn iteration-numb
  []
  (fn [] [:div
          "set iteration: "
          [web-input/number-field
           {:on-change-fn
            #(let [intended-it (- (cljs.reader/read-string %) @snapshot-it)
                   next-iteration (sim-de-control/move-x! rendering
                                                          intended-it)]
               (web-events-proxy/dispatch [:on-iteration next-iteration]))
            :value @snapshot-it}]]))

(defn parse-transformation-query
  [v]
  (try (-> v
           (str/replace #"::" ":automaton-simulation-de.predicates/")
           cljs.reader/read-string)
       (catch :default _ v)))

#_{:clj-kondo/ignore [:unused-binding]}
(defn transform-test
  [contr-output intent]
  (fn [contr-output]
    (let [snapshot (get-in
                    contr-output
                    [:automaton-simulation-de.control/response
                     :automaton-simulation-de.simulation-engine/snapshot])
          query (partial sim-pred/apply-query
                         (-> @intent
                             parse-transformation-query))
          query-valid? (vector? (parse-transformation-query @intent))]
      (web-table/map->table
       (if query-valid? (sim-trans/keep-snapshot query snapshot) snapshot)))))

(defn filter-input
  [filter-intent]
  (fn [] [:div
          "filtering intent"
          [web-input/text-field {:on-change-fn (fn [v] (reset! filter-intent v))
                                 :value @filter-intent}]]))

(defn sample
  []
  (let [sim-resp (web-events-proxy/subscribe-value [:simulation-response])
        filter-intent (r/atom "")]
    [:div
     [simulation-player (constantly rendering)]
     [filter-input filter-intent]
     [:div {:class ["mb-4"]}
      (str "whole response:")
      [transform-test sim-resp filter-intent]]
     [iteration-numb]
     [:div "iteration:" @snapshot-it]
     [:div "status: " (:automaton-simulation-de.control/status sim-resp)]]))
