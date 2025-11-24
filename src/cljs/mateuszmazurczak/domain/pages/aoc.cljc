(ns mateuszmazurczak.domain.pages.aoc
  "Advent of Code domain - schemas and pure functions."
  (:require
   [clojure.string                      :as str]
   [malli.core                          :as m]
   [malli.error                         :as me]
   [mateuszmazurczak.domain.i18n.schema :as i18n-schema]))

;; =============================================================================
;; State Paths
;; =============================================================================

(def ^:dynamic *aoc-page-path* [:pages :aoc])
(def ^:dynamic *aoc-selected-year-path* [:pages :aoc :selected-year])
(def ^:dynamic *aoc-selected-challenge-path* [:pages :aoc :selected-challenge])
(def ^:dynamic *aoc-selected-part-path* [:pages :aoc :selected-part])
(def ^:dynamic *aoc-challenges-options-path* [:pages :aoc :challenges-options])
(def ^:dynamic *aoc-solution-ids-path* [:pages :aoc :solution-ids])
(def ^:dynamic *aoc-loading-path* [:pages :aoc :loading?])
(def ^:dynamic *aoc-modal-open-path* [:pages :aoc :modal-open?])
(def ^:dynamic *aoc-form-path* [:pages :aoc :form])
(def ^:dynamic *aoc-form-errors-path* [:pages :aoc :form-errors])
(def ^:dynamic *aoc-submitting-path* [:pages :aoc :submitting?])

;; =============================================================================
;; Domain Data
;; =============================================================================

(def years (vec (range 2015 2026)))

(defn challenges-for-year
  "Returns available challenges for given year.
   2025 only has 12 challenges (current year), others have 24."
  [year]
  (let [max-challenge (if (= year 2025) 12 24)] (vec (range 1 (inc max-challenge)))))

(def Year [:and :int [:>= 2015] [:<= 2025]])

(def Challenge [:and :int [:>= 1] [:<= 24]])

(def Part [:enum 1 2])

(def ContentType [:enum :code-snippet :repo-link])

(def Solution
  "Solution in app-db (content-type normalized to keyword)."
  [:map
   [:id :string]
   [:year Year]
   [:challenge Challenge]
   [:part Part]
   [:author-name :string]
   [:github-profile {:optional true}
    [:maybe :string]]
   [:github-username {:optional true}
    [:maybe :string]]
   [:content-type ContentType]
   [:content :string]
   [:created-at {:optional true}
    :string]])

(def SolutionForm
  "Schema for the upload form data."
  [:map
   [:author-name [:string {:min 1}]]
   [:github-profile {:optional true}
    [:maybe :string]]
   [:content-type ContentType]
   [:content [:string {:min 1}]]])

(def valid-solution-form? (m/validator SolutionForm))

