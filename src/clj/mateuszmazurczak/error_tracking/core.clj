(ns mateuszmazurczak.error-tracking.core
  (:require
   [clojure.pprint  :as pp]
   [clojure.walk    :as walk]
   [sentry-clj.core :as sentry])
  (:import [io.sentry Breadcrumb Sentry SentryLevel]
           [java.util Date HashMap Map]))


(defn map-util-hashmappify-vals
  "Converts an ordinary Clojure map into a Clojure map with nested map
   values recursively translated into what modify-type-fn is returning. Based
   on walk/stringify-keys.
   When key or value is nil, the pair is removed, as the hashmap doesn't allow null keys/values."
  [m modify-type-fn]
  (let [f (fn [[k v]]
            (let [k (if (keyword? k) (str (symbol k)) k)
                  v (if (keyword? v) (str (symbol v)) v)]
              (cond
                (map? v) [k (modify-type-fn v)]
                (and (some? v) (some? k)) [k v])))]
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn remove-last-character
  "Remove the last character of a string"
  [s]
  (let [s (str s)] (subs s 0 (max 0 (- (count s) 1)))))

(defn one-liner-print
  "Prepare the element `elt` to display in the print
           Params:
            * `elt` data to show, which type will be checked"
  [elt]
  (if (or (map? elt) (set? elt) (vector? elt))
    (-> elt
        pp/pprint
        with-out-str
        remove-last-character)
    elt))

(defn seq->string
  "Returns string from sequence, that is readable in write outputs."
  [message]
  (if (seqable? message) (apply str "" (map one-liner-print message)) message))

(defn- keyword->level
  "Converts a keyword into an event level."
  [level]
  (case level
    :debug SentryLevel/DEBUG
    :info SentryLevel/INFO
    :warning SentryLevel/WARNING
    :error SentryLevel/ERROR
    :fatal SentryLevel/FATAL
    SentryLevel/INFO))

(defn- map->breadcrumb
  "Converts a map into a Breadcrumb."
  ^Breadcrumb [{:keys [type level message category data timestamp]}]
  (let [breadcrumb (if timestamp (Breadcrumb. ^Date timestamp) (Breadcrumb.))]
    (when type (.setType breadcrumb type))
    (when level (.setLevel breadcrumb (keyword->level level)))
    (when message (.setMessage breadcrumb message))
    (when category (.setCategory breadcrumb category))
    (when data
      (doseq [[k v] (map-util-hashmappify-vals data #(HashMap. ^Map %))]
        (.setData breadcrumb k v)))
    breadcrumb))

(defn send-breadcrumb!
  "Sends breadcrumb, which will not be shown in sentry until event is sent.
   You can read more here: https://docs.sentry.io/platforms/java/enriching-events/breadcrumbs/"
  [{:keys [message level context]}]
  (Sentry/addBreadcrumb (map->breadcrumb {:message (seq->string message)
                                          :level level
                                          :data context})))

(defn send-event!
  "Sends an event that is registered in sentry."
  [{:keys [message level context]}]
  (sentry/send-event {:message (seq->string message)
                      :level level
                      :extra context}))

(defn init-sentry!
  "Initialize sentry for jvm, so events can be recorded.
   'development' as an environment is ignored, so no event is sent from it."
  [{:keys [dsn env]}]
  (if (every? some? [dsn env])
    (sentry/init! dsn {:environment env})
    (prn "Sentry initialization is skipped, paremeters are missing")))


(defn- sentry-data
  [ns level & message]
  (let [context (if (map? (first message))
                  (merge (first message) (when ns {:ns ns}))
                  (when ns {:ns ns}))
        message (if (map? (first message)) (rest message) message)]
    {:message message
     :level level
     :context context}))

(defn add-context
  [ns level & message]
  (send-breadcrumb! (apply sentry-data ns level message)))

(defn error-alert
  [ns level & message]
  (send-event! (apply sentry-data ns level message)))

(defn init-error-tracking!
  [{:keys [dsn env]}]
  (when-not dsn (prn "dsn is missing in init-error-tracking!"))
  (when-not env (prn "env is missing in init-error-tracking!"))
  (init-sentry! {:dsn dsn
                 :env env}))
