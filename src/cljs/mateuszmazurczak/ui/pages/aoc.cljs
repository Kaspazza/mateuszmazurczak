(ns mateuszmazurczak.ui.pages.aoc
  "Advent of Code solutions page UI."
  (:require
   ["lucide-react"                            :refer [ArrowUp Check]]
   [mateuszmazurczak.ui.components.admin      :as admin]
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
  - :voted-best-practices? - Whether user has voted for best practices
  - :voted-clever? - Whether user has voted for clever
  - :on-vote-best-practices - Handler for best practices vote
  - :on-vote-clever - Handler for clever vote
  - :text - Map of translated text strings"
  [{:keys [best-practices-count
           clever-count
           voted-best-practices?
           voted-clever?
           on-vote-best-practices
           on-vote-clever
           text]
    :as _vote-data}]
  [:div {:class "flex gap-2 mt-4"}
   [button/button {:variant (if voted-best-practices? :default :secondary)
                   :size :xs
                   :disabled voted-best-practices?
                   :on-click on-vote-best-practices}
    (if voted-best-practices? [:> Check {:class "size-4"}] [:> ArrowUp {:class "size-4"}])
    (str (:best-practices text) " " (or best-practices-count 0))]
   [button/button {:variant (if voted-clever? :default :secondary)
                   :size :xs
                   :disabled voted-clever?
                   :on-click on-vote-clever}
    (if voted-clever? [:> Check {:class "size-4"}] [:> ArrowUp {:class "size-4"}])
    (str (:clever text) " " (or clever-count 0))]])

(defn solution-card
  "Display a single solution card.
   
   Props:
   - solution-card-data - Map containing solution data including theme, text, and handlers
   - is-user-solution? - Whether this is the current user's solution
   - admin-logged-in? - Whether admin is logged in
   - on-delete-solution - Delete handler function (admin only)"
  [solution-card-data is-user-solution? admin-logged-in? on-delete-solution]
  [:div {:class (str "border rounded-lg p-6 shadow-sm hover:shadow-md transition-shadow "
                     (if is-user-solution? "bg-primary/5 border-primary border-2" "bg-card"))}
   [:div {:class "flex justify-between items-start mb-3"}
    (when is-user-solution?
      [:div {:class "flex items-center gap-2 text-primary font-semibold text-sm"}
       [:svg {:class "size-4"
              :xmlns "http://www.w3.org/2000/svg"
              :view-box "0 0 24 24"
              :fill "none"
              :stroke "currentColor"
              :stroke-width "2"}
        [:path {:d "M9 12l2 2 4-4"}]
        [:circle {:cx "12"
                  :cy "12"
                  :r "10"}]]
       [:span (:your-solution (:text solution-card-data))]])
    (when admin-logged-in?
      [:div {:class "ml-auto"}
       [admin/delete-solution-button {:solution-id (:id solution-card-data)
                                      :text (:text solution-card-data)
                                      :on-delete on-delete-solution}]])]
   [author-info solution-card-data]
   [solution-info solution-card-data]
   [vote-buttons solution-card-data]])

(defn input-author
  [{:keys [form submitting? on-update-form text form-errors]
    :as _form-data}]
  (let [has-error? (contains? form-errors :author-name)
        error-msg (get form-errors :author-name)]
    [:div {:class "space-y-2"}
     [label/label {:htmlFor "author-name"}
      (:your-name text)]
     [input/input {:id "author-name"
                   :type "text"
                   :value (:author-name form)
                   :placeholder (:name-placeholder text)
                   :disabled submitting?
                   :class (when has-error? "border-destructive focus-visible:ring-destructive")
                   :on-change #(on-update-form :author-name
                                               (-> %
                                                   .-target
                                                   .-value))}]
     (when has-error?
       [:p {:class "text-xs text-destructive"}
        error-msg])]))

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
  [{:keys [modal-open? form submitting? text form-errors handlers]
    :as _upload-modal-data}]
  (let [{:keys [on-close-modal on-update-form on-submit-solution]} handlers
        form-data {:form form
                   :submitting? submitting?
                   :on-update-form on-update-form
                   :text text
                   :form-errors form-errors}]
    [dialog/dialog {:open modal-open?
                    :onOpenChange #(when-not % (on-close-modal))}
     [dialog/dialog-content {:class "sm:max-w-4xl max-h-[90vh] p-8 flex flex-col"}
      [dialog/dialog-header {}
       [dialog/dialog-title {}
        (:upload-your-solution text)]
       [dialog/dialog-description {}
        (:share-your-advent-of-code-solution text)]]
      [:div {:class "space-y-4 py-4 px-2 overflow-y-auto flex-1"}
       [input-author form-data]
       [input-gh form-data]
       [:div {:class "space-y-2"}
        [label/label {}
         (:content-type text)]
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
       (let [has-error? (contains? form-errors :content)
             error-msg (get form-errors :content)]
         [:div {:class "space-y-2"}
          [label/label {:htmlFor "content"}
           (if (= (:content-type form) :code-snippet) (:your-code text) (:repository-url text))]
          (if (= (:content-type form) :code-snippet)
            [textarea/textarea {:id "content"
                                :value (:content form)
                                :placeholder (:code-placeholder text)
                                :rows 10
                                :disabled submitting?
                                :class (when has-error?
                                         "border-destructive focus-visible:ring-destructive")
                                :on-change #(on-update-form :content
                                                            (-> %
                                                                .-target
                                                                .-value))}]
            [input/input {:id "content"
                          :type "url"
                          :value (:content form)
                          :placeholder (:repo-placeholder text)
                          :disabled submitting?
                          :class (when has-error?
                                   "border-destructive focus-visible:ring-destructive")
                          :on-change #(on-update-form :content
                                                      (-> %
                                                          .-target
                                                          .-value))}])
          (when has-error?
            [:p {:class "text-xs text-destructive"}
             error-msg])])]
      [dialog/dialog-footer {}
       [button/button {:variant :outline
                       :disabled submitting?
                       :on-click on-close-modal}
        (:cancel text)]
       [button/button {:disabled submitting?
                       :on-click on-submit-solution}
        (if submitting? (:submitting text) (:submit-solution text))]]]]))

