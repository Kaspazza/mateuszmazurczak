(ns mateuszmazurczak.logging
  "Api to logging"
  (:require
   [malli.core                              :as m]
   [mateuszmazurczak.logging.protocol       :as p]
   [mateuszmazurczak.logging.telemere-utils :as logging-utils]
   [mateuszmazurczak.validation             :as validation]))

(def LoggerSchema
  "Schema for logger objects - validates that it implements the protocol"
  [:fn (fn [logger] (and (some? logger) (satisfies? p/Logger logger)))])

(def log!-opts
  [:map {:closed true}
   [:id :any]
   [:level [:enum :trace :debug :info :warn :error]]
   [:msg {:optional true}
    :any]
   [:data {:optional true}
    :map]])

(def event!-opts
  [:map {:closed true}
   [:id :any]
   [:level [:enum :trace :debug :info :warn :error]]
   [:data {:optional true}
    :map]])

(defn log!
  "Record system health and debugging info"
  [logger opts]
  (try (validation/validate-data LoggerSchema logger "Logger inst")
       (validation/validate-data log!-opts opts "logging opts")
       (p/-log! logger opts)
       nil
       (catch #?(:clj Exception
                 :cljs :default)
         e
         (try (p/-error! logger
                         {:error e
                          :msg "failed while logging"})
              (catch #?(:clj Exception
                        :cljs :default)
                _
                (prn (str "Failed while logging, opts: " (pr-str opts)
                          " logger:" logger)
                     (pr-str e)))))))

(defn event!
  "Record business or operational events"
  [logger event-data]
  {:pre [(m/validate LoggerSchema logger) (m/validate event!-opts event-data)]}
  (try (p/-event! logger event-data)
       nil
       (catch #?(:clj Exception
                 :cljs :default)
         e
         (throw (ex-info "Failed to log event" {:event-data event-data} e)))))

(defn error!
  "Record error occurrences"
  [logger error-data]
  {:pre [(m/validate LoggerSchema logger) (map? error-data)]}
  (try (p/-error! logger error-data)
       (catch #?(:clj Exception
                 :cljs :default)
         e
         (throw (ex-info "Failed to log error" {:error-data error-data} e)))))

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
