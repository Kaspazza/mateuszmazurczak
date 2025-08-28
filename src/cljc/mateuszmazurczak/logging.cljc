(ns mateuszmazurczak.logging
  "Api to logging"
  (:require
   [mateuszmazurczak.logging.protocol       :as p]
   [mateuszmazurczak.logging.telemere-utils :as logging-utils]))

(def log!-opts
  [:map {:open true}
   [:id :any]
   [:level [:enum :trace :debug :info :warn :error]]
   [:msg :any]
   [:data {:optional true}
    :map]])

(def event!-opts
  [:map {:open true}
   [:id :any]
   [:level [:enum :trace :debug :info :warn :error]]
   [:data {:optional true}
    :map]])

(defn log!
  "Record system health and debugging info"
  [logger opts]
  (p/-log! logger opts)
  nil)

(defn event!
  "Record business or operational events"
  [logger event-data]
  (p/-event! logger event-data)
  nil)

(defn error!
  "Record error occurrences"
  [logger error-data]
  (p/-error! logger error-data))

(defn spy!
  "Trace performance of executed code - returns the value"
  [logger id form]
  (p/-spy! logger id form))

(defn with-context
  "Create new logger with additional context"
  [logger context]
  (p/-with-context logger context))

(defmacro ->log!
  "Same as `log!` but returns value."
  [value opts-or-msg]
  `(do ~(logging-utils/keep-callsite `(t/log! ~opts-or-msg)) ~value))

(defmacro ->>log!
  "Same as `log!` but returns value."
  [opts-or-msg value]
  `(do ~(logging-utils/keep-callsite `(t/log! ~opts-or-msg)) ~value))

(defn init!
  "Initialize the logging system"
  [logger config]
  (p/-init! logger config))

(defn shutdown!
  "Shutdown the logging system"
  [logger system]
  (p/-shutdown! logger system))
