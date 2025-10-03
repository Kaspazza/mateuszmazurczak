(ns mateuszmazurczak.error-tracking
  "Error tracking - hybrid port/adapter approach.
   
   Provides public API for error monitoring, directly using Sentry as implementation.
   No protocol abstraction since requirements are still evolving.
   Acts as Application Service layer wrapping Sentry adapter."
  (:require
   #?(:clj [mateuszmazurczak.error-tracking.adapters.sentry :as sentry]
      :cljs [mateuszmazurczak.error-tracking.adapters.sentry :as sentry])))

(defn init!
  "Initialize error tracking system with configuration.
   
   Config map should contain:
   - :dsn - Sentry DSN string
   - :env - environment name (development, staging, production)
   - :traced-website - (CLJS only) regex for tracing
   
   Returns nil."
  [config]
  {:pre [(map? config)]}
  (sentry/init! config))

(defn capture-error!
  "Capture and report an error to the tracking system.
   
   Error data map:
   - :message - error message or description
   - :level - severity level (:debug :info :warning :error :fatal)  
   - :data - optional map of additional metadata
   - :silent? - (optional) if true, adds as breadcrumb instead of event
   
   Returns nil on success, throws on failure."
  [error-data]
  {:pre [(map? error-data)]}
  (sentry/capture-error! error-data))
