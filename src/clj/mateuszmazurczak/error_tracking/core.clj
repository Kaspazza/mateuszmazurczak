(ns mateuszmazurczak.error-tracking.core
  (:require
   [clojure.pprint              :as pp]
   [clojure.walk                :as walk]
   [malli.core                  :as m]
   [mateuszmazurczak.validation :as validation]
   [sentry-clj.core             :as sentry])
  (:import [io.sentry Breadcrumb Sentry SentryLevel]
           [java.util Date HashMap Map]))

(def SentryLevelSchema
  "Valid Sentry log levels"
  [:enum :debug :info :warning :error :fatal])

(def BreadcrumbData
  "Schema for breadcrumb data"
  [:map
   [:message :any]
   [:level SentryLevelSchema]
   [:context {:optional true}
    :map]])

(def EventData
  "Schema for Sentry event data"
  [:map
   [:message :any]
   [:level SentryLevelSchema]
   [:context {:optional true}
    :map]])


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
  [{:keys [message level context]
    :as breadcrumb-data}]
  {:pre [(m/validate BreadcrumbData breadcrumb-data)]}
  (try (Sentry/addBreadcrumb (map->breadcrumb {:message (seq->string message)
                                               :level level
                                               :data context}))
       (catch Exception e
         (throw (ex-info "Failed to send breadcrumb to Sentry"
                         {:breadcrumb-data breadcrumb-data}
                         e)))))

(defn send-event!
  "Sends an event that is registered in sentry."
  [{:keys [message level context]
    :as event-data}]
  {:pre [(m/validate EventData event-data)]}
  (try (sentry/send-event {:message (seq->string message)
                           :level level
                           :extra context})
       (catch Exception e
         (throw (ex-info "Failed to send event to Sentry"
                         {:event-data event-data}
                         e)))))

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
  "Initialize sentry for jvm, so events can be recorded.
   'development' as an environment is ignored, so no event is sent from it."
  [{:keys [dsn env]
    :as config}]
  {:pre [(map? config)
         (validation/valid-non-empty-string? dsn)
         (validation/valid-non-empty-string? env)]}
  (try (sentry/init! dsn {:environment env})
       (catch Exception e
         (throw (ex-info "Failed to initialize Sentry error tracking"
                         {:config (dissoc config :dsn)} ; Don't log DSN for security
                         e)))))
