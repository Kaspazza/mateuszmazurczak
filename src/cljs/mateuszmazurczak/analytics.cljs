(ns mateuszmazurczak.analytics
  "Web analytics - hybrid port/adapter approach.
   
   Provides public API for web analytics tracking, directly using PostHog as implementation.
   No protocol abstraction since requirements are still evolving.
   Acts as Application Service layer wrapping PostHog adapter."
  (:require
   [mateuszmazurczak.analytics.adapters.posthog :as posthog]))

(defn init!
  "Initialize analytics system with configuration.
   
   Config map should contain:
   - :api-key - PostHog API key
   - :api-host - PostHog API host URL
   - :person-profiles - 'identified_only' or 'always'
   
   Returns nil."
  [config]
  {:pre [(map? config)]}
  (posthog/init! config))

(defn capture-event!
  "Capture a custom analytics event.
   
   Event data map:
   - :event - event name string
   - :properties - optional map of event properties
   
   Returns nil."
  [event-data]
  {:pre [(map? event-data)]}
  (posthog/capture-event! event-data))
