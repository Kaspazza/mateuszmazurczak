(ns mateuszmazurczak.application.aoc.use-cases
  "AoC application use cases.
   
   Orchestrates domain logic with infrastructure concerns like URL parsing.
   Reusable across different event systems (re-frame, etc.)."
  (:require
   [mateuszmazurczak.application.aoc.page-schema :as page-schema]
   [mateuszmazurczak.application.aoc.solution    :as app-solution]
   [mateuszmazurczak.domain.aoc.selection        :as selection]
   [mateuszmazurczak.domain.aoc.solution         :as solution]
   [mateuszmazurczak.domain.aoc.validation       :as validation]
   [mateuszmazurczak.utils.url                   :as url-utils]))

;; =============================================================================
;; Private Helpers
;; =============================================================================

(defn- parse-solution-id-from-hash
  [url-hash]
  (when (and url-hash (not= url-hash "")) (second (re-find #"^#solution-(.+)$" url-hash))))

(defn- parse-int-safe [s] (when s (let [n (js/parseInt s 10)] (when-not (js/isNaN n) n))))

(defn- reset-form
  "Reset form to initial state with given year/challenge."
  [year challenge]
  {:author-name ""
   :github-username ""
   :content-type :code-snippet
   :content ""
   :year year
   :challenge challenge})

(defn- build-external-form-data
  "Build form data for external mode (when playground-url is present)."
  [playground-url year challenge]
  {:author-name ""
   :github-username ""
   :content-type :repo-link
   :content playground-url
   :year year
   :challenge challenge})

(defn- update-form-field
  "Update a single form field and clear its error."
  [form form-errors field value]
  {:form (assoc form field value)
   :form-errors (dissoc form-errors field)})

(defn- resolve-modal-year-challenge
  "Resolve modal form year/challenge. Priority: query params > page selection."
  [query-year query-challenge page-year]
  (let [modal-year (or query-year page-year)
        valid-modal-year? (selection/valid-year? modal-year)
        final-year (if valid-modal-year? modal-year page-year)
        valid-modal-challenge?
        (and valid-modal-year? (selection/valid-challenge-for-year? modal-year query-challenge))
        final-challenge (if valid-modal-challenge?
                          query-challenge
                          (first (selection/challenges-for-year final-year)))]
    {:year final-year
     :challenge final-challenge
     :challenges-options (selection/build-challenges-options final-year)}))

(defn- resolve-modal-open-internal
  "Resolve modal state when opening."
  [{:keys [playground-url form-year form-challenge page-year page-challenge]}]
  (let [{:keys [year challenge]} (if playground-url
                                   {:year form-year
                                    :challenge form-challenge}
                                   {:year page-year
                                    :challenge page-challenge})]
    {:year year
     :challenge challenge
     :challenges-options (selection/build-challenges-options year)}))

(defn- resolve-modal-year-selection-internal
  "Resolve modal state after year selection in modal form."
  [year]
  (let [first-challenge (first (selection/challenges-for-year year))]
    {:year year
     :challenge first-challenge
     :challenges-options (selection/build-challenges-options year)}))

(defn- resolve-modal-close-internal
  "Resolve state when closing modal."
  [page-year page-challenge]
  {:form (reset-form page-year page-challenge)})

(defn- resolve-aoc-selection
  "Resolve AoC page selection state."
  [{:keys [requested-year
           requested-challenge
           external-share-url
           external-year
           external-challenge
           highlight-solution-id
           existing-page-data
           years-options
           build-initial-page-data-fn]}]
  (let [initial-data (build-initial-page-data-fn)
        page-data
        (if (or (nil? existing-page-data) (empty? years-options)) initial-data existing-page-data)
        default-year (get-in page-data [:selector-data :selected-year])
        {:keys [year challenge]}
        (selection/resolve-year-challenge requested-year requested-challenge default-year)
        challenges-options (selection/build-challenges-options year)
        modal-resolved (resolve-modal-year-challenge external-year external-challenge year)
        form-data (if external-share-url
                    (build-external-form-data external-share-url
                                              (:year modal-resolved)
                                              (:challenge modal-resolved))
                    (get-in page-data [:modal-data :form]))
        updated-page-data (-> page-data
                              (assoc-in [:selector-data :selected-year] year)
                              (assoc-in [:selector-data :selected-challenge] challenge)
                              (assoc-in [:selector-data :challenges-options] challenges-options)
                              (assoc-in [:modal-data :form] form-data)
                              (assoc-in [:modal-data :playground-url] external-share-url)
                              (assoc-in [:modal-data :challenges-options]
                                        (:challenges-options modal-resolved)))
        dispatches (cond-> [[:admin/check-status] [:aoc/fetch-solutions year challenge]]
                     highlight-solution-id (conj [:aoc/highlight-solution highlight-solution-id]
                                                 [:dispatch-later {:ms 3000
                                                                   :dispatch
                                                                   [:aoc/clear-highlight]}])
                     external-share-url (conj [:aoc/open-modal]))]
    {:page-data updated-page-data
     :dispatches dispatches}))

(defn- can-submit-solution?
  "Check if solution can be submitted."
  [can-upload-fn year challenge validation-errors]
  (cond
    (not (can-upload-fn year challenge)) {:can-submit? false
                                          :reason :upload-limit-reached}
    validation-errors {:can-submit? false
                       :reason :validation-errors}
    :else {:can-submit? true
           :reason nil}))

(defn- resolve-submission-internal
  "Resolve submission payload from form and page state."
  [{:keys [form page-year page-challenge]}]
  (let [year (or (:year form) page-year)
        challenge (or (:challenge form) page-challenge)
        payload (-> form
                    (assoc :year year :challenge challenge)
                    app-solution/prepare-solution-payload)]
    {:year year
     :challenge challenge
     :payload payload
     :validation-errors (validation/validate-solution-form form)}))

(defn- resolve-submission-success-internal
  "Resolve state after successful submission."
  [{:keys [form page-year page-challenge]}]
  (let [year (or (:year form) page-year)
        challenge (or (:challenge form) page-challenge)
        challenges-options (selection/build-challenges-options year)]
    {:year year
     :challenge challenge
     :challenges-options challenges-options
     :form (reset-form year challenge)}))

(defn- process-fetched-solutions
  "Process solutions fetched from API."
  [{:keys [solutions
           year
           challenge
           has-consented-fn
           get-user-solution-ids-fn
           get-upload-count-fn
           enrich-voting-fn]}]
  (let [has-consent? (has-consented-fn year challenge)
        user-solution-ids (set (get-user-solution-ids-fn year challenge))
        upload-count (get-upload-count-fn year challenge)
        enriched-solutions (mapv enrich-voting-fn solutions)
        {:keys [entities ids]} (solution/normalize-solutions enriched-solutions)]
    {:entities entities
     :ids ids
     :user-solution-ids user-solution-ids
     :upload-count upload-count
     :gated? (not has-consent?)}))

(defn- can-vote?
  "Check if user can vote for solution."
  [has-voted-fn solution-id vote-type]
  {:can-vote? (not (has-voted-fn solution-id vote-type))})

;; =============================================================================
;; Public API
;; =============================================================================

(defn handle-page-entry
  "Handle AoC page entry.
   
   Parses URL params, resolves business state via domain.
   
   Args:
   - path-params: Route path parameters {:year int :challenge int}
   - url: Current URL string (for query params)
   - url-hash: URL hash string (e.g., '#solution-123')
   - existing-page-data: Current page data from state (can be nil)
   - years-options: Current years options from state
   
   Returns: Map with:
     - :page-data - Resolved page data to store
     - :dispatches - Vector of event dispatches to trigger"
  [{:keys [path-params url url-hash existing-page-data years-options]}]
  (let [query-params (url-utils/parse-queries url)
        requested-year (:year path-params)
        requested-challenge (:challenge path-params)
        external-share-url (:playground-url query-params)
        external-year (parse-int-safe (:year query-params))
        external-challenge (parse-int-safe (:challenge query-params))
        highlight-solution-id (parse-solution-id-from-hash url-hash)]
    (resolve-aoc-selection {:requested-year requested-year
                            :requested-challenge requested-challenge
                            :external-share-url external-share-url
                            :external-year external-year
                            :external-challenge external-challenge
                            :highlight-solution-id highlight-solution-id
                            :existing-page-data existing-page-data
                            :years-options years-options
                            :build-initial-page-data-fn page-schema/build-initial-page-data})))

(defn handle-year-selection
  "Handle year selection change.
   
   Args:
   - year-str: Year as string from UI
   
   Returns: Map with :state-updates :dispatches"
  [year-str]
  (let [year (parse-int-safe year-str)
        {:keys [challenge challenges-options]} (selection/resolve-year-selection year)]
    {:state-updates {:selected-year year
                     :selected-challenge challenge
                     :challenges-options challenges-options}
     :dispatches [[:nav/navigate-no-history
                   :mateuszmazurczak.adapters.navigation.routes/aoc-specific
                   {:year (str year)
                    :challenge (str challenge)}]
                  [:aoc/fetch-solutions year challenge]]}))

(defn handle-challenge-selection
  "Handle challenge selection change.
   
   Args:
   - challenge-str: Challenge as string from UI
   - current-year: Currently selected year
   
   Returns: Map with :state-updates :dispatches"
  [challenge-str current-year]
  (let [challenge (parse-int-safe challenge-str)]
    {:state-updates {:selected-challenge challenge}
     :dispatches [[:nav/navigate-no-history
                   :mateuszmazurczak.adapters.navigation.routes/aoc-specific
                   {:year (str current-year)
                    :challenge (str challenge)}]
                  [:aoc/fetch-solutions current-year challenge]]}))

(defn handle-modal-open
  "Handle modal opening.
   
   Args:
   - context: Map with :playground-url :form-year :form-challenge :page-year :page-challenge
   
   Returns: Map with :state-updates containing modal state to apply"
  [context]
  (let [{:keys [year challenge challenges-options]} (resolve-modal-open-internal context)]
    {:state-updates {:modal-open? true
                     :form-year year
                     :form-challenge challenge
                     :modal-challenges-options challenges-options}}))

(defn handle-modal-close
  "Handle modal closing.
   
   Args:
   - page-year: Current page year
   - page-challenge: Current page challenge
   
   Returns: Map with :state-updates containing modal state to apply"
  [page-year page-challenge]
  (let [{:keys [form]} (resolve-modal-close-internal page-year page-challenge)]
    {:state-updates {:modal-open? false
                     :form form
                     :playground-url nil}}))

(defn handle-modal-year-selection
  "Handle year selection change within modal.
   
   Args:
   - year-str: Year as string from UI
   
   Returns: Map with :state-updates containing modal state to apply"
  [year-str]
  (let [year (parse-int-safe year-str)
        {:keys [year challenge challenges-options]} (resolve-modal-year-selection-internal year)]
    {:state-updates {:form-year year
                     :form-challenge challenge
                     :modal-challenges-options challenges-options}}))

(defn handle-modal-challenge-selection
  "Handle challenge selection change within modal.
   
   Args:
   - challenge-str: Challenge as string from UI
   
   Returns: Map with :state-updates containing modal state to apply"
  [challenge-str]
  (let [challenge (parse-int-safe challenge-str)] {:state-updates {:form-challenge challenge}}))

(defn prepare-submission
  "Prepare solution submission - build payload, validate, and determine state updates.
   
   Args:
   - context: Map with :form :page-year :page-challenge :can-upload-fn
   
   Returns: Map with:
     - :can-submit? - boolean, true if submission should proceed
     - :reason - keyword (:upload-limit-reached, :validation-errors, or nil)
     - :payload - submission payload (only if can-submit? is true)
     - :state-updates - map of state paths to values to update"
  [{:keys [form page-year page-challenge can-upload-fn]}]
  (let [{:keys [year challenge payload validation-errors]} (resolve-submission-internal
                                                            {:form form
                                                             :page-year page-year
                                                             :page-challenge page-challenge})
        {:keys [can-submit? reason]}
        (can-submit-solution? can-upload-fn year challenge validation-errors)
        state-updates (cond
                        (= reason :validation-errors) {:form-errors validation-errors}
                        can-submit? {:submitting? true
                                     :form-errors nil}
                        :else {})]
    {:can-submit? can-submit?
     :reason reason
     :payload payload
     :state-updates state-updates}))

(defn handle-submission-success
  "Handle successful submission.
   
   Args:
   - context: Map with :form :page-year :page-challenge
   
   Returns: Map with:
     - :year :challenge :challenges-options :form - updated data
     - :navigate - navigation dispatch
     - :state-updates - map of state updates to apply"
  [context]
  (let [{:keys [year challenge challenges-options form]} (resolve-submission-success-internal
                                                          context)]
    {:year year
     :challenge challenge
     :challenges-options challenges-options
     :form form
     :navigate [:nav/navigate-no-history
                :mateuszmazurczak.adapters.navigation.routes/aoc-specific
                {:year (str year)
                 :challenge (str challenge)}]
     :state-updates {:submitting? false
                     :modal-open? false
                     :form form
                     :form-errors nil
                     :playground-url nil
                     :selected-year year
                     :selected-challenge challenge
                     :challenges-options challenges-options
                     :modal-challenges-options challenges-options}}))

(defn handle-form-update
  "Handle form field update.
   
   Args:
   - form: Current form data
   - form-errors: Current form errors (can be nil)
   - field: Field keyword to update
   - value: New value for field
   
   Returns: Map with :state-updates containing form state to apply"
  [form form-errors field value]
  (let [{:keys [form form-errors]} (update-form-field form form-errors field value)]
    {:state-updates {:form form
                     :form-errors form-errors}}))

(defn handle-fetch-solutions-success
  "Handle successful solutions fetch.
   
   Args:
   - context: Map with:
     - :solutions - Raw solutions from API
     - :year - Year for cache lookup
     - :challenge - Challenge for cache lookup
     - :has-consented-fn - Function (year challenge) -> boolean
     - :get-user-solution-ids-fn - Function (year challenge) -> set of IDs
     - :get-upload-count-fn - Function (year challenge) -> int
     - :enrich-voting-fn - Function (solution) -> enriched solution
   
   Returns: Map with :state-updates containing solutions state to apply"
  [context]
  (let [{:keys [entities ids user-solution-ids upload-count gated?]} (process-fetched-solutions
                                                                      context)]
    {:state-updates {:solutions-entities entities
                     :solution-ids ids
                     :user-solution-ids user-solution-ids
                     :upload-count upload-count
                     :gated? gated?
                     :loading? false}}))

(defn handle-vote-success
  "Handle successful vote.
   
   Args:
   - solution: Current solution data
   - vote-type: Vote type (:best-practices or :clever)
   - best-practices-count: New best practices count from API
   - clever-count: New clever count from API
   
   Returns: Updated solution map"
  [solution vote-type best-practices-count clever-count]
  (solution/update-solution-votes solution vote-type best-practices-count clever-count))

(defn check-can-vote
  "Check if vote is allowed.
   
   Args:
   - has-voted-fn: Function (solution-id vote-type) -> boolean
   - solution-id: ID of solution to vote on
   - vote-type: Vote type (:best-practices or :clever)
   
   Returns: Map with :can-vote? boolean"
  [has-voted-fn solution-id vote-type]
  (can-vote? has-voted-fn solution-id vote-type))
