(ns mateuszmazurczak.integrant-utils
  "Utilities for frontend Integrant component lifecycle management."
  (:require
   [mateuszmazurczak.logging :as log]))

(defn optional-component
  "Wraps a frontend component initialization function to make it optional.
   
   If initialization throws an exception:
   - Logs the error via the provided logger
   - Returns nil instead of throwing
   
   This allows non-critical frontend components (like analytics) to fail
   gracefully without bringing down the entire application.
   
   Args:
   - init-fn: zero-arity function that performs initialization
   - logger: logger instance for error reporting
   - component-name: keyword identifying the component (for logging)
   
   Returns:
   - Result of init-fn on success
   - nil on failure (after logging)"
  [init-fn logger component-name]
  (when-not (fn? init-fn)
    (throw (ex-info "init-fn must be a function"
                    {:type ::invalid-init-fn
                     :provided init-fn})))
  (when-not (some? logger)
    (throw (ex-info "logger must be provided" {:type ::missing-logger})))
  (when-not (keyword? component-name)
    (throw (ex-info "component-name must be a keyword"
                    {:type ::invalid-component-name
                     :provided component-name})))
  (try (init-fn)
       (catch :default e
         (log/error!
          logger
          (ex-info "Non-critical component failed, continuing in degraded mode"
                   {:id ::optional-component-failed
                    :data {:component component-name
                           :mode :degraded
                           :ex e}}))
         nil)))
