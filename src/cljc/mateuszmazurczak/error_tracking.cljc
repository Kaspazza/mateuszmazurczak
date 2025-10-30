(ns mateuszmazurczak.error-tracking
  "Error tracking - hybrid port/adapter approach.
   
   Provides public API for error monitoring. Supports multiple adapters:
   - :sentry - Full-featured error tracking with Sentry (requires DSN, env)
   - :logging - Simple uncaught exception logging (requires logger)
   
   No protocol abstraction since requirements are still evolving."
  (:require
   #?(:clj [mateuszmazurczak.adapters.error-tracking.logging :as logging]
      :cljs [mateuszmazurczak.adapters.error-tracking.logging :as logging])))

(defn init!
  "Initialize error tracking system with configuration.
   
   Returns nil."
  [config]
  (when-not (map? config)
    (throw (ex-info "Error tracking config must be a map"
                    {:type ::invalid-config
                     :provided config})))
  (logging/init! config))

