(ns mateuszmazurczak.adapters.analytics.posthog
  "PostHog adapter for web analytics (CLJS)"
  (:require
   ["posthog-js"                :default posthog]
   [mateuszmazurczak.validation :as validation]))

(defn init!
  "Initialize PostHog with configuration"
  [{:keys [api-key api-host person-profiles]
    :as config}]
  (when-not (map? config)
    (throw (ex-info "PostHog config must be a map"
                    {:type ::invalid-config
                     :config config})))
  (when-not (validation/valid-non-empty-string? api-key)
    (throw (ex-info "PostHog api-key must be a non-empty string"
                    {:type ::invalid-api-key
                     :provided api-key})))
  (when-not (validation/valid-non-empty-string? api-host)
    (throw (ex-info "PostHog api-host must be a non-empty string"
                    {:type ::invalid-api-host
                     :provided api-host})))
  (when-not (validation/valid-non-empty-string? person-profiles)
    (throw (ex-info "PostHog person-profiles must be a non-empty string"
                    {:type ::invalid-person-profiles
                     :provided person-profiles})))
  (try (.init posthog
              api-key
              #js {"api_host" api-host
                   "person_profiles" person-profiles
                   "persistence" "localStorage+cookie"
                   "cross_subdomain_cookie" true})
       (.capture posthog "Initialized" #js {"Started" "true"})
       (catch :default e
         (throw (ex-info "Failed to initialize PostHog analytics"
                         {:config (dissoc config :api-key)} ; Don't log API key for security
                         e)))))

(defn capture-event!
  "Capture a custom event in PostHog"
  [{:keys [event properties]
    :as event-data}]
  (when-not (map? event-data)
    (throw (ex-info "Event data must be a map"
                    {:type ::invalid-event-data
                     :provided event-data})))
  (when-not (validation/valid-non-empty-string? event)
    (throw (ex-info "Event name must be a non-empty string"
                    {:type ::invalid-event-name
                     :provided event})))
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
  (when-not (map? identity-data)
    (throw (ex-info "Identity data must be a map"
                    {:type ::invalid-identity-data
                     :provided identity-data})))
  (when-not (validation/valid-non-empty-string? user-id)
    (throw (ex-info "User ID must be a non-empty string"
                    {:type ::invalid-user-id
                     :provided user-id})))
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
  (when-not (map? page-data)
    (throw (ex-info "Page data must be a map"
                    {:type ::invalid-page-data
                     :provided page-data})))
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