(defn year-selector
  "Year dropdown selector."
  [{:keys [selected-year years-options on-select-year text]}]
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
  [{:keys [selected-challenge challenges-options on-select-challenge text]}]
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
  [{:keys [selected-part on-select-part text]}]
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
  [{:keys [text]}]
  [:div {:class "mb-8"}
   [:h1 {:class "text-4xl font-bold mb-2"}
    (:advent-of-code-solutions text)]
   [:p {:class "text-muted-foreground"}
    (:share-and-explore-solutions text)]])

(defn solution-selector
  [{:keys [selected-year
           years-options
           text
           selected-challenge
           challenges-options
           selected-part
           handlers]}]
  (let [{:keys [on-select-year on-select-challenge on-select-part]} handlers]
    [:<>
     [year-selector {:selected-year selected-year
                     :years-options years-options
                     :on-select-year on-select-year
                     :text text}]
     [challenge-selector {:selected-challenge selected-challenge
                          :challenges-options challenges-options
                          :on-select-challenge on-select-challenge
                          :text text}]
     [part-selector {:selected-part selected-part
                     :on-select-part on-select-part
                     :text text}]]))

(defn upload-solution
  [{:keys [upload-count text handlers]}]
  (let [{:keys [on-open-modal]} handlers
        upload-count (or upload-count 0)
        can-upload? (< upload-count 5)]
    [:div {:class "ml-auto flex flex-col items-end gap-2"}
     [button/button {:on-click on-open-modal
                     :size :lg
                     :disabled (not can-upload?)}
      [:svg {:class "size-5"
             :xmlns "http://www.w3.org/2000/svg"
             :view-box "0 0 24 24"
             :fill "none"
             :stroke "currentColor"
             :stroke-width "2"}
       [:path {:d "M5 12h14"}]
       [:path {:d "M12 5v14"}]]
      (:upload-solution text)]]))

(defn solutions-container
  [{:keys [text
           loading?
           gated?
           solutions
           selected-year
           selected-challenge
           selected-part
           user-solution-ids
           admin-logged-in?
           handlers]}]
  (let [{:keys [on-give-consent on-delete-solution]} handlers]
    [:div {:class "space-y-4"}
     (cond
       loading? [:div {:class "text-center py-12"}
                 [:div {:class
                        "inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-primary"}]
                 [:p {:class "mt-4 text-muted-foreground"}
                  (:loading-solutions text)]]
       (and gated? (seq solutions))
       [:div {:class "text-center py-12 bg-card rounded-lg border-2 border-dashed"}
        [:div {:class "max-w-md mx-auto"}
         [:svg {:class "size-16 mx-auto text-muted-foreground mb-4"
                :xmlns "http://www.w3.org/2000/svg"
                :view-box "0 0 24 24"
                :fill "none"
                :stroke "currentColor"
                :stroke-width "1.5"}
          [:rect {:x "3"
                  :y "11"
                  :width "18"
                  :height "11"
                  :rx "2"
                  :ry "2"}]
          [:path {:d "M7 11V7a5 5 0 0 1 10 0v4"}]]
         [:p {:class "text-muted-foreground text-lg font-semibold mb-2"}
          (:unlock-community-solutions text)]
         [:p {:class "text-muted-foreground text-sm mb-4"}
          (:only-if-solved-no-cheating text)]
         [button/button {:on-click
                         #(when on-give-consent
                            (on-give-consent selected-year selected-challenge selected-part))
                         :size :lg}
          (:show-me-solutions text)]]]
       (empty? solutions) [:div {:class "text-center py-12 bg-card rounded-lg border"}
                           [:p {:class "text-muted-foreground text-lg"}
                            (:no-solutions-yet text)]
                           [:p {:class "text-muted-foreground mt-2"}
                            (:be-first-to-share text)]]
       :else
       (for [solution solutions]
         (let [is-user-solution? (contains? user-solution-ids (:id solution))]
           ^{:key (:id solution)}
           [solution-card solution is-user-solution? admin-logged-in? on-delete-solution])))]))

(defn aoc-page
  "Main Advent of Code page.
  
  Pure presentation component that receives structured data matching component hierarchy.
  
  Props (nested structure):
  - :header-data - Page title and description with text
  - :selector-data - Year/challenge/part filters with options, handlers, and text
  - :upload-data - Upload button with count, handlers, and text
  - :solutions-data - Solutions list with state (loading/gated), handlers, theme, and text
  - :modal-data - Form with state, handlers, and text"
  [{:keys [header-data selector-data upload-data solutions-data modal-data]}]
  [:div {:class "min-h-screen bg-background"}
   [:div {:class "container mx-auto px-4 py-8 max-w-5xl"}
    [page-header header-data]
    [:div {:class "flex flex-wrap items-end gap-4 mb-8 p-6 bg-card rounded-lg border shadow-sm"}
     [solution-selector selector-data]
     [upload-solution upload-data]]
    [solutions-container
     (assoc solutions-data
            :selected-year (:selected-year selector-data)
            :selected-challenge (:selected-challenge selector-data)
            :selected-part (:selected-part selector-data))]]
   [upload-modal modal-data]])
