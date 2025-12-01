(ns mateuszmazurczak.ui.pages.aoc
  "Advent of Code solutions page UI."
  (:require
   ["lucide-react"                               :refer [ArrowUp
                                                         Check
                                                         ChevronDown
                                                         ChevronUp
                                                         ExternalLink
                                                         Link2]]
   ["pako"                                       :as pako]
   [clojure.string                               :as str]
   [mateuszmazurczak.ui.components.admin         :as admin]
   [mateuszmazurczak.ui.components.button        :as button]
   [mateuszmazurczak.ui.components.code-block    :as code-block]
   [mateuszmazurczak.ui.components.dialog        :as dialog]
   [mateuszmazurczak.ui.components.dropdown-menu :as dropdown-menu]
   [mateuszmazurczak.ui.components.input         :as input]
   [mateuszmazurczak.ui.components.label         :as label]
   [mateuszmazurczak.ui.components.select        :as select]
   [mateuszmazurczak.ui.components.textarea      :as textarea]
   [reagent.core                                 :as r]))

(defn copy-solution-link!
  "Copy solution link to clipboard and show feedback.
  Returns a function that copies the link when called."
  [solution-id copied-atom]
  (fn []
    (let [url (str (.-origin js/window.location)
                   (.-pathname js/window.location)
                   "#solution-"
                   solution-id)]
      (-> (js/navigator.clipboard.writeText url)
          (.then (fn [] (reset! copied-atom true) (js/setTimeout #(reset! copied-atom false) 2000)))
          (.catch (fn [err] (js/console.error "Failed to copy link:" err)))))))

(def ^:private aoc-helper-comment
  ";; Helper functions:
;; (fetch-input year day) - get AOC input
;; (append str) - append str to DOM
;; (spy x) - log x to console and return x

;; Example fetch call.
;;(def input (->> (js-await (fetch-input 2022 1))
;;             #_spy
;;             str/split-lines
;;             (mapv parse-long)))

")

(defn- uint8array-to-binary-string
  "Convert Uint8Array to binary string for btoa encoding.
  
  This is necessary because pako.gzip returns a Uint8Array and btoa expects
  a binary string (each character represents a byte)."
  [uint8array]
  (let [len (.-length uint8array)
        chars (js/Array. len)]
    (dotimes [i len] (aset chars i (.fromCharCode js/String (aget uint8array i))))
    (.join chars "")))

(defn- compose-playground-url
  "Compose a playground URL (Squint or Cherry) for code content.
  
  Uses gzip compression for Squint (supported), plain base64 for Cherry (not supported).
  
  Args:
  - code: The code string to open in playground
  - opts: Optional map with:
    - :playground - Playground type (:squint | :cherry), default :squint
    - :boilerplate - URL to boilerplate code
    - :repl - Enable REPL mode (default: true)
  
  Returns: Complete playground URL"
  [code
   {:keys [playground boilerplate repl]
    :or {playground :squint
         repl true}
    :as _opts}]
  (let [base-url (case playground
                   :cherry "https://squint-cljs.github.io/cherry/"
                   :squint "https://squint-cljs.github.io/squint/")
        encoded-code (if (= playground :squint)
                       (let [compressed (pako/gzip code)
                             binary-string (uint8array-to-binary-string compressed)]
                         (str "gzip:" (js/btoa binary-string)))
                       (js/btoa code))
        url (js/URL. base-url)]
    (.. url -searchParams (set "src" encoded-code))
    (when boilerplate (.. url -searchParams (set "boilerplate" boilerplate)))
    (.. url -searchParams (set "repl" (str repl)))
    (.toString url)))

(defn open-in-playground-menu
  "Dropdown menu to open code solution in interactive playground (Squint or Cherry).
  Only shown for code-snippet content type."
  [content-type content text]
  (when (= content-type :code-snippet)
    (let
      [aoc-boilerplate-url
       "https://gist.githubusercontent.com/borkdude/cf94b492d948f7f418aa81ba54f428ff/raw/3b58a80710fbbbda091966c8eb85323eef4652c1/aoc_ui.cljs"
       code-with-helpers (str aoc-helper-comment content)
       squint-url (compose-playground-url code-with-helpers
                                          {:playground :squint
                                           :boilerplate aoc-boilerplate-url
                                           :repl true})
       cherry-url (compose-playground-url code-with-helpers
                                          {:playground :cherry
                                           :boilerplate aoc-boilerplate-url
                                           :repl true})]
      [dropdown-menu/dropdown-menu {}
       [dropdown-menu/dropdown-menu-trigger {:as-child true}
        (button/button {:variant :ghost
                        :size :xs
                        :class "gap-1"}
                       [:> ExternalLink {:class "size-3"}]
                       [:span {:class "text-xs"}
                        (or (:open-interactively text) "Open interactively")]
                       [:> ChevronDown {:class "size-3 ml-1"}])]
       [dropdown-menu/dropdown-menu-content {:align "end"}
        [dropdown-menu/dropdown-menu-item {:on-select #(js/window.open squint-url "_blank")
                                           :class "cursor-pointer gap-2"}
         [:> ExternalLink {:class "size-4"}]
         [:span "Squint"]]
        [dropdown-menu/dropdown-menu-item {:on-select #(js/window.open cherry-url "_blank")
                                           :class "cursor-pointer gap-2"}
         [:> ExternalLink {:class "size-4"}]
         [:span "Cherry"]]]])))

;; Backward compatibility alias
(def open-in-squint-button open-in-playground-menu)

(defn share-button
  "Button to share/copy link to a specific solution."
  [solution-id text]
  (let [copied? (r/atom false)]
    (fn [solution-id text] [button/button {:variant :ghost
                                           :size :xs
                                           :on-click (copy-solution-link! solution-id copied?)
                                           :class "gap-1"}
                            (if @copied? [:> Check {:class "size-3"}] [:> Link2 {:class "size-3"}])
                            [:span {:class "text-xs"}
                             (if @copied? (:copied text) (:share text))]])))

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

(def ^:private long-code-threshold
  "Line count threshold to consider code as long and collapsible."
  20)

(defn- is-long-code?
  "Check if content is long enough to warrant collapsing."
  [content]
  (when content (let [lines (str/split-lines content)] (> (count lines) long-code-threshold))))

(defn solution-info
  "Display solution content based on type with expand/collapse for long code.
  
  Props:
  - :content-type (:code-snippet | :repo-link) - Type of content
  - :content - The actual content (code or URL)
  - :theme - Current theme (:light | :dark)
  - :text - Map of translated text strings"
  [{:keys [content-type content theme text]
    :as _solution-card-data}]
  (let [expanded? (r/atom false)
        is-long-code? (and (= content-type :code-snippet) (is-long-code? content))
        is-long-url? (and (= content-type :repo-link) (> (count content) 100))]
    (fn [{:keys [content-type content theme text]}]
      [:div {:class "mt-4"}
       (cond
         (= content-type :code-snippet)
         [:div {:class "relative"}
          [:div {:class (when (and is-long-code? (not @expanded?))
                          "max-h-64 overflow-hidden relative")}
           [code-block/copy-block {:text content
                                   :language "clojure"
                                   :theme theme}]
           (when (and is-long-code? (not @expanded?))
             [:div
              {:class
               "absolute bottom-0 left-0 right-0 h-24 bg-gradient-to-t from-card to-transparent pointer-events-none"}])]
          (when is-long-code?
            [:div {:class "mt-2 flex justify-center"}
             [button/button {:variant :ghost
                             :size :sm
                             :on-click #(swap! expanded? not)
                             :class "gap-1"}
              (if @expanded?
                [:<> [:> ChevronUp {:class "size-4"}] [:span (or (:show-less text) "Show less")]]
                [:<>
                 [:> ChevronDown {:class "size-4"}]
                 [:span (or (:show-more text) "Show more")]])]])]
         (= content-type :repo-link)
         [:div {:class "relative"}
          [:div {:class (when (and is-long-url? (not @expanded?))
                          "max-h-20 overflow-hidden relative")}
           [:a {:href content
                :target "_blank"
                :rel "noopener noreferrer"
                :class "inline-flex items-start gap-2 text-primary hover:underline"}
            [:svg {:class "size-5 flex-shrink-0 mt-0.5"
                   :xmlns "http://www.w3.org/2000/svg"
                   :view-box "0 0 24 24"
                   :fill "none"
                   :stroke "currentColor"
                   :stroke-width "2"}
             [:path {:d "M10 13a5 5 0 0 0 7.54.54l3-3a5 5 0 0 0-7.07-7.07l-1.72 1.71"}]
             [:path {:d "M14 11a5 5 0 0 0-7.54-.54l-3 3a5 5 0 0 0 7.07 7.07l1.71-1.71"}]]
            [:span {:class "break-all"}
             (:view-repository text)
             " ("
             content
             ")"]]
           (when (and is-long-url? (not @expanded?))
             [:div
              {:class
               "absolute bottom-0 left-0 right-0 h-12 bg-gradient-to-t from-card to-transparent pointer-events-none"}])]
          (when is-long-url?
            [:div {:class "mt-2 flex justify-center"}
             [button/button {:variant :ghost
                             :size :sm
                             :on-click #(swap! expanded? not)
                             :class "gap-1"}
              (if @expanded?
                [:<> [:> ChevronUp {:class "size-4"}] [:span (or (:show-less text) "Show less")]]
                [:<>
                 [:> ChevronDown {:class "size-4"}]
                 [:span (or (:show-more text) "Show more")]])]])]
         :else [:p (:unknown-content-type text)])])))

(defn vote-buttons
  "Display vote buttons and share button for solution.
  
  Props:
  - :best-practices-count - Number of best practices votes
  - :clever-count - Number of clever votes
  - :voted-best-practices? - Whether user has voted for best practices
  - :voted-clever? - Whether user has voted for clever
  - :on-vote-best-practices - Handler for best practices vote
  - :on-vote-clever - Handler for clever vote
  - :solution-id - ID of the solution for share link
  - :content-type - Type of content (:code-snippet or :repo-link)
  - :content - The actual content (code or URL)
  - :text - Map of translated text strings"
  [{:keys [best-practices-count
           clever-count
           voted-best-practices?
           voted-clever?
           on-vote-best-practices
           on-vote-clever
           solution-id
           content-type
           content
           text]
    :as _vote-data}]
  [:div {:class "flex justify-between items-center mt-4"}
   [:div {:class "flex gap-2"}
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
     (str (:clever text) " " (or clever-count 0))]]
   [:div {:class "flex gap-2"}
    [open-in-squint-button content-type content text]
    [share-button solution-id text]]])

(defn solution-card
  "Display a single solution card.
   
   Props:
   - solution-card-data - Map containing solution data including theme, text, and handlers
   - is-user-solution? - Whether this is the current user's solution
   - is-highlighted? - Whether this solution is currently highlighted
   - admin-logged-in? - Whether admin is logged in
   - on-delete-solution - Delete handler function (admin only)"
  [solution-card-data is-user-solution? is-highlighted? admin-logged-in? on-delete-solution]
  [:div {:id (str "solution-" (:id solution-card-data))
         :class (str "border rounded-lg p-6 shadow-sm hover:shadow-md transition-all scroll-mt-4 "
                     "target:ring-4 target:ring-primary/30 target:border-primary target:shadow-lg "
                     (when is-highlighted? "ring-4 ring-primary/30 border-primary shadow-lg ")
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
   [vote-buttons
    (assoc solution-card-data
           :solution-id (:id solution-card-data)
           :content-type (:content-type solution-card-data)
           :content (:content solution-card-data))]])

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



(defn upload-modal
  "Modal for uploading a solution.
   
   Two modes:
   1. External mode (has playground-url): Content type locked to :repo-link, URL field pre-filled and locked
   2. Normal mode (no playground-url): All fields editable"
  [{:keys [modal-open?
           form
           submitting?
           text
           form-errors
           handlers
           playground-url
           years-options
           challenges-options]
    :as _upload-modal-data}]
  (let [{:keys [on-close-modal
                on-update-form
                on-submit-solution
                on-select-year
                on-select-challenge]}
        handlers
        external-mode? (some? playground-url)
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
       ;; Always show year/challenge selectors
       [:div {:class "mb-4 pb-4"}
        [:p {:class "text-sm font-medium mb-3"}
         (:uploading-for text)]
        [:div {:class "grid grid-cols-2 gap-4"}
         [year-selector {:selected-year (:year form)
                         :years-options years-options
                         :on-select-year on-select-year
                         :text text}]
         [challenge-selector {:selected-challenge (:challenge form)
                              :challenges-options challenges-options
                              :on-select-challenge on-select-challenge
                              :text text}]]]
       [input-author form-data]
       [input-gh form-data]
       ;; Content type selector - locked to :repo-link in external mode
       [:div {:class "space-y-2"}
        [label/label {}
         (:content-type text)]
        [:div {:class "flex gap-4"}
         [:label {:class (str "flex items-center gap-2 "
                              (if external-mode? "opacity-50 cursor-not-allowed" "cursor-pointer"))}
          [:input {:type "radio"
                   :name "content-type"
                   :checked (= (:content-type form) :code-snippet)
                   :disabled (or submitting? external-mode?)
                   :on-change #(on-update-form :content-type :code-snippet)}]
          [:span (:code-snippet text)]]
         [:label {:class (str "flex items-center gap-2 "
                              (if external-mode? "opacity-50 cursor-not-allowed" "cursor-pointer"))}
          [:input {:type "radio"
                   :name "content-type"
                   :checked (= (:content-type form) :repo-link)
                   :disabled (or submitting? external-mode?)
                   :on-change #(on-update-form :content-type :repo-link)}]
          [:span (:repository-link text)]]]]
       ;; Content input - pre-filled and locked in external mode
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
                                :disabled (or submitting? external-mode?)
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
                          :disabled (or submitting? external-mode?)
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
           handlers]}]
  (let [{:keys [on-select-year on-select-challenge]} handlers]
    [:<>
     [year-selector {:selected-year selected-year
                     :years-options years-options
                     :on-select-year on-select-year
                     :text text}]
     [challenge-selector {:selected-challenge selected-challenge
                          :challenges-options challenges-options
                          :on-select-challenge on-select-challenge
                          :text text}]]))

(defn upload-solution
  [{:keys [upload-count text handlers]}]
  (let [{:keys [on-open-modal on-upload-limit-reached]} handlers
        upload-count (or upload-count 0)
        can-upload? (< upload-count 5)
        handle-click (if can-upload? on-open-modal on-upload-limit-reached)]
    [:div {:class "ml-auto flex flex-col items-end gap-2"}
     [button/button {:on-click handle-click
                     :size :lg}
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
           user-solution-ids
           highlighted-solution-id
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
       ;; Don't show unlock block if viewing a specific solution (highlighted-solution-id is set)
       (and gated? (seq solutions) (nil? highlighted-solution-id))
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
                            (on-give-consent selected-year selected-challenge))
                         :size :lg}
          (:show-me-solutions text)]]]
       (empty? solutions) [:div {:class "text-center py-12 bg-card rounded-lg border"}
                           [:p {:class "text-muted-foreground text-lg"}
                            (:no-solutions-yet text)]
                           [:p {:class "text-muted-foreground mt-2"}
                            (:be-first-to-share text)]]
       :else (let [sorted-solutions (sort-by (fn [solution]
                                               (if (contains? user-solution-ids (:id solution))
                                                 0 ;; User solutions first
                                                 1)) ;; Others after
                                             solutions)]
               (for [solution sorted-solutions]
                 (let [is-user-solution? (contains? user-solution-ids (:id solution))
                       is-highlighted? (= highlighted-solution-id (:id solution))]
                   ^{:key (:id solution)}
                   [solution-card
                    solution
                    is-user-solution?
                    is-highlighted?
                    admin-logged-in?
                    on-delete-solution]))))]))

(defn aoc-page
  "Main Advent of Code page.
  
  Pure presentation component that receives structured data matching component hierarchy.
  
  Props (nested structure):
  - :header-data - Page title and description with text
  - :selector-data - Year/challenge filters with options, handlers, and text
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
            :selected-challenge (:selected-challenge selector-data))]]
   [upload-modal modal-data]])
