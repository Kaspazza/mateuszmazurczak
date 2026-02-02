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
   :aoc/open-modal {:category :page
                    :description "Open upload solution modal."
                    :schema [:cat [:= :aoc/open-modal]]
                    :handler-type :db}
   :aoc/upload-limit-reached
   {:category :page
    :description
    "Show notification when user tries to upload but has reached the 5 solution limit for current year/challenge/part."
    :schema [:cat [:= :aoc/upload-limit-reached]]
    :handler-type :db}
   :aoc/close-modal {:category :page
                     :description "Close upload solution modal and reset form."
                     :schema [:cat [:= :aoc/close-modal]]
                     :handler-type :db}
   :aoc/update-form {:category :page
                     :description "Update form field in modal."
                     :schema [:cat [:= :aoc/update-form] keyword? :any]
                     :handler-type :db}
   :aoc/modal-select-year {:category :page
                           :description
                           "Select year in modal and reset challenge to first available."
                           :schema [:cat [:= :aoc/modal-select-year] :string]
                           :handler-type :db}
   :aoc/modal-select-challenge {:category :page
                                :description "Select challenge in modal."
                                :schema [:cat [:= :aoc/modal-select-challenge] :string]
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
                         :description "Fetch solutions from backend for year/challenge."
                         :schema [:cat [:= :aoc/fetch-solutions] :int :int]
                         :handler-type :fx}
   :aoc/fetch-solutions-success {:category :page
                                 :description "Handle successful solutions fetch."
                                 :schema [:cat [:= :aoc/fetch-solutions-success] [:sequential :any]]
                                 :handler-type :db}
   :aoc/fetch-solutions-failure {:category :page
                                 :description "Handle failed solutions fetch."
                                 :schema [:cat [:= :aoc/fetch-solutions-failure] :any]
                                 :handler-type :fx}
   :aoc/vote {:category :page
              :description "Vote for a solution (best-practices or clever)."
              :schema [:cat [:= :aoc/vote] :string [:enum :best-practices :clever]]
              :handler-type :fx}
   :aoc/vote-success
   {:category :page
    :description "Handle successful vote submission. Refetches solutions to get updated counts."
    :schema [:cat [:= :aoc/vote-success]]
    :handler-type :fx}
   :aoc/vote-failure {:category :page
                      :description "Handle failed vote submission."
                      :schema [:cat [:= :aoc/vote-failure] :any]
                      :handler-type :fx}
   :aoc/give-consent {:category :page
                      :description "Give consent ('I've solved it') to unlock viewing solutions."
                      :schema [:cat [:= :aoc/give-consent] :int :int]
                      :handler-type :fx}
   :aoc/highlight-solution
   {:category :page
    :description "Set a solution as highlighted (from URL hash). Cleared automatically after 3s."
    :schema [:cat [:= :aoc/highlight-solution] [:maybe :string]]
    :handler-type :db}
   :aoc/clear-highlight {:category :page
                         :description "Clear the highlighted solution."
                         :schema [:cat [:= :aoc/clear-highlight]]
                         :handler-type :db}
   :admin/on-route-enter {:category :page
                          :description "Initialize admin page state on route entry."
                          :schema [:cat [:= :admin/on-route-enter]]
                          :handler-type :fx}
   :admin/update-form {:category :page
                       :description "Update admin form field."
                       :schema [:cat [:= :admin/update-form] :keyword :any]
                       :handler-type :fx}
   :admin/login {:category :admin
                 :description "Admin login with API key from form."
                 :schema [:cat [:= :admin/login]]
                 :handler-type :fx}
   :admin/logout {:category :admin
                  :description "Admin logout (clear key)."
                  :schema [:cat [:= :admin/logout]]
                  :handler-type :fx}
   :admin/check-status {:category :admin
                        :description "Check if admin key exists in localStorage on init."
                        :schema [:cat [:= :admin/check-status]]
                        :handler-type :fx}
   :admin/delete-solution {:category :admin
                           :description "Delete a solution (admin only)."
                           :schema [:cat [:= :admin/delete-solution] :string]
                           :handler-type :fx}
   :admin/delete-solution-success {:category :admin
                                   :description "Handle successful solution deletion."
                                   :schema [:cat [:= :admin/delete-solution-success] :any]
                                   :handler-type :fx}
   :admin/delete-solution-failure {:category :admin
                                   :description "Handle failed solution deletion."
                                   :schema [:cat [:= :admin/delete-solution-failure] :any]
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
                  :handler-type :fx}
   :qr-codes/on-route-enter {:category :page
                             :description "Initialize QR codes page state on route entry."
                             :schema [:cat [:= :qr-codes/on-route-enter]]
                             :handler-type :db}
   :qr-codes/update-input {:category :page
                           :description "Update QR codes input text."
                           :schema [:cat [:= :qr-codes/update-input] :string]
                           :handler-type :db}
   :qr-codes/update-size {:category :page
                          :description "Update QR code size in pixels."
                          :schema [:cat [:= :qr-codes/update-size] :int]
                          :handler-type :db}
   :qr-codes/update-format {:category :page
                            :description "Update output format (:zip or :pdf)."
                            :schema [:cat [:= :qr-codes/update-format] [:enum :zip :pdf]]
                            :handler-type :db}
   :qr-codes/generate-preview {:category :page
                               :description "Generate preview QR codes from input."
                               :schema [:cat [:= :qr-codes/generate-preview]]
                               :handler-type :db}
   :qr-codes/download {:category :page
                       :description "Download generated QR codes in selected format."
                       :schema [:cat [:= :qr-codes/download]]
                       :handler-type :fx}
   :qr-codes/worker-ready {:category :page
                           :description "Handle worker readiness for QR batch generation."
                           :schema [:cat [:= :qr-codes/worker-ready] map?]
                           :handler-type :fx}
   :qr-codes/worker-progress {:category :page
                              :description "Handle progress update from worker during QR generation."
                              :schema [:cat [:= :qr-codes/worker-progress] map?]
                              :handler-type :fx}
   :qr-codes/worker-finalizing {:category :page
                                :description "Handle worker starting finalization of archive (PDF/ZIP generation)."
                                :schema [:cat [:= :qr-codes/worker-finalizing] map?]
                                :handler-type :db}
   :qr-codes/worker-done {:category :page
                          :description "Handle completion of QR generation with final buffer."
                          :schema [:cat [:= :qr-codes/worker-done] map?]
                          :handler-type :fx}
   :qr-codes/worker-failure {:category :page
                             :description "Handle failed QR code batch generation."
                             :schema [:cat [:= :qr-codes/worker-failure] [:vector :string]]
                             :handler-type :fx}
   :qr-codes/download-failure {:category :page
                               :description "Handle failed download of final QR archive."
                               :schema [:cat [:= :qr-codes/download-failure] :any]
                               :handler-type :fx}
   :qr-codes/update-show-label {:category :page
                                :description
                                "Toggle whether to show QR code value as label below QR code."
                                :schema [:cat [:= :qr-codes/update-show-label] :boolean]
                                :handler-type :db}})

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
