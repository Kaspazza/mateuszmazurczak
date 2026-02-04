(ns mateuszmazurczak.application.aoc.page-data
  "AoC page data preparation - application layer orchestration."
  (:require
   [mateuszmazurczak.application.aoc.page-schema :as page-schema]
   [mateuszmazurczak.application.aoc.solution    :as app-solution]
   [mateuszmazurczak.domain.aoc.solution         :as solution]
   [mateuszmazurczak.frontend-i18n               :as fi18n]
   [mateuszmazurczak.ports.events                :as events]
   [mateuszmazurczak.ports.logging               :as log]))

(defn prepare-ui-data
  "Prepare AoC page data for UI display.
   
   Orchestrates domain transformations and port operations to convert
   raw app-db data into component-ready UI data with validation."
  [raw-data solutions-entities theme admin-logged-in? logger]
  (let [solution-ids (get-in raw-data
                             (page-schema/relative-path page-schema/*aoc-solution-ids-path*))
        _ (log/log! logger
                    {:level :info
                     :id ::prepare-ui-data-start
                     :msg "Starting prepare-ui-data"
                     :data {:solution-ids solution-ids
                            :solution-ids-count (count solution-ids)
                            :entities-count (count solutions-entities)}})
        user-solution-ids
        (get-in raw-data (page-schema/relative-path page-schema/*aoc-user-solution-ids-path*))
        denormalized-solutions (app-solution/denormalize-and-enrich-solutions
                                solution-ids
                                solutions-entities
                                solution/denormalize-solutions)
        
        solutions-text-raw
        (get-in raw-data (page-schema/relative-path page-schema/*aoc-solutions-text-path*))
        solutions-text (fi18n/i18n-markers->translation solutions-text-raw)
        _ (log/log! logger
                    {:level :info
                     :id ::after-denormalization
                     :msg "After denormalization"
                     :data {:denormalized-count (count denormalized-solutions)
                            :theme theme :solutions-text solutions-text
                            :first-solution (first denormalized-solutions)}})
        prepared-solutions
        (->> denormalized-solutions
             (mapv #(app-solution/enrich-solution-with-ui-context % theme solutions-text))
             (app-solution/sort-solutions-for-display user-solution-ids))
        _ (log/log! logger
                    {:level :info
                     :id ::after-preparation
                     :msg "After UI enrichment and sorting"
                     :data {:prepared-count (count prepared-solutions)
                            :first-prepared (first prepared-solutions)}})
        ui-data
        (-> raw-data
            events/dispatch-markers->handlers
            fi18n/i18n-markers->translation
            (assoc-in (page-schema/relative-path page-schema/*aoc-solutions-path*)
                      prepared-solutions)
            (update-in (page-schema/relative-path page-schema/*aoc-solutions-data-path*)
                       dissoc
                       :solution-ids)
            (assoc-in (page-schema/relative-path page-schema/*aoc-solutions-theme-path*) theme)
            (assoc-in (page-schema/relative-path page-schema/*aoc-solutions-admin-logged-in-path*)
                      admin-logged-in?)
            (update-in (page-schema/relative-path page-schema/*aoc-solutions-path*)
                       events/dispatch-markers->handlers))
        _ (log/log! logger
                    {:level :info
                     :id ::before-validation
                     :msg "Before validation"
                     :data {:solutions-in-ui-data (get-in ui-data (page-schema/relative-path page-schema/*aoc-solutions-path*))
                            :solutions-count (count (get-in ui-data (page-schema/relative-path page-schema/*aoc-solutions-path*)))}})
        valid? (page-schema/valid-aoc-page-ui-data? ui-data)
        explanation (when-not valid? (page-schema/explain-aoc-page-ui-data ui-data))
        _ (log/log! logger
                    {:level (if valid? :info :error)
                     :id ::validation-result
                     :msg (if valid? "Validation passed" "Validation FAILED")
                     :data {:valid? valid?
                            :explanation explanation}})]
    (when-not valid?
      (log/error! logger
                  {:error (ex-info "AOC page validation failed"
                                   {:type ::aoc-validation-failed
                                    :explanation explanation
                                    :raw-data raw-data})}))
    (if valid?
      {:data ui-data
       :valid? true}
      {:data ui-data
       :valid? false
       :error {:id ::aoc-validation-failed
               :actual-data raw-data
               :explanation explanation
               :text {:title (fi18n/tr :page-data-validation-error)
                      :description (fi18n/tr :page-data-error-description)
                      :refresh-page (fi18n/tr :refresh-page)
                      :validation-errors (fi18n/tr :validation-errors)
                      :raw-data-received (fi18n/tr :raw-data-received)
                      :click-to-expand-raw-data (fi18n/tr :click-to-expand-raw-data)}}})))

(comment

(def solution-ids {:header-data
 {:text
  {:title [:i18n :advent-of-code-solutions],
   :description [:i18n :share-and-explore-solutions]}},
 :selector-data
 {:selected-year 2025,
  :selected-challenge 2,
  :years-options
  [{:value 2025, :label "2025"}
   {:value 2024, :label "2024"}
   {:value 2023, :label "2023"}
   {:value 2022, :label "2022"}
   {:value 2021, :label "2021"}
   {:value 2020, :label "2020"}
   {:value 2019, :label "2019"}
   {:value 2018, :label "2018"}
   {:value 2017, :label "2017"}
   {:value 2016, :label "2016"}
   {:value 2015, :label "2015"}],
  :challenges-options
  [{:value 1, :label "Day 1"}
   {:value 2, :label "Day 2"}
   {:value 3, :label "Day 3"}
   {:value 4, :label "Day 4"}
   {:value 5, :label "Day 5"}
   {:value 6, :label "Day 6"}
   {:value 7, :label "Day 7"}
   {:value 8, :label "Day 8"}
   {:value 9, :label "Day 9"}
   {:value 10, :label "Day 10"}
   {:value 11, :label "Day 11"}
   {:value 12, :label "Day 12"}],
  :text
  {:year [:i18n :year],
   :challenge [:i18n :challenge],
   :select-year [:i18n :select-year],
   :select-challenge [:i18n :select-challenge]},
  :handlers
  {:on-select-year [:dispatch [:aoc/select-year]],
   :on-select-challenge [:dispatch [:aoc/select-challenge]]}},
 :upload-data
 {:upload-count 0,
  :text {:upload-solution [:i18n :upload-solution]},
  :handlers
  {:on-open-modal [:dispatch [:aoc/open-modal]],
   :on-upload-limit-reached [:dispatch [:aoc/upload-limit-reached]]}},
 :solutions-data
 {:solution-ids
  ["30ca9c31-63bf-4713-9c7f-acb11c642d36"
   "e18416cf-bb7c-4b45-8bdd-2170d550dd18"
   "7360ecc6-2896-45bb-89fe-150a682a30ca"],
  :loading? true,
  :gated? false,
  :user-solution-ids #{},
  :text
  {:only-if-solved-no-cheating [:i18n :only-if-solved-no-cheating],
   :no-solutions-yet [:i18n :no-solutions-yet],
   :show-me-solutions [:i18n :show-me-solutions],
   :view-repository [:i18n :view-repository],
   :unlock-community-solutions [:i18n :unlock-community-solutions],
   :delete [:i18n :delete],
   :clever [:i18n :clever],
   :confirm-delete [:i18n :confirm-delete],
   :unknown-content-type [:i18n :unknown-content-type],
   :be-first-to-share [:i18n :be-first-to-share],
   :loading-solutions [:i18n :loading-solutions],
   :your-solution [:i18n :your-solution],
   :best-practices [:i18n :best-practices]},
  :handlers
  {:on-vote [:dispatch [:aoc/vote]],
   :on-give-consent [:dispatch [:aoc/give-consent]],
   :on-delete-solution [:dispatch [:admin/delete-solution]]}},
 :modal-data
 {:submitting? false,
  :challenges-options
  [{:value 1, :label "Day 1"}
   {:value 2, :label "Day 2"}
   {:value 3, :label "Day 3"}
   {:value 4, :label "Day 4"}
   {:value 5, :label "Day 5"}
   {:value 6, :label "Day 6"}
   {:value 7, :label "Day 7"}
   {:value 8, :label "Day 8"}
   {:value 9, :label "Day 9"}
   {:value 10, :label "Day 10"}
   {:value 11, :label "Day 11"}
   {:value 12, :label "Day 12"}],
  :playground-url nil,
  :handlers
  {:on-close-modal [:dispatch [:aoc/close-modal]],
   :on-submit-solution [:dispatch [:aoc/submit-solution]],
   :on-update-form [:dispatch [:aoc/update-form]],
   :on-select-year [:dispatch [:aoc/modal-select-year]],
   :on-select-challenge [:dispatch [:aoc/modal-select-challenge]]},
  :years-options
  [{:value 2025, :label "2025"}
   {:value 2024, :label "2024"}
   {:value 2023, :label "2023"}
   {:value 2022, :label "2022"}
   {:value 2021, :label "2021"}
   {:value 2020, :label "2020"}
   {:value 2019, :label "2019"}
   {:value 2018, :label "2018"}
   {:value 2017, :label "2017"}
   {:value 2016, :label "2016"}
   {:value 2015, :label "2015"}],
  :modal-open? false,
  :form
  {:author-name "",
   :github-username "",
   :content-type :code-snippet,
   :content "",
   :year 2025,
   :challenge 1},
  :form-errors nil,
  :text
  {:repo-placeholder [:i18n :repo-placeholder],
   :submit-solution [:i18n :submit-solution],
   :name-placeholder [:i18n :name-placeholder],
   :your-name [:i18n :your-name],
   :github-username-optional [:i18n :github-username-optional],
   :select-year [:i18n :select-year],
   :repository-url [:i18n :repository-url],
   :your-code [:i18n :your-code],
   :uploading-for [:i18n :uploading-for],
   :repository-link [:i18n :repository-link],
   :share-your-advent-of-code-solution
   [:i18n :share-your-advent-of-code-solution],
   :year [:i18n :year],
   :upload-your-solution [:i18n :upload-your-solution],
   :code-snippet [:i18n :code-snippet],
   :challenge [:i18n :challenge],
   :submitting [:i18n :submitting],
   :select-challenge [:i18n :select-challenge],
   :content-type [:i18n :content-type],
   :code-placeholder [:i18n :code-placeholder],
   :cancel [:i18n :cancel],
   :source-playground [:i18n :source-playground],
   :github-placeholder [:i18n :github-placeholder]}}}
)




(def solutions [{:author-name "Logan Turner", :clever-count 1, :content ";; Part 1\n\n(defn interpret-input [s]\n  (for [[dir & n-chars] (str/split-lines s)]\n    (* ({\\L -1, \\R +1} dir)\n       (Long/parseLong (apply str n-chars)))))\n\n(defn integer-overflow-99 [n]\n  (let [r (rem n 100)]\n    (cond\n      (< 99 r) (- r 99 1)\n      (< r 0) (+ r 99 1)\n      :else r)))\n\n(defn day-1-part-1 [input]\n  (->> (interpret-input input)\n       (reductions + 50)\n       (map integer-overflow-99)\n       (filter #{0})\n       count))\n\n;; Part 2\n\n(defn hundreds-passed\n  \"Calculates how many multiples of 100 (including zero) are passed or reached\n   when counting the integer range `a` to `b`. (a exclusive, b inclusive.)\"\n  [[a b]]\n  (let [[q r] ((juxt quot rem) (Math/abs (- b a)) 100)]\n    (+ q ;; complete hundreds of difference\n       (if (< a b)\n         (quot (+ r (integer-overflow-99 a)) 100)\n         (if (<= (integer-overflow-99 (dec a)) (dec r)) 1 0))))) \n               ;; crossing another hundred mark with the remainder\n\n(defn day-1-part-2 [input]\n  (->> (interpret-input input)\n       (reductions + 50)\n       (partition 2 1)\n       (map hundreds-passed)\n       (apply +)))", :github-profile "https://github.com/loganturner", :on-vote-clever [:dispatch [:aoc/vote "30ca9c31-63bf-4713-9c7f-acb11c642d36" :clever]], :year 2025, :voted-clever? false, :best-practices-count 0, :challenge 1, :on-vote-best-practices [:dispatch [:aoc/vote "30ca9c31-63bf-4713-9c7f-acb11c642d36" :best-practices]], :id "30ca9c31-63bf-4713-9c7f-acb11c642d36", :voted-best-practices? false, :content-type :code-snippet, :github-username "loganturner", :created-at "Tue Dec 02 20:00:43 CET 2025", :github-username-display "@loganturner"}])
  (def theme  :light),
  (def  solutions-text {:only-if-solved-no-cheating "Only if you solved it yourself, no cheating!", :no-solutions-yet "No solutions yet for this challenge.", :show-me-solutions "Show me solutions", :view-repository "View solution externally", :unlock-community-solutions "Unlock community solutions", :delete "Delete", :clever "Clever", :confirm-delete "Are you sure you want to delete this solution?", :unknown-content-type "Unknown content type", :be-first-to-share "Be the first to share your solution!", :loading-solutions "Loading solutions...", :your-solution "Your Solution", :best-practices "Best practices"})
  (mapv #(app-solution/enrich-solution-with-ui-context % theme solutions-text) solutions)

  ;;
  )
