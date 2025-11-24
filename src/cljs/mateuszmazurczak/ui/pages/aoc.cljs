(ns mateuszmazurczak.ui.pages.aoc
  "Advent of Code solutions page UI."
  (:require
   ["lucide-react"                            :refer [ArrowUp]]
   [mateuszmazurczak.ui.components.button     :as button]
   [mateuszmazurczak.ui.components.code-block :as code-block]
   [mateuszmazurczak.ui.components.dialog     :as dialog]
   [mateuszmazurczak.ui.components.input      :as input]
   [mateuszmazurczak.ui.components.label      :as label]
   [mateuszmazurczak.ui.components.select     :as select]
   [mateuszmazurczak.ui.components.textarea   :as textarea]))

(defn author-info
  [{:keys [author-name github-profile github-username created-at]
    :as _author-data}]
  [:div {:class "flex items-center gap-3 mb-4"}
   [:div {:class "flex-1"}
    [:div {:class "flex items-center gap-2"}
     [:h3 {:class "font-semibold text-lg"}
      author-name]
     (when (and github-profile github-username)
       [:a {:href github-profile
            :target "_blank"
            :rel "noopener noreferrer"
            :class "text-sm text-muted-foreground hover:text-primary transition-colors"}
        github-username])]
    (when created-at
      [:p {:class "text-xs text-muted-foreground mt-1"}
       created-at])]])

(defn solution-info
  "Display solution content based on type.
  
  Props:
  - :content-type (:code-snippet | :repo-link) - Type of content
  - :content - The actual content (code or URL)
  - :theme - Current theme (:light | :dark)
  - :text - Map of translated text strings"
  [{:keys [content-type content theme text]
    :as _solution-card-data}]
  [:div {:class "mt-4"}
   (cond
     (= content-type :code-snippet) [code-block/copy-block {:text content
                                                            :language "clojure"
                                                            :theme theme}]
     (= content-type :repo-link)
     [:a {:href content
          :target "_blank"
          :rel "noopener noreferrer"
          :class "inline-flex items-center gap-2 text-primary hover:underline"}
      [:svg {:class "size-5"
             :xmlns "http://www.w3.org/2000/svg"
             :view-box "0 0 24 24"
             :fill "none"
             :stroke "currentColor"
             :stroke-width "2"}
       [:path {:d "M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"}]
       [:path {:d "M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"}]]
      [:span (:view-repository text)]]
     :else [:p (:unknown-content-type text)])])

(defn vote-buttons
  "Display vote buttons for solution.
  
  Props:
  - :best-practices-count - Number of best practices votes
  - :clever-count - Number of clever votes
  - :text - Map of translated text strings"
  [{:keys [best-practices-count clever-count text]
    :as _vote-data}]
  [:div {:class "flex gap-2 mt-4"}
   [button/button {:variant :secondary
                   :size :xs}
    [:> ArrowUp]
    (str (:best-practices text) " " (or best-practices-count 0))]
   [button/button {:variant :secondary
                   :size :xs}
    [:> ArrowUp]
    (str (:clever text) " " (or clever-count 0))]])

(defn solution-card
  "Display a single solution card.
  
  Props:
  - solution-card-data - Map containing solution data including theme and text"
  [solution-card-data]
  [:div {:class "border rounded-lg p-6 bg-card shadow-sm hover:shadow-md transition-shadow"}
   [author-info solution-card-data]
   [solution-info solution-card-data]
   [vote-buttons solution-card-data]])

(defn input-author
  [{:keys [form submitting? on-update-form text]
    :as _form-data}]
  [:div {:class "space-y-2"}
   [label/label {:htmlFor "author-name"}
    (str (:your-name text) " *")]
   [input/input {:id "author-name"
                 :type "text"
                 :value (:author-name form)
                 :placeholder (:name-placeholder text)
                 :disabled submitting?
                 :on-change #(on-update-form :author-name
                                             (-> %
                                                 .-target
                                                 .-value))}]])

