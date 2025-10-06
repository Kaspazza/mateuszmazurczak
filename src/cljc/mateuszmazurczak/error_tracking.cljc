(ns mateuszmazurczak.error-tracking
  "Error tracking - hybrid port/adapter approach.
   
   Provides public API for error monitoring. Supports multiple adapters:
   - :sentry - Full-featured error tracking with Sentry (requires DSN, env)
   - :logging - Simple uncaught exception logging (requires logger)
   
   No protocol abstraction since requirements are still evolving."
  (:require
   #?(:clj [mateuszmazurczak.error-tracking.adapters.logging :as logging]
      :cljs [mateuszmazurczak.error-tracking.adapters.logging :as logging])))

(defn init!
  "Initialize error tracking system with configuration.
   
   Returns nil."
  [config]
  {:pre [(map? config)]}
  (logging/init! config))

