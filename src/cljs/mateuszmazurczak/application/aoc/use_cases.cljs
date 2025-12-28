(ns mateuszmazurczak.application.aoc.use-cases
  "AoC application use cases.
   
   Orchestrates domain logic with infrastructure concerns like URL parsing.
   Reusable across different event systems (re-frame, etc.)."
  (:require
   [mateuszmazurczak.domain.pages.aoc :as domain]
   [mateuszmazurczak.utils.url        :as url-utils]))

(defn- parse-solution-id-from-hash
  [url-hash]
  (when (and url-hash (not= url-hash "")) (second (re-find #"^#solution-(.+)$" url-hash))))

(defn- parse-int-safe [s] (when s (let [n (js/parseInt s 10)] (when-not (js/isNaN n) n))))

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
    (domain/resolve-aoc-selection {:requested-year requested-year
                                   :requested-challenge requested-challenge
                                   :external-share-url external-share-url
                                   :external-year external-year
                                   :external-challenge external-challenge
                                   :highlight-solution-id highlight-solution-id
                                   :existing-page-data existing-page-data
                                   :years-options years-options})))

(defn handle-year-selection
  "Handle year selection change.
   
   Args:
   - year-str: Year as string from UI
   
   Returns: Map with :year :challenge :challenges-options :navigate"
  [year-str]
  (let [year (parse-int-safe year-str)
        {:keys [challenge]
         :as result}
        (domain/resolve-year-selection year)]
    (assoc result
           :navigate
           [:nav/navigate-no-history
            :mateuszmazurczak.adapters.navigation.routes/aoc-specific
            {:year (str year)
             :challenge (str challenge)}])))

(defn handle-challenge-selection
  "Handle challenge selection change.
   
   Args:
   - challenge-str: Challenge as string from UI
   - current-year: Currently selected year
   
   Returns: Map with :challenge :navigate"
  [challenge-str current-year]
  (let [challenge (parse-int-safe challenge-str)]
    {:challenge challenge
     :navigate [:nav/navigate-no-history
                :mateuszmazurczak.adapters.navigation.routes/aoc-specific
                {:year (str current-year)
                 :challenge (str challenge)}]}))

(defn handle-modal-open
  "Handle modal opening.
   
   Args:
   - context: Map with :playground-url :form-year :form-challenge :page-year :page-challenge
   
   Returns: Map with :year :challenge :challenges-options"
  [context]
  (domain/resolve-modal-open context))

(defn handle-modal-close
  "Handle modal closing.
   
   Args:
   - page-year: Current page year
   - page-challenge: Current page challenge
   
   Returns: Map with :form"
  [page-year page-challenge]
  (domain/resolve-modal-close page-year page-challenge))

(defn handle-modal-year-selection
  "Handle year selection change within modal.
   
   Args:
   - year-str: Year as string from UI
   
   Returns: Map with :year :challenge :challenges-options"
  [year-str]
  (let [year (parse-int-safe year-str)] (domain/resolve-modal-year-selection year)))

(defn handle-modal-challenge-selection
  "Handle challenge selection change within modal.
   
   Args:
   - challenge-str: Challenge as string from UI
   
   Returns: Parsed challenge integer"
  [challenge-str]
  (parse-int-safe challenge-str))

(defn prepare-submission
  "Prepare solution submission - build payload and validate.
   
   Args:
   - context: Map with :form :page-year :page-challenge :can-upload-fn
   
   Returns: Map with :can-submit? :reason :year :challenge :payload :validation-errors
     - :can-submit? - boolean, true if submission should proceed
     - :reason - keyword (:upload-limit-reached, :validation-errors, or nil)
     - :year, :challenge, :payload - submission data (only if can-submit? is true)
     - :validation-errors - form errors (only if reason is :validation-errors)"
  [{:keys [form page-year page-challenge can-upload-fn]}]
  (let [{:keys [year challenge payload validation-errors]} (domain/resolve-submission
                                                            {:form form
                                                             :page-year page-year
                                                             :page-challenge page-challenge})
        {:keys [can-submit? reason]}
        (domain/can-submit-solution? can-upload-fn year challenge validation-errors)]
    {:can-submit? can-submit?
     :reason reason
     :year year
     :challenge challenge
     :payload payload
     :validation-errors validation-errors}))

(defn handle-submission-success
  "Handle successful submission.
   
   Args:
   - context: Map with :form :page-year :page-challenge
   
   Returns: Map with :year :challenge :challenges-options :form :navigate"
  [context]
  (let [{:keys [year challenge]
         :as result}
        (domain/resolve-submission-success context)]
    (assoc result
           :navigate
           [:nav/navigate-no-history
            :mateuszmazurczak.adapters.navigation.routes/aoc-specific
            {:year (str year)
             :challenge (str challenge)}])))

(defn handle-form-update
  "Handle form field update.
   
   Args:
   - form: Current form data
   - form-errors: Current form errors (can be nil)
   - field: Field keyword to update
   - value: New value for field
   
   Returns: Map with :form :form-errors"
  [form form-errors field value]
  (domain/update-form-field form form-errors field value))

(defn handle-fetch-solutions-success
  "Handle successful solutions fetch.
   
   Args:
   - solutions: Raw solutions from API
   - year: Year for cache lookup
   - challenge: Challenge for cache lookup
   - has-consented-fn: Function (year challenge) -> boolean
   - get-user-solution-ids-fn: Function (year challenge) -> set of IDs
   - get-upload-count-fn: Function (year challenge) -> int
   - enrich-voting-fn: Function (solution) -> enriched solution
   
   Returns: Map with :entities :ids :user-solution-ids :upload-count :gated?"
  [context]
  (domain/process-fetched-solutions context))

(defn handle-vote-success
  "Handle successful vote.
   
   Args:
   - solution: Current solution data
   - vote-type: Vote type (:best-practices or :clever)
   - best-practices-count: New best practices count from API
   - clever-count: New clever count from API
   
   Returns: Updated solution map"
  [solution vote-type best-practices-count clever-count]
  (domain/update-solution-votes solution vote-type best-practices-count clever-count))

(defn check-can-vote
  "Check if vote is allowed.
   
   Args:
   - has-voted-fn: Function (solution-id vote-type) -> boolean
   - solution-id: ID of solution to vote on
   - vote-type: Vote type (:best-practices or :clever)
   
   Returns: Map with :can-vote? boolean"
  [has-voted-fn solution-id vote-type]
  (domain/can-vote? has-voted-fn solution-id vote-type))
