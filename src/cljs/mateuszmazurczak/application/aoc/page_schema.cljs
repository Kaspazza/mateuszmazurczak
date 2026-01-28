(ns mateuszmazurczak.application.aoc.page-schema
  "AoC page data schemas, state paths, and initialization."
  (:require
   [malli.core                             :as m]
   [malli.error                            :as me]
   [mateuszmazurczak.domain.aoc.selection  :as selection]
   [mateuszmazurczak.domain.aoc.validation :as validation]
   [mateuszmazurczak.domain.i18n.schema    :as i18n-schema]
   [mateuszmazurczak.domain.state.registry :as state-registry]))

;; =============================================================================
;; State Paths
;; =============================================================================

(def ^:dynamic *aoc-selected-year-path*
  (conj state-registry/*aoc-page-path* :selector-data :selected-year))
(def ^:dynamic *aoc-selected-challenge-path*
  (conj state-registry/*aoc-page-path* :selector-data :selected-challenge))
(def ^:dynamic *aoc-challenges-options-path*
  (conj state-registry/*aoc-page-path* :selector-data :challenges-options))
(def ^:dynamic *aoc-years-options-path*
  (conj state-registry/*aoc-page-path* :selector-data :years-options))
(def ^:dynamic *aoc-upload-count-path*
  (conj state-registry/*aoc-page-path* :upload-data :upload-count))
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
(def ^:dynamic *aoc-modal-open-path* (conj state-registry/*aoc-page-path* :modal-data :modal-open?))
(def ^:dynamic *aoc-form-path* (conj state-registry/*aoc-page-path* :modal-data :form))
(def ^:dynamic *aoc-form-errors-path*
  (conj state-registry/*aoc-page-path* :modal-data :form-errors))
(def ^:dynamic *aoc-submitting-path* (conj state-registry/*aoc-page-path* :modal-data :submitting?))

(defn relative-path [full-path] (vec (drop (count state-registry/*aoc-page-path*) full-path)))

;; =============================================================================
;; Schemas
;; =============================================================================

(def Solution
  "Solution in app-db."
  [:map
   [:id :string]
   [:year validation/Year]
   [:challenge validation/Challenge]
   [:author-name :string]
   [:github-username {:optional true}
    [:maybe :string]]
   [:github-profile {:optional true}
    [:maybe :string]]
   [:github-username-display {:optional true}
    [:maybe :string]]
   [:content-type validation/ContentType]
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

(def YearOption
  [:map {:closed true}
   [:value validation/Year]
   [:label :string]])

(def ChallengeOption
  [:map {:closed true}
   [:value validation/Challenge]
   [:label :string]])

(def AocPageData
  "Schema for AoC page data in app-db."
  [:map {:closed true}
   [:playground-url {:optional true}
    [:maybe :string]]
   [:header-data
    [:map {:closed true}
     [:text [:map-of :keyword i18n-schema/I18nMarker]]]]
   [:selector-data
    [:map {:closed true}
     [:selected-year validation/Year]
     [:selected-challenge validation/Challenge]
     [:years-options [:vector YearOption]]
     [:challenges-options [:vector ChallengeOption]]
     [:text [:map-of :keyword i18n-schema/I18nMarker]]
     [:handlers
      [:map {:closed true}
       [:on-select-year fn?]
       [:on-select-challenge fn?]]]]]
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
       [:github-username :string]
       [:content-type validation/ContentType]
       [:content :string]
       [:year {:optional true}
        [:maybe validation/Year]]
       [:challenge {:optional true}
        [:maybe validation/Challenge]]]]
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
       [:on-select-challenge fn?]]]]]])

(def AocPageUIData
  "Schema for AoC page UI data (denormalized for component consumption)."
  [:map {:closed true}
   [:header-data
    [:map {:closed true}
     [:text [:map-of :keyword :string]]]]
   [:selector-data
    [:map {:closed true}
     [:selected-year validation/Year]
     [:selected-challenge validation/Challenge]
     [:years-options [:vector YearOption]]
     [:challenges-options [:vector ChallengeOption]]
     [:text [:map-of :keyword :string]]
     [:handlers
      [:map {:closed true}
       [:on-select-year fn?]
       [:on-select-challenge fn?]]]]]
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
       [:github-username :string]
       [:content-type validation/ContentType]
       [:content :string]
       [:year {:optional true}
        [:maybe validation/Year]]
       [:challenge {:optional true}
        [:maybe validation/Challenge]]]]
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
       [:on-select-challenge fn?]]]]]])

;; =============================================================================
;; Validation
;; =============================================================================

(defn valid-aoc-page-data?
  "Validate AoC page data against schema."
  [data]
  (m/validate AocPageData data))

(defn explain-aoc-page-data
  "Explain validation errors for AoC page data."
  [data]
  (when-let [explanation (m/explain AocPageData data)]
    {:explained (me/humanize explanation)
     :raw-explanation explanation}))

(defn valid-aoc-page-ui-data?
  "Validate AoC page UI data (denormalized) against schema."
  [data]
  (m/validate AocPageUIData data))

(defn explain-aoc-page-ui-data
  "Explain validation errors for AoC page UI data."
  [data]
  (when-let [explanation (m/explain AocPageUIData data)]
    {:explained (me/humanize explanation)
     :raw-explanation explanation}))

;; =============================================================================
;; Initial Data
;; =============================================================================

(defn initial-aoc-data
  "Initial AoC page data structure."
  []
  {:header-data {:text {:title [:i18n :advent-of-code-solutions]
                        :description [:i18n :share-and-explore-solutions]}}
   :selector-data {:selected-year (last selection/years)
                   :selected-challenge 1
                   :years-options []
                   :challenges-options []
                   :text {:year [:i18n :year]
                          :challenge [:i18n :challenge]
                          :select-year [:i18n :select-year]
                          :select-challenge [:i18n :select-challenge]}
                   :handlers {:on-select-year [:dispatch [:aoc/select-year]]
                              :on-select-challenge [:dispatch [:aoc/select-challenge]]}}
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
                       :github-username ""
                       :content-type :code-snippet
                       :content ""
                       :year (last selection/years)
                       :challenge 1}
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
                       :github-username-optional [:i18n :github-username-optional]
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
                       :select-year [:i18n :select-year]
                       :select-challenge [:i18n :select-challenge]}
                :handlers {:on-close-modal [:dispatch [:aoc/close-modal]]
                           :on-submit-solution [:dispatch [:aoc/submit-solution]]
                           :on-update-form [:dispatch [:aoc/update-form]]
                           :on-select-year [:dispatch [:aoc/modal-select-year]]
                           :on-select-challenge [:dispatch [:aoc/modal-select-challenge]]}}})

(defn build-initial-page-data
  "Build initial AoC page data with all options populated."
  []
  (let [initial-data (initial-aoc-data)
        year (get-in initial-data [:selector-data :selected-year])
        years-options (selection/build-years-options)
        challenges-options (selection/build-challenges-options year)]
    (-> initial-data
        (assoc-in [:selector-data :years-options] years-options)
        (assoc-in [:selector-data :challenges-options] challenges-options)
        (assoc-in [:modal-data :years-options] years-options)
        (assoc-in [:modal-data :challenges-options] challenges-options))))