(defn validate-solution-form
  "Validate solution form and return field-level errors.
   
   Returns nil if valid, or a map of field -> error message if invalid.
   
   Example return value:
   {:author-name \"Name is required\"
    :content \"Content is required\"}"
  [form]
  (when-not (valid-solution-form? form)
    (let [errors {}
          author-name (:author-name form)
          content (:content form)]
      (cond-> errors
        (or (nil? author-name) (str/blank? author-name)) (assoc :author-name "Name is required")
        (or (nil? content) (str/blank? content)) (assoc :content "Content is required")))))

(def YearOption
  [:map {:closed true}
   [:value Year]
   [:label :string]])

(def ChallengeOption
  [:map {:closed true}
   [:value Challenge]
   [:label :string]])

(def AocPageData
  "Schema for AoC page data in app-db.
   
   Note: This schema is for the page state only, NOT the denormalized
   UI data. The page stores solution IDs, subscriptions denormalize them."
  [:map {:closed true}
   [:selected-year Year]
   [:selected-challenge Challenge]
   [:selected-part Part]
   [:years-options [:vector YearOption]]
   [:challenges-options [:vector ChallengeOption]]
   [:solution-ids [:vector :string]]
   [:loading? :boolean]
   [:modal-open? :boolean]
   [:form
    [:map {:closed true}
     [:author-name :string]
     [:github-profile :string]
     [:content-type ContentType]
     [:content :string]]]
   [:form-errors {:optional true}
    [:maybe [:map-of :keyword :string]]]
   [:submitting? :boolean]
   [:text [:map-of :keyword i18n-schema/I18nMarker]]
   [:handlers {:optional true}
    [:map
     [:on-select-year fn?]
     [:on-select-challenge fn?]
     [:on-select-part fn?]
     [:on-open-modal fn?]
     [:on-close-modal fn?]
     [:on-submit-solution fn?]
     [:on-update-form fn?]]]
   [:theme {:optional true}
    [:enum :light :dark]]])

(def AocPageUIData
  "Schema for AoC page UI data (denormalized for component consumption).
   
   This is the shape returned by the :pages/aoc subscription after
   denormalizing solution-ids into full Solution objects and translating text."
  [:map {:closed true}
   [:selected-year Year]
   [:selected-challenge Challenge]
   [:selected-part Part]
   [:years-options [:vector YearOption]]
   [:challenges-options [:vector ChallengeOption]]
   [:solutions [:vector Solution]]
   [:loading? :boolean]
   [:modal-open? :boolean]
   [:form
    [:map {:closed true}
     [:author-name :string]
     [:github-profile :string]
     [:content-type ContentType]
     [:content :string]]]
   [:form-errors {:optional true}
    [:maybe [:map-of :keyword :string]]]
   [:submitting? :boolean]
   [:text [:map-of :keyword :string]]
   [:handlers {:optional true}
    [:map
     [:on-select-year fn?]
     [:on-select-challenge fn?]
     [:on-select-part fn?]
     [:on-open-modal fn?]
     [:on-close-modal fn?]
     [:on-submit-solution fn?]
     [:on-update-form fn?]]]
   [:theme {:optional true}
    [:enum :light :dark]]])

(defn valid-aoc-page-data?
  "Validate AoC page data against schema."
  [data]
  (m/validate AocPageData data))

(defn explain-aoc-page-data
  "Explain validation errors for AoC page data.
   
   Returns human-readable explanation of validation errors,
   or nil if data is valid."
  [data]
  (when-let [explanation (m/explain AocPageData data)]
    {:explained (me/humanize explanation)
     :raw-explanation explanation}))

(defn valid-aoc-page-ui-data?
  "Validate AoC page UI data (denormalized) against schema."
  [data]
  (m/validate AocPageUIData data))

(defn explain-aoc-page-ui-data
  "Explain validation errors for AoC page UI data.
   
   Returns human-readable explanation of validation errors,
   or nil if data is valid."
  [data]
  (when-let [explanation (m/explain AocPageUIData data)]
    {:explained (me/humanize explanation)
     :raw-explanation explanation}))

(defn initial-aoc-data
  "Returns initial AoC page data structure for app-db initialization.
   
   Starts with default year (2025), challenge (1), and part (1).
   Solutions and options are loaded via :aoc/on-route-enter event.
   
   Note: Stores solution-ids (not full solutions) - entities stored separately."
  []
  {:selected-year (last years)
   :selected-challenge 1
   :selected-part 1
   :years-options []
   :challenges-options []
   :solution-ids []
   :loading? false
   :modal-open? false
   :form {:author-name ""
          :github-profile ""
          :content-type :code-snippet
          :content ""}
   :form-errors nil
   :submitting? false
   :text {:advent-of-code-solutions [:i18n :advent-of-code-solutions]
          :share-and-explore-solutions [:i18n :share-and-explore-solutions]
          :year [:i18n :year]
          :challenge [:i18n :challenge]
          :part [:i18n :part]
          :part-1 [:i18n :part-1]
          :part-2 [:i18n :part-2]
          :upload-solution [:i18n :upload-solution]
          :loading-solutions [:i18n :loading-solutions]
          :no-solutions-yet [:i18n :no-solutions-yet]
          :be-first-to-share [:i18n :be-first-to-share]
          :view-repository [:i18n :view-repository]
          :unknown-content-type [:i18n :unknown-content-type]
          :best-practices [:i18n :best-practices]
          :clever [:i18n :clever]
          :upload-your-solution [:i18n :upload-your-solution]
          :share-your-advent-of-code-solution [:i18n :share-your-advent-of-code-solution]
          :your-name [:i18n :your-name]
          :github-profile-optional [:i18n :github-profile-optional]
          :content-type [:i18n :content-type]
          :code-snippet [:i18n :code-snippet]
          :repository-link [:i18n :repository-link]
          :your-code [:i18n :your-code]
          :repository-url [:i18n :repository-url]
          :cancel [:i18n :cancel]
          :submit-solution [:i18n :submit-solution]
          :submitting [:i18n :submitting]
          :name-placeholder [:i18n :name-placeholder]
          :github-placeholder [:i18n :github-placeholder]
          :code-placeholder [:i18n :code-placeholder]
          :repo-placeholder [:i18n :repo-placeholder]
          :select-year [:i18n :select-year]
          :select-challenge [:i18n :select-challenge]
          :select-part [:i18n :select-part]}
   :handlers {:on-select-year [:dispatch [:aoc/select-year]]
              :on-select-challenge [:dispatch [:aoc/select-challenge]]
              :on-select-part [:dispatch [:aoc/select-part]]
              :on-open-modal [:dispatch [:aoc/open-modal]]
              :on-close-modal [:dispatch [:aoc/close-modal]]
              :on-submit-solution [:dispatch [:aoc/submit-solution]]
              :on-update-form [:dispatch [:aoc/update-form]]}})

(defn build-years-options
  "Build year selector options from available years."
  []
  (mapv (fn [y]
          {:value y
           :label (str y)})
        (reverse years)))

(defn build-challenges-options
  "Build challenge selector options for given year."
  [year]
  (mapv (fn [c]
          {:value c
           :label (str "Day " c)})
        (challenges-for-year year)))

(defn build-initial-page-data
  "Build initial AoC page data with all options populated.
   
   This is called on route entry to ensure all selector options
   are available for the UI. Does not include handlers - those are
   added at the application layer."
  []
  (let [initial-data (initial-aoc-data)
        year (:selected-year initial-data)]
    (assoc initial-data
           :years-options (build-years-options)
           :challenges-options (build-challenges-options year))))

(defn normalize-solutions
  "Normalize a collection of solutions into entities map and IDs vector.
   
   Takes a vector of Solution objects and returns a map with:
   - :entities - map of solution-id -> Solution
   - :ids - vector of solution-ids in original order"
  [solutions]
  {:entities (into {} (map (fn [solution] [(:id solution) solution]) solutions))
   :ids (mapv :id solutions)})

(defn format-github-username
  "Extract and format GitHub username from profile URL.
   
   Examples:
   \"https://github.com/username\" -> \"(@username)\"
   \"https://github.com/user-name\" -> \"(@user-name)\"
   nil -> nil"
  [github-profile]
  (when github-profile
    (when-let [username (last (str/split github-profile #"/"))] (str "(@" username ")"))))

(defn enrich-solution-with-github-username
  "Add formatted github-username to solution if github-profile exists."
  [solution]
  (if-let [profile (:github-profile solution)]
    (assoc solution :github-username (format-github-username profile))
    solution))

(defn denormalize-solutions
  "Denormalize solution IDs back into full Solution objects.
   
   Takes solution-ids vector and entities map, returns vector of Solutions.
   Enriches each solution with formatted github-username.
   Filters out any IDs that don't have corresponding entities (defensive)."
  [solution-ids entities]
  (into []
        (comp (map #(get entities %)) (filter some?) (map enrich-solution-with-github-username))
        solution-ids))