(defn input-gh
  [{:keys [form submitting? on-update-form text]
    :as _form-data}]
  [:div {:class "space-y-2"}
   [label/label {:htmlFor "github-profile"}
    (:github-profile-optional text)]
   [input/input {:id "github-profile"
                 :type "url"
                 :value (:github-profile form)
                 :placeholder (:github-placeholder text)
                 :disabled submitting?
                 :on-change #(on-update-form :github-profile
                                             (-> %
                                                 .-target
                                                 .-value))}]])

(defn upload-modal
  "Modal for uploading a solution."
  [{:keys [modal-open? form submitting? on-close-modal on-update-form on-submit-solution text]
    :as form-data}]
  [dialog/dialog {:open modal-open?
                  :onOpenChange #(when-not % (on-close-modal))}
   [dialog/dialog-content {}
    [dialog/dialog-header {}
     [dialog/dialog-title {}
      (:upload-your-solution text)]
     [dialog/dialog-description {}
      (:share-your-advent-of-code-solution text)]]
    [:div {:class "space-y-4 py-4"}
     [input-author form-data]
     [input-gh form-data]
     [:div {:class "space-y-2"}
      [label/label {}
       (str (:content-type text) " *")]
      [:div {:class "flex gap-4"}
       [:label {:class "flex items-center gap-2 cursor-pointer"}
        [:input {:type "radio"
                 :name "content-type"
                 :checked (= (:content-type form) :code-snippet)
                 :disabled submitting?
                 :on-change #(on-update-form :content-type :code-snippet)}]
        [:span (:code-snippet text)]]
       [:label {:class "flex items-center gap-2 cursor-pointer"}
        [:input {:type "radio"
                 :name "content-type"
                 :checked (= (:content-type form) :repo-link)
                 :disabled submitting?
                 :on-change #(on-update-form :content-type :repo-link)}]
        [:span (:repository-link text)]]]]
     [:div {:class "space-y-2"}
      [label/label {:htmlFor "content"}
       (str (if (= (:content-type form) :code-snippet) (:your-code text) (:repository-url text))
            " *")]
      (if (= (:content-type form) :code-snippet)
        [textarea/textarea {:id "content"
                            :value (:content form)
                            :placeholder (:code-placeholder text)
                            :rows 10
                            :disabled submitting?
                            :on-change #(on-update-form :content
                                                        (-> %
                                                            .-target
                                                            .-value))}]
        [input/input {:id "content"
                      :type "url"
                      :value (:content form)
                      :placeholder (:repo-placeholder text)
                      :disabled submitting?
                      :on-change #(on-update-form :content
                                                  (-> %
                                                      .-target
                                                      .-value))}])]]
    [dialog/dialog-footer {}
     [button/button {:variant :outline
                     :disabled submitting?
                     :on-click on-close-modal}
      (:cancel text)]
     [button/button {:disabled submitting?
                     :on-click on-submit-solution}
      (if submitting? (:submitting text) (:submit-solution text))]]]])

(defn year-selector
  "Year dropdown selector."
  [selected-year years-options on-select-year text]
  [:div {:class "space-y-2"}
   [label/label {}
    (:year text)]
   [select/select {:value (str selected-year)
                   :onValueChange on-select-year}
    [select/select-trigger {:class "w-[180px]"}
     [select/select-value {:placeholder (:select-year text)}]]
    [select/select-content {}
     (for [{:keys [value label]} years-options]
       ^{:key value}
       [select/select-item {:value (str value)}
        label])]]])

(defn challenge-selector
  "Challenge dropdown selector."
  [selected-challenge challenges-options on-select-challenge text]
  [:div {:class "space-y-2"}
   [label/label {}
    (:challenge text)]
   [select/select {:value (str selected-challenge)
                   :onValueChange on-select-challenge}
    [select/select-trigger {:class "w-[180px]"}
     [select/select-value {:placeholder (:select-challenge text)}]]
    [select/select-content {}
     (for [{:keys [value label]} challenges-options]
       ^{:key value}
       [select/select-item {:value (str value)}
        label])]]])

(defn part-selector
  "Part dropdown selector."
  [selected-part on-select-part text]
  [:div {:class "space-y-2"}
   [label/label {}
    (:part text)]
   [select/select {:value (str selected-part)
                   :onValueChange on-select-part}
    [select/select-trigger {:class "w-[180px]"}
     [select/select-value {:placeholder (:select-part text)}]]
    [select/select-content {}
     [select/select-item {:value "1"}
      (:part-1 text)]
     [select/select-item {:value "2"}
      (:part-2 text)]]]])

(defn page-header
  [text]
  [:div {:class "mb-8"}
   [:h1 {:class "text-4xl font-bold mb-2"}
    (:advent-of-code-solutions text)]
   [:p {:class "text-muted-foreground"}
    (:share-and-explore-solutions text)]])

(defn aoc-page
  "Main Advent of Code page.
  
  Pure presentation component that receives all data via props including theme and text.
  
  Props:
  - :selected-year - Currently selected year
  - :selected-challenge - Currently selected challenge
  - :selected-part - Currently selected part (1 or 2)
  - :years-options - Available year options
  - :challenges-options - Available challenge options
  - :solutions - List of solutions to display
  - :loading? - Whether solutions are loading
  - :modal-open? - Whether upload modal is open
  - :form - Form data for upload modal
  - :submitting? - Whether form is submitting
  - :theme - Current theme (:light | :dark)
  - :text - Map of translated text strings
  - :handlers - Map of event handlers"
  [{:keys [selected-year
           selected-challenge
           selected-part
           years-options
           challenges-options
           solutions
           loading?
           modal-open?
           form
           submitting?
           theme
           text
           handlers]}]
  (let [{:keys [on-select-year
                on-select-challenge
                on-select-part
                on-open-modal
                on-close-modal
                on-submit-solution
                on-update-form]}
        handlers]
    [:div {:class "min-h-screen bg-background"}
     [:div {:class "container mx-auto px-4 py-8 max-w-5xl"}
      [page-header text]
      [:div {:class "flex flex-wrap items-end gap-4 mb-8 p-6 bg-card rounded-lg border shadow-sm"}
       [year-selector selected-year years-options on-select-year text]
       [challenge-selector selected-challenge challenges-options on-select-challenge text]
       [part-selector selected-part on-select-part text]
       [:div {:class "ml-auto"}
        [button/button {:on-click on-open-modal
                        :size :lg}
         [:svg {:class "size-5"
                :xmlns "http://www.w3.org/2000/svg"
                :view-box "0 0 24 24"
                :fill "none"
                :stroke "currentColor"
                :stroke-width "2"}
          [:path {:d "M5 12h14"}]
          [:path {:d "M12 5v14"}]]
         (:upload-solution text)]]]
      [:div {:class "space-y-4"}
       (cond
         loading?
         [:div {:class "text-center py-12"}
          [:div {:class "inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-primary"}]
          [:p {:class "mt-4 text-muted-foreground"}
           (:loading-solutions text)]]
         (empty? solutions) [:div {:class "text-center py-12 bg-card rounded-lg border"}
                             [:p {:class "text-muted-foreground text-lg"}
                              (:no-solutions-yet text)]
                             [:p {:class "text-muted-foreground mt-2"}
                              (:be-first-to-share text)]]
         :else (for [solution solutions]
                 ^{:key (:id solution)} [solution-card (assoc solution :theme theme :text text)]))]]
     [upload-modal {:modal-open? modal-open?
                    :form form
                    :submitting? submitting?
                    :on-close-modal on-close-modal
                    :on-submit-solution on-submit-solution
                    :on-update-form on-update-form
                    :text text}]]))
