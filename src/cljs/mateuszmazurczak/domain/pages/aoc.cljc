(ns mateuszmazurczak.domain.pages.aoc
  "Advent of Code domain - schemas and pure functions."
  (:require
   [clojure.string                         :as str]
   [malli.core                             :as m]
   [malli.error                            :as me]
   [mateuszmazurczak.domain.i18n.schema    :as i18n-schema]
   [mateuszmazurczak.domain.state.registry :as state-registry]))

;; =============================================================================
;; State Paths
;; =============================================================================

;; Selector data paths
(def ^:dynamic *aoc-selected-year-path*
  (conj state-registry/*aoc-page-path* :selector-data :selected-year))
(def ^:dynamic *aoc-selected-challenge-path*
  (conj state-registry/*aoc-page-path* :selector-data :selected-challenge))
(def ^:dynamic *aoc-selected-part-path*
  (conj state-registry/*aoc-page-path* :selector-data :selected-part))
(def ^:dynamic *aoc-challenges-options-path*
  (conj state-registry/*aoc-page-path* :selector-data :challenges-options))
(def ^:dynamic *aoc-years-options-path*
  (conj state-registry/*aoc-page-path* :selector-data :years-options))

;; Upload data paths
(def ^:dynamic *aoc-upload-count-path*
  (conj state-registry/*aoc-page-path* :upload-data :upload-count))

;; Solutions data paths
(def ^:dynamic *aoc-solution-ids-path*
  (conj state-registry/*aoc-page-path* :solutions-data :solution-ids))
(def ^:dynamic *aoc-solutions-path*
  (conj state-registry/*aoc-page-path* :solutions-data :solutions))
(def ^:dynamic *aoc-solutions-data-path* (conj state-registry/*aoc-page-path* :solutions-data))
(def ^:dynamic *aoc-solutions-theme-path*
  (conj state-registry/*aoc-page-path* :solutions-data :theme))
(def ^:dynamic *aoc-solutions-admin-logged-in-path*
  (conj state-registry/*aoc-page-path* :solutions-data :admin-logged-in?))
(def ^:dynamic *aoc-solutions-text-path*
  (conj state-registry/*aoc-page-path* :solutions-data :text))
(def ^:dynamic *aoc-loading-path* (conj state-registry/*aoc-page-path* :solutions-data :loading?))
(def ^:dynamic *aoc-user-solution-ids-path*
  (conj state-registry/*aoc-page-path* :solutions-data :user-solution-ids))
(def ^:dynamic *aoc-gated-path* (conj state-registry/*aoc-page-path* :solutions-data :gated?))
(def ^:dynamic *aoc-highlighted-solution-id-path*
  (conj state-registry/*aoc-page-path* :solutions-data :highlighted-solution-id))

;; Modal data paths
(def ^:dynamic *aoc-modal-open-path* (conj state-registry/*aoc-page-path* :modal-data :modal-open?))
(def ^:dynamic *aoc-form-path* (conj state-registry/*aoc-page-path* :modal-data :form))
(def ^:dynamic *aoc-form-errors-path*
  (conj state-registry/*aoc-page-path* :modal-data :form-errors))
(def ^:dynamic *aoc-submitting-path* (conj state-registry/*aoc-page-path* :modal-data :submitting?))

(defn relative-path [full-path] (vec (drop (count state-registry/*aoc-page-path*) full-path)))

;; =============================================================================
;; Schema
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
  "Solution in app-db (content-type normalized to keyword).
   
   Note: UI-specific fields (theme, text, handlers) are added during
   denormalization in the subscription layer"
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
    :string]
   [:best-practices-count {:optional true}
    [:maybe :int]]
   [:clever-count {:optional true}
    [:maybe :int]]
   [:voted-best-practices? {:optional true}
    [:maybe :boolean]]
   [:voted-clever? {:optional true}
    [:maybe :boolean]]
   [:theme {:optional true}
    [:maybe [:enum :light :dark]]]
   [:text {:optional true}
    [:maybe :map]]
   [:on-vote-best-practices {:optional true}
    [:maybe fn?]]
   [:on-vote-clever {:optional true}
    [:maybe fn?]]])

(def SolutionForm
  "Schema for the upload form data."
  [:map
   [:author-name [:string {:min 1}]]
   [:github-profile {:optional true}
    [:maybe :string]]
   [:content-type ContentType]
   [:content [:string {:min 1}]]
   [:year Year]
   [:challenge Challenge]
   [:part Part]])

(def valid-solution-form? (m/validator SolutionForm))

(defn url?
  "Check if string is a valid URL (basic validation).
   
   Returns true if string starts with http:// or https://"
  [s]
  (when (string? s) (or (str/starts-with? s "http://") (str/starts-with? s "https://"))))

(defn validate-solution-form
  "Validate solution form and return field-level errors.
   
   Returns nil if valid, or a map of field -> i18n marker if invalid.
   
   Example return value:
   {:author-name [:i18n :name-required]
    :content [:i18n :content-required]
    :content [:i18n :invalid-url]}"
  [form]
  (when-not (valid-solution-form? form)
    (let [errors {}
          author-name (:author-name form)
          content (:content form)
          content-type (:content-type form)
          is-repo-link? (= content-type :repo-link)]
      (cond-> errors
        (or (nil? author-name) (str/blank? author-name)) (assoc :author-name [:i18n :name-required])
        (or (nil? content) (str/blank? content)) (assoc :content [:i18n :content-required])
        (and is-repo-link? (not (url? content))) (assoc :content [:i18n :invalid-url])))))

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
   [:playground-url {:optional true}
    [:maybe :string]]
   [:header-data
    [:map {:closed true}
     [:text [:map-of :keyword i18n-schema/I18nMarker]]]]
   [:selector-data
    [:map {:closed true}
     [:selected-year Year]
     [:selected-challenge Challenge]
     [:selected-part Part]
     [:years-options [:vector YearOption]]
     [:challenges-options [:vector ChallengeOption]]
     [:text [:map-of :keyword i18n-schema/I18nMarker]]
     [:handlers
      [:map {:closed true}
       [:on-select-year fn?]
       [:on-select-challenge fn?]
       [:on-select-part fn?]]]]]
   [:upload-data
    [:map {:closed true}
     [:upload-count :int]
     [:text
      [:map {:closed true}
       [:upload-solution i18n-schema/I18nMarker]]]
     [:handlers
      [:map {:closed true}
       [:on-open-modal fn?]
       [:on-upload-limit-reached fn?]]]]]
   [:solutions-data
    [:map {:closed true}
     [:solution-ids [:vector :string]]
     [:loading? :boolean]
     [:gated? :boolean]
     [:user-solution-ids [:set :string]]
     [:highlighted-solution-id {:optional true}
      [:maybe :string]]
     [:text [:map-of :keyword i18n-schema/I18nMarker]]
     [:handlers
      [:map {:closed true}
       [:on-vote fn?]
       [:on-give-consent fn?]
       [:on-delete-solution fn?]]]]]
   [:modal-data
    [:map {:closed true}
     [:modal-open? :boolean]
     [:form
      [:map {:closed true}
       [:author-name :string]
       [:github-profile :string]
       [:content-type ContentType]
       [:content :string]
       [:year {:optional true}
        [:maybe Year]]
       [:challenge {:optional true}
        [:maybe Challenge]]
       [:part {:optional true}
        [:maybe Part]]]]
     [:form-errors {:optional true}
      [:maybe [:map-of :keyword i18n-schema/I18nMarker]]]
     [:submitting? :boolean]
     [:playground-url {:optional true}
      [:maybe :string]]
     [:years-options [:vector YearOption]]
     [:challenges-options [:vector ChallengeOption]]
     [:text [:map-of :keyword i18n-schema/I18nMarker]]
     [:handlers
      [:map {:closed true}
       [:on-close-modal fn?]
       [:on-submit-solution fn?]
       [:on-update-form fn?]
       [:on-select-year fn?]
       [:on-select-challenge fn?]
       [:on-select-part fn?]]]]]])

(def AocPageUIData
  "Schema for AoC page UI data (denormalized for component consumption).
   
   This is the shape returned by the :pages/aoc subscription after
   denormalizing solution-ids into full Solution objects and translating text."
  [:map {:closed true}
   [:header-data
    [:map {:closed true}
     [:text [:map-of :keyword :string]]]]
   [:selector-data
    [:map {:closed true}
     [:selected-year Year]
     [:selected-challenge Challenge]
     [:selected-part Part]
     [:years-options [:vector YearOption]]
     [:challenges-options [:vector ChallengeOption]]
     [:text [:map-of :keyword :string]]
     [:handlers
      [:map {:closed true}
       [:on-select-year fn?]
       [:on-select-challenge fn?]
       [:on-select-part fn?]]]]]
   [:upload-data
    [:map {:closed true}
     [:upload-count :int]
     [:text
      [:map {:closed true}
       [:upload-solution :string]]]
     [:handlers
      [:map {:closed true}
       [:on-open-modal fn?]
       [:on-upload-limit-reached fn?]]]]]
   [:solutions-data
    [:map {:closed true}
     [:solutions [:vector Solution]]
     [:loading? :boolean]
     [:gated? :boolean]
     [:user-solution-ids [:set :string]]
     [:highlighted-solution-id {:optional true}
      [:maybe :string]]
     [:theme [:enum :light :dark]]
     [:admin-logged-in? [:maybe :boolean]]
     [:text [:map-of :keyword :string]]
     [:handlers
      [:map {:closed true}
       [:on-vote fn?]
       [:on-give-consent fn?]
       [:on-delete-solution fn?]]]]]
   [:modal-data
    [:map {:closed true}
     [:modal-open? :boolean]
     [:form
      [:map {:closed true}
       [:author-name :string]
       [:github-profile :string]
       [:content-type ContentType]
       [:content :string]
       [:year {:optional true}
        [:maybe Year]]
       [:challenge {:optional true}
        [:maybe Challenge]]
       [:part {:optional true}
        [:maybe Part]]]]
     [:form-errors {:optional true}
      [:maybe [:map-of :keyword :string]]]
     [:submitting? :boolean]
     [:playground-url {:optional true}
      [:maybe :string]]
     [:years-options [:vector YearOption]]
     [:challenges-options [:vector ChallengeOption]]
     [:text [:map-of :keyword :string]]
     [:handlers
      [:map {:closed true}
       [:on-close-modal fn?]
       [:on-submit-solution fn?]
       [:on-update-form fn?]
       [:on-select-year fn?]
       [:on-select-challenge fn?]
       [:on-select-part fn?]]]]]])

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

;; =============================================================================
;; Domain data
;; =============================================================================

(defn initial-aoc-data
  "Returns initial AoC page data structure for app-db initialization.
   
   Starts with default year (2025), challenge (1), and part (1).
   Solutions and options are loaded via :aoc/on-route-enter event.
   
   Note: Stores solution-ids (not full solutions) - entities stored separately.
   
   Data structure mirrors component hierarchy:
   - header-data: Page title and description
   - selector-data: Year/challenge/part filters with options
   - upload-data: Upload button and limits
   - solutions-data: Solutions display with loading/gating states
   - modal-data: Form for uploading solutions"
  []
  {:header-data {:text {:title [:i18n :advent-of-code-solutions]
                        :description [:i18n :share-and-explore-solutions]}}
   :selector-data {:selected-year (last years)
                   :selected-challenge 1
                   :selected-part 1
                   :years-options []
                   :challenges-options []
                   :text {:year [:i18n :year]
                          :challenge [:i18n :challenge]
                          :part [:i18n :part]
                          :part-1 [:i18n :part-1]
                          :part-2 [:i18n :part-2]
                          :select-year [:i18n :select-year]
                          :select-challenge [:i18n :select-challenge]
                          :select-part [:i18n :select-part]}
                   :handlers {:on-select-year [:dispatch [:aoc/select-year]]
                              :on-select-challenge [:dispatch [:aoc/select-challenge]]
                              :on-select-part [:dispatch [:aoc/select-part]]}}
   :upload-data {:upload-count 0
                 :text {:upload-solution [:i18n :upload-solution]}
                 :handlers {:on-open-modal [:dispatch [:aoc/open-modal]]
                            :on-upload-limit-reached [:dispatch [:aoc/upload-limit-reached]]}}
   :solutions-data {:solution-ids []
                    :loading? false
                    :gated? false
                    :user-solution-ids #{}
                    :text {:loading-solutions [:i18n :loading-solutions]
                           :unlock-community-solutions [:i18n :unlock-community-solutions]
                           :only-if-solved-no-cheating [:i18n :only-if-solved-no-cheating]
                           :show-me-solutions [:i18n :show-me-solutions]
                           :no-solutions-yet [:i18n :no-solutions-yet]
                           :be-first-to-share [:i18n :be-first-to-share]
                           :your-solution [:i18n :your-solution]
                           :view-repository [:i18n :view-repository]
                           :unknown-content-type [:i18n :unknown-content-type]
                           :best-practices [:i18n :best-practices]
                           :clever [:i18n :clever]
                           :delete [:i18n :delete]
                           :confirm-delete [:i18n :confirm-delete]}
                    :handlers {:on-vote [:dispatch [:aoc/vote]]
                               :on-give-consent [:dispatch [:aoc/give-consent]]
                               :on-delete-solution [:dispatch [:admin/delete-solution]]}}
   :modal-data {:modal-open? false
                :form {:author-name ""
                       :github-profile ""
                       :content-type :code-snippet
                       :content ""
                       :year (last years)
                       :challenge 1
                       :part 1}
                :form-errors nil
                :submitting? false
                :playground-url nil
                :years-options []
                :challenges-options []
                :text {:upload-your-solution [:i18n :upload-your-solution]
                       :share-your-advent-of-code-solution [:i18n
                                                            :share-your-advent-of-code-solution]
                       :uploading-for [:i18n :uploading-for]
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
                       :source-playground [:i18n :source-playground]
                       :year [:i18n :year]
                       :challenge [:i18n :challenge]
                       :part [:i18n :part]
                       :part-1 [:i18n :part-1]
                       :part-2 [:i18n :part-2]
                       :select-year [:i18n :select-year]
                       :select-challenge [:i18n :select-challenge]
                       :select-part [:i18n :select-part]}
                :handlers {:on-close-modal [:dispatch [:aoc/close-modal]]
                           :on-submit-solution [:dispatch [:aoc/submit-solution]]
                           :on-update-form [:dispatch [:aoc/update-form]]
                           :on-select-year [:dispatch [:aoc/modal-select-year]]
                           :on-select-challenge [:dispatch [:aoc/modal-select-challenge]]
                           :on-select-part [:dispatch [:aoc/modal-select-part]]}}})

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
        year (get-in initial-data [:selector-data :selected-year])
        years-options (build-years-options)
        challenges-options (build-challenges-options year)]
    (-> initial-data
        (assoc-in [:selector-data :years-options] years-options)
        (assoc-in [:selector-data :challenges-options] challenges-options)
        (assoc-in [:modal-data :years-options] years-options)
        (assoc-in [:modal-data :challenges-options] challenges-options))))

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

(defn enrich-solution-with-vote-handlers
  "Add vote handler dispatch markers to solution.
   
   These dispatch markers will be converted to actual handler functions
   by events/dispatch-markers->handlers in the subscription layer."
  [solution]
  (let [solution-id (:id solution)]
    (assoc solution
           :on-vote-best-practices [:dispatch [:aoc/vote solution-id :best-practices]]
           :on-vote-clever [:dispatch [:aoc/vote solution-id :clever]])))

(defn denormalize-solutions
  "Denormalize solution IDs back into vector of full solution objects."
  [solution-ids entities]
  (into []
        (comp (map #(get entities %))
              (filter some?)
              (map enrich-solution-with-github-username)
              (map enrich-solution-with-vote-handlers))
        solution-ids))
