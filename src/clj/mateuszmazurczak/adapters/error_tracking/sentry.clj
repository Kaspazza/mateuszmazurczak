(ns mateuszmazurczak.adapters.error-tracking.sentry
  "Sentry adapter for error tracking (CLJ)"
  (:require
   [clojure.pprint              :as pp]
   [clojure.walk                :as walk]
   [mateuszmazurczak.utils.validation :as validation]
   [sentry-clj.core             :as sentry])
  (:import [io.sentry Breadcrumb Sentry SentryLevel]
           [java.util Date HashMap Map]))

;;; Utility functions for Sentry data formatting

(defn- map-util-hashmappify-vals
  "Converts Clojure map into nested HashMap for Sentry Java API.
   Removes nil keys/values as HashMap doesn't allow nulls."
  [m modify-type-fn]
  (let [f (fn [[k v]]
            (let [k (if (keyword? k) (str (symbol k)) k)
                  v (if (keyword? v) (str (symbol v)) v)]
              (cond
                (map? v) [k (modify-type-fn v)]
                (and (some? v) (some? k)) [k v])))]
    (walk/postwalk (fn [x] (if (map? x) (into {} (map f x)) x)) m)))

(defn- remove-last-character
  "Remove the last character of a string"
  [s]
  (let [s (str s)] (subs s 0 (max 0 (- (count s) 1)))))

(defn- one-liner-print
  "Format element for display in print output"
  [elt]
  (if (or (map? elt) (set? elt) (vector? elt))
    (-> elt
        pp/pprint
        with-out-str
        remove-last-character)
    elt))

(defn- seq->string
  "Returns string from sequence that is readable in write outputs"
  [message]
  (if (seqable? message) (apply str "" (map one-liner-print message)) message))

(defn- keyword->level
  "Converts keyword into Sentry event level"
  [level]
  (case level
    :debug SentryLevel/DEBUG
    :info SentryLevel/INFO
    :warning SentryLevel/WARNING
    :error SentryLevel/ERROR
    :fatal SentryLevel/FATAL
    SentryLevel/INFO))

(defn- map->breadcrumb
  "Converts map into Sentry Breadcrumb"
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

;;; Public API

(defn init!
  "Initialize Sentry with configuration"
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

(defn capture-error!
  "Capture error in Sentry"
  [{:keys [message level data silent?]
    :or {silent? false}
    :as error-data}]
  {:pre [(map? error-data)]}
  (try
    ;; If silent, add as breadcrumb (won't show in Sentry until next real error)
    (if silent?
      (Sentry/addBreadcrumb (map->breadcrumb {:message (seq->string message)
                                              :level level
                                              :data data}))
      ;; Otherwise, send as event (appears immediately in Sentry)
      (sentry/send-event {:message (seq->string message)
                          :level level
                          :extra data}))
    nil
    (catch Exception e
      (throw (ex-info "Failed to capture error in Sentry"
                      {:error-data error-data}
                      e)))))
