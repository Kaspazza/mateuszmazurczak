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
                      :handler-type :fx}
   :aoc/on-route-enter {:category :page
                        :description "Controller event: Initialize AoC page when route is entered."
                        :schema [:cat [:= :aoc/on-route-enter]]
                        :handler-type :fx}
   :aoc/select-year {:category :page
                     :description "Select year and reset challenge to 1, then fetch solutions."
                     :schema [:cat [:= :aoc/select-year] :int]
                     :handler-type :fx}
   :aoc/select-challenge {:category :page
                          :description "Select challenge and fetch solutions for current year."
                          :schema [:cat [:= :aoc/select-challenge] :int]
                          :handler-type :fx}
   :aoc/select-part {:category :page
                     :description
                     "Select part (1 or 2) and fetch solutions for current year/challenge."
                     :schema [:cat [:= :aoc/select-part] [:enum 1 2]]
                     :handler-type :fx}
   :aoc/open-modal {:category :page
                    :description "Open upload solution modal."
                    :schema [:cat [:= :aoc/open-modal]]
                    :handler-type :db}
   :aoc/close-modal {:category :page
                     :description "Close upload solution modal and reset form."
                     :schema [:cat [:= :aoc/close-modal]]
                     :handler-type :db}
   :aoc/update-form {:category :page
                     :description "Update form field in modal."
                     :schema [:cat [:= :aoc/update-form] keyword? :any]
                     :handler-type :db}
   :aoc/submit-solution {:category :page
                         :description "Submit solution to backend API."
                         :schema [:cat [:= :aoc/submit-solution]]
                         :handler-type :fx}
   :aoc/submit-success {:category :page
                        :description "Handle successful solution submission."
                        :schema [:cat [:= :aoc/submit-success]]
                        :handler-type :fx}
   :aoc/submit-failure {:category :page
                        :description "Handle failed solution submission."
                        :schema [:cat [:= :aoc/submit-failure] :any]
                        :handler-type :fx}
   :aoc/fetch-solutions {:category :page
                         :description "Fetch solutions from backend for year/challenge/part."
                         :schema [:cat [:= :aoc/fetch-solutions] :int :int [:enum 1 2]]
                         :handler-type :fx}
   :aoc/fetch-solutions-success {:category :page
                                 :description "Handle successful solutions fetch."
                                 :schema [:cat [:= :aoc/fetch-solutions-success] [:sequential :any]]
                                 :handler-type :db}
   :aoc/fetch-solutions-failure {:category :page
                                 :description "Handle failed solutions fetch."
                                 :schema [:cat [:= :aoc/fetch-solutions-failure] :any]
                                 :handler-type :fx}
   :theme/set {:category :theme
               :description
               "Set theme to app-db and apply to DOM. Theme persistence handled by cache system."
               :schema [:cat [:= :theme/set] [:enum :light :dark]]
               :handler-type :fx}
   :theme/toggle {:category :theme
                  :description
                  "Toggle between light and dark themes. Theme persistence handled by cache system."
                  :schema [:cat [:= :theme/toggle]]
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
