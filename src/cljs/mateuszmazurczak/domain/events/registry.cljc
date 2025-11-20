(ns mateuszmazurczak.domain.events.registry
  "Event registry - defines ALL events in the system.
   
   This is the CONTRACT that any state management adapter must implement.
   When replacing re-frame, check this registry to see all required events.
   
   Each event is defined with:
   - :category - Logical grouping (:navigation, :page, :i18n)
   - :description - What this event does (behavior, not implementation)
   - :schema - Malli schema for validation
   - :handler-type - :fx (effects) or :db (pure state update)")

(def events
  "Registry of all application events.
   
   This is the single source of truth for what events exist in the application."
  {:nav/navigate
   {:category :navigation
    :description
    "Navigate to a route with optional path and query parameters. Adds to browser history."
    :schema [:cat
             [:= :nav/navigate]
             keyword? ; route-name
             [:? [:maybe map?]] ; path-params
             [:? [:maybe map?]]] ; query-params
    :handler-type :fx}
   :nav/navigate-no-history
   {:category :navigation
    :description "Navigate to a route without adding to browser history (replace current entry)."
    :schema [:cat [:= :nav/navigate-no-history] keyword? [:? [:maybe map?]] [:? [:maybe map?]]]
    :handler-type :fx}
   :nav/change-query-parameters! {:category :navigation
                                  :description
                                  "Update only query parameters without changing route or path."
                                  :schema [:cat [:= :nav/change-query-parameters!] map?]
                                  :handler-type :fx}
   :nav/route-changed
   {:category :navigation
    :description
    "Internal event: dispatched by history adapter when route changes. Updates app state with new route data."
    :schema [:cat
             [:= :nav/route-changed]
             [:map
              [:route-name keyword?]
              [:page-id keyword?]
              [:path-parameters {:optional true}
               map?]
              [:query-parameters {:optional true}
               map?]
              [:fragment {:optional true}
               [:maybe string?]]]]
    :handler-type :fx
    :internal? true}
   :home/on-route-enter
   {:category :page
    :description
    "Controller event: dispatched when home route is entered. Loads/refreshes home page data."
    :schema [:cat [:= :home/on-route-enter]]
    :handler-type :fx}
   :home/refresh {:category :page
                  :description
                  "Rebuild home page data (e.g., when articles are updated). Pure state update."
                  :schema [:cat [:= :home/refresh]]
                  :handler-type :db}
   :i18n/change-lang {:category :i18n
                      :description
                      "Change application language. Takes DOM event from language selector."
                      :schema [:cat [:= :i18n/change-lang] [:or "PL" "EN"]]
                      :handler-type :fx}})

(defn events-by-category
  "Get events grouped by category (:navigation, :page, :i18n).
   
   Arguments:
   - category: keyword category to filter by
   
   Returns: seq of [event-id event-spec] tuples"
  [category]
  (filter (fn [[_id spec]] (= category (:category spec))) events))

(defn event-schema
  "Get Malli schema for an event.
   
   Arguments:
   - event-id: keyword event identifier
   
   Returns: Malli schema or nil if event not found"
  [event-id]
  (get-in events [event-id :schema]))
