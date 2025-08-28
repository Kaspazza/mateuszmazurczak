(ns mateuszmazurczak.portal
  (:require
   #?@(:clj [[portal.api :as portal]])
   #?@(:cljs [[portal.web :as portal] [cljs.pprint :as pp]])
   [clojure.datafy :as d]
   [taoensso.telemere.impl]))

(defonce filters (atom []))

(defonce logs (atom '()))

(defn get-filters [] @filters)

(defn add-filter [filter] (swap! filters (fnil conj []) filter))

(defn remove-filter
  [filter]
  (let [filter-id (if (map? filter) (:id filter) filter)]
    (swap! filters #(remove (fn [f] (= (:id f) filter-id)) %))))

(defn get-logs
  "Returns logs after applying all filters in order."
  []
  (reduce (fn [acc f] ((:f f) acc)) @logs @filters))

(defn signal->portal
  [{:keys
    [id uid level error msg_ data inst ns coords kind run-nsecs parent root]
    :as signal}]
  (if (instance? taoensso.telemere.impl.Signal signal)
    (with-meta (merge #_{:id id}
                      {:level level
                       :kind (if (= :trace kind) :spy kind)
                       :ns (symbol (or ns "?"))
                       :line (first coords)
                       :column (second coords)
                       :result id}
                      (when parent {:parent-id (:id parent)})
                      (when (and root
                                 (not= (:id parent) (:id root))
                                 (not= id (:id root)))
                        {:root-id (:id root)})
                      (when-let [msg (force msg_)] {:msg msg})
                      (when error {:error (d/datafy error)})
                      (when-let [ts (force inst)]
                        {:time #?(:clj (java.util.Date/from inst)
                                  :cljs (js/Date.))})
                      (when run-nsecs {:run-nsecs run-nsecs})
                      (when data {:data data})
                      (when parent {:parent parent})
                      (when root {:root root}))
               {:portal.viewer/default :lekta.viewer/log-viewer
                :portal.viewer/for {:form :portal.viewer/pprint
                                    :time :portal.viewer/relative-time}}
               #_{:level level
                  :line (first coords)
                  :column (second coords)
                  :ns (quote (symbol ns))})
    signal))

(defn submit
  [value]
  (swap! logs (fn [taps] (conj taps (signal->portal value)))))

(defonce filtered-logs (atom (get-logs)))

(defn update-filtered-logs [_ _ _ _] (reset! filtered-logs (get-logs)))

(add-watch logs :update-filtered-logs update-filtered-logs)
(add-watch filters :update-filtered-logs update-filtered-logs)

(defn level-filter-fn
  "Returns a function that filters logs, removing any with a level in `excluded-levels`."
  [excluded-levels]
  (fn [logs] (remove #(contains? excluded-levels (:level %)) logs)))

(defn level-filter
  [excluded-levels]
  {:id :level-filter
   :f (level-filter-fn excluded-levels)})

(defn set-level-filter
  "Removes any existing :level-filter and adds a new one with the given excluded-levels."
  [excluded-levels]
  (swap! filters (fn [l]
                   (let [remove-if-already-exists
                         (remove (fn [f] (= (:result f) :level-filter)) l)]
                     (conj remove-if-already-exists
                           (level-filter excluded-levels)))))
  filtered-logs)

(defn debug-filter
  "Sets a filter that excludes :trace logs."
  [& _args]
  (set-level-filter #{:trace}))

(defn info-filter
  "Sets a filter that excludes :trace and :debug logs."
  [& _args]
  (set-level-filter #{:trace :debug}))

(defn warn-filter
  "Sets a filter that excludes :trace, :debug, and :info logs."
  [& _args]
  (set-level-filter #{:trace :debug :info}))

(defn error-filter
  "Sets a filter that excludes :trace, :debug, :info, and :warn logs."
  [& _args]
  (set-level-filter #{:trace :debug :info :warn}))


(defn id-filter-fn
  "Returns a function that removes logs with the given log-id."
  [log-id]
  (fn [logs] (remove #(= (:result %) log-id) logs)))

(defn id-filter
  "Creates a filter that removes logs with the given log-id(s).
   Accepts a map with :id, a collection of maps with :id, or a single id."
  {:shortcuts [["i"]]}
  [& args]
  (when (= 1 (count args))
    (let [arg (first args)
          ids (cond
                (map? arg) [(:id arg)]
                (coll? arg) (mapv :id arg)
                :else [arg])
          ids (filter some? ids)]
      (doseq [log-id ids]
        (let [filter-id (keyword (gensym "id-filter-"))]
          (add-filter {:id filter-id
                       :f (id-filter-fn log-id)
                       :log-id log-id})))))
  filtered-logs)

(defn show-filters [& _] (get-filters))

(defn reset-filters [& _] (reset! filters []) filtered-logs)

(defn clear-logs [& _] (reset! logs '()) filtered-logs)

(defn show-logs
  "Returns logs after applying all filters in order."
  [& _args]
  filtered-logs)

(defn ns-filter-fn
  "Returns a function that only includes logs from the given namespaces."
  [namespace]
  (fn [logs] (remove #(= (:ns %) namespace) logs)))

(defn ns-filter
  "Creates a filter that only includes logs from the given namespace(s).
   Accepts a map with :ns, a collection of maps with :ns, or a single ns (symbol/string)."
  {:shortcuts [["n"]]}
  [& args]
  (when (= 1 (count args))
    (let [arg (first args)
          namespaces (cond
                       (map? arg) [(:ns arg)]
                       (coll? arg) (mapv :ns arg)
                       (keyword? arg) (try [(namespace arg)]
                                           (catch #?(:clj Exception
                                                     :cljs :default)
                                             _
                                             []))
                       :else [arg])
          namespaces (filter some? namespaces)]
      (doseq [namespace namespaces]
        (let [filter-id (keyword (gensym "ns-filter-"))]
          (add-filter {:id filter-id
                       :f (ns-filter-fn namespace)
                       :ns namespace})))))
  filtered-logs)

#?(:clj (do (portal/register! #'debug-filter)
            (portal/register! #'info-filter)
            (portal/register! #'warn-filter)
            (portal/register! #'error-filter)
            (portal/register! #'id-filter)
            (portal/register! #'show-filters)
            (portal/register! #'clear-logs)
            (portal/register! #'show-logs)
            (portal/register! #'reset-filters)
            (portal/register! #'remove-filter)
            (portal/register! #'ns-filter))
   :cljs (do (portal/register! #'debug-filter)
             (portal/register! #'info-filter)
             (portal/register! #'warn-filter)
             (portal/register! #'error-filter)
             (portal/register! #'id-filter)
             (portal/register! #'show-filters)
             (portal/register! #'clear-logs)
             (portal/register! #'show-logs)
             (portal/register! #'reset-filters)
             (portal/register! #'remove-filter)
             (portal/register! #'ns-filter)))
