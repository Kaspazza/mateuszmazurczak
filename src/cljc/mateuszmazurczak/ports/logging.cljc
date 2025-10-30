(ns mateuszmazurczak.ports.logging
  "Api to logging"
  (:require
   [malli.core                                 :as m]
   [mateuszmazurczak.adapters.logging.protocol :as p]
   [mateuszmazurczak.adapters.logging.telemere :as t]
   [mateuszmazurczak.utils.macro               :as utils-macro]
   [mateuszmazurczak.utils.validation          :as validation]))

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
  (when-not (m/validate LoggerSchema logger)
    (throw (ex-info "Invalid logger instance"
                    {:type ::invalid-logger
                     :provided logger})))
  (when-not (m/validate event!-opts event-data)
    (throw (ex-info "Invalid event data"
                    {:type ::invalid-event-data
                     :provided event-data})))
  (try (p/-event! logger event-data)
       nil
       (catch #?(:clj Exception
                 :cljs :default)
         e
         (throw (ex-info "Failed to log event" {:event-data event-data} e)))))

(defn error!
  "Record error occurrences"
  [logger error-data]
  (when-not (m/validate LoggerSchema logger)
    (throw (ex-info "Invalid logger instance"
                    {:type ::invalid-logger
                     :provided logger})))
  (when-not (map? error-data)
    (throw (ex-info "Error data must be a map"
                    {:type ::invalid-error-data
                     :provided error-data})))
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

(defn safe-error!
  "Record error with fallback to console if logger is unavailable.
   
   Use this for critical error logging during system initialization/shutdown
   where the logger might not be available. Falls back to console.error if:
   - logger is nil
   - logger is invalid
   - logging itself fails
   
   Arguments:
   - logger: Logger instance (can be nil)
   - error-data: Map with error information"
  [logger error-data]
  (if (and logger (m/validate LoggerSchema logger))
    (try (p/-error! logger error-data)
         (catch #?(:clj Exception
                   :cljs :default)
           e
           #?(:clj (println "Logging failed, falling back to print:"
                            (pr-str error-data)
                            "\n" (pr-str e))
              :cljs (js/console.error "Logging failed, falling back to console:"
                                      (clj->js error-data)
                                      e))))
    ;; Logger unavailable, fall back to console
    #?(:clj (println "Logger unavailable, falling back to print:"
                     (pr-str error-data))
       :cljs (js/console.error "Logger unavailable, falling back to console:"
                               (clj->js error-data)))))

(defmacro ->log!
  "Same as `log!` but returns value."
  [value opts-or-msg]
  `(do ~(utils-macro/keep-callsite `(t/log! ~opts-or-msg)) ~value))

(defmacro ->>log!
  "Same as `log!` but returns value."
  [opts-or-msg value]
  `(do ~(utils-macro/keep-callsite `(t/log! ~opts-or-msg)) ~value))

(defn init!
  "Initialize the logging system"
  [logger config]
  (p/-init! logger config))

(defn shutdown!
  "Shutdown the logging system"
  [logger system]
  (p/-shutdown! logger system))
