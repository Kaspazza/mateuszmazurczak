(ns mateuszmazurczak.analytics.adapters.posthog
  "PostHog adapter for web analytics (CLJS)"
  (:require
   ["posthog-js"                :default posthog]
   [mateuszmazurczak.validation :as validation]))

(defn init!
  "Initialize PostHog with configuration"
  [{:keys [api-key api-host person-profiles]
    :as config}]
  {:pre [(map? config)
         (validation/valid-non-empty-string? api-key)
         (validation/valid-non-empty-string? api-host)
         (validation/valid-non-empty-string? person-profiles)]}
  (try (.init posthog
              api-key
              #js {"api_host" api-host
                   "person_profiles" person-profiles})
       (.capture posthog "Initialized" #js {"Started" "true"})
       (catch :default e
         (throw (ex-info "Failed to initialize PostHog analytics"
                         {:config (dissoc config :api-key)} ; Don't log API key for security
                         e)))))

(defn capture-event!
  "Capture a custom event in PostHog"
  [{:keys [event properties]
    :as event-data}]
  {:pre [(map? event-data) (validation/valid-non-empty-string? event)]}
  (try (if properties
         (.capture posthog event (clj->js properties))
         (.capture posthog event))
       nil
       (catch :default e
         (throw (ex-info "Failed to capture event in PostHog"
                         {:event-data event-data}
                         e)))))

(defn identify!
  "Identify user in PostHog"
  [{:keys [user-id properties]
    :as identity-data}]
  {:pre [(map? identity-data) (validation/valid-non-empty-string? user-id)]}
  (try (if properties
         (.identify posthog user-id (clj->js properties))
         (.identify posthog user-id))
       nil
       (catch :default e
         (throw (ex-info "Failed to identify user in PostHog"
                         {:identity-data (dissoc identity-data :user-id)} ; Don't log user-id
                         e)))))

(defn page-view!
  "Capture page view in PostHog"
  [{:keys [path properties]
    :as page-data}]
  {:pre [(map? page-data)]}
  (try (let [opts (cond-> {}
                    path (assoc :path path)
                    properties (assoc :properties properties))]
         (if (seq opts)
           (.capture posthog "$pageview" (clj->js opts))
           (.capture posthog "$pageview")))
       nil
       (catch :default e
         (throw (ex-info "Failed to capture page view in PostHog"
                         {:page-data page-data}
                         e)))))
