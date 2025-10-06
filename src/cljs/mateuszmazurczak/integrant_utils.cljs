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
  {:pre [(fn? init-fn) (some? logger) (keyword? component-name)]}
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
