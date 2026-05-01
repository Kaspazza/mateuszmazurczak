(ns mateuszmazurczak.portfolio.ui-components.checkbox
  (:require
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.checkbox :as sut]
   [mateuszmazurczak.ui.components.field    :as field]
   [mateuszmazurczak.ui.components.label    :as label]
   [mateuszmazurczak.ui.components.table    :as table]
   [portfolio.reagent-18                    :refer-macros [defscene configure-scenes]]
   [reagent.core                            :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Checkbox"})

(defscene installation
  "Install dependencies and copy the component code into your project."
  []
  [mm-portfolio-utils/installation-scene
   {:description "Checkbox component with built-in check indicator."
    :npm-install "npm install @radix-ui/react-checkbox lucide-react"
    :source-code (embed-source "mateuszmazurczak.ui.components.checkbox")
    :namespace-path "src/cljs/mateuszmazurczak/ui/components/checkbox.cljs"
    :filename "checkbox.cljs"}])

(defscene api-reference
  []
  (mm-portfolio-utils/wrap-component
   [:div {:class "p-6 max-w-4xl"}
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card
      {:component-name "checkbox"
       :link {:href "https://www.radix-ui.com/primitives/docs/components/checkbox"
              :label "Radix Checkbox Docs"}
       :description "Checkbox component built on top of Radix UI checkbox. Supports controlled and uncontrolled modes, indeterminate state, and aria-invalid styling for form validation."
       :props [{:name ":checked"           :type "boolean | :indeterminate | \"indeterminate\"" :default nil :description "Controlled checked state. Use true/false for checked/unchecked, or :indeterminate keyword (string also accepted) for a partial selection state."}
               {:name ":default-checked"   :type "boolean"                                       :default nil :description "Uncontrolled default checked state. Use when you don't need to control the state externally."}
               {:name ":on-checked-change" :type "function"                                      :default nil :description "Callback when checked state changes: (fn [checked?] ...). Receives true, false, or :indeterminate."}
               {:name ":disabled"          :type "boolean"                                       :default nil :description "Disables the checkbox. Renders with reduced opacity and cursor-not-allowed."}
               {:name ":required"          :type "boolean"                                       :default nil :description "Marks the checkbox as required for form validation."}
               {:name ":name"             :type "string"                                        :default nil :description "Form field name. Used when checkbox is inside a <form> for native form submission."}
               {:name ":value"            :type "string"                                        :default nil :description "Form field value submitted with the name when checked."}
               {:name ":aria-invalid"     :type "boolean"                                       :default nil :description "Marks the checkbox as invalid. Applies destructive ring and border styling for form validation errors."}
               {:name ":class"            :type "string"                                        :default nil :description "Additional Tailwind classes merged with default styling."}]}]
     [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
      [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
      [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
       [:li "The props map {} is required even when empty."]
       [:li "For controlled usage, always pair :checked with :on-checked-change to avoid a read-only checkbox."]
       [:li "Use :indeterminate keyword for the indeterminate state. The component handles Radix JS interop in both directions — callbacks also return :indeterminate, never the raw string."]
       [:li "Use :aria-invalid true to show error styling — pair with Field and field-error for full validation UX."]]]]]))

(defscene
  checkbox-demo
  "Checkbox variants: uncontrolled and controlled usage.

  Radix primitive: @radix-ui/react-checkbox

  Use labels and helper text for clarity. Always pair :checked with :on-checked-change for controlled usage."
  []
  (let [checked? (r/atom false)]
    (fn []
      (mm-portfolio-utils/wrap-component
       [:div {:class "p-6 space-y-6"}
        [:div {:class "space-y-4"}
         [:p {:class "text-xs font-semibold uppercase tracking-wide text-muted-foreground"} "Uncontrolled"]
         [:div {:class "flex items-center gap-2"}
          [sut/checkbox {:id "terms"}]
          [label/label {:html-for "terms"}
           "Accept terms and conditions"]]
         [:div {:class "flex items-start gap-3"}
          [sut/checkbox {:id "terms-2"
                         :default-checked true}]
          [:div {:class "grid gap-2"}
           [label/label {:html-for "terms-2"}
            "Accept terms and conditions"]
           [:p {:class "text-muted-foreground text-sm"}
            "By clicking this checkbox, you agree to the terms and conditions."]]]]
        [:div {:class "space-y-4"}
         [:p {:class "text-xs font-semibold uppercase tracking-wide text-muted-foreground"} "Controlled"]
         [:div {:class "flex items-center gap-2"}
          [sut/checkbox {:id "controlled"
                         :checked @checked?
                         :on-checked-change #(reset! checked? %)}]
          [label/label {:html-for "controlled"}
           "Enable feature"]]
         [:p {:class "text-sm text-muted-foreground"}
          (str "State: " (if @checked? "checked" "unchecked"))]]]))))

(defscene
  checkbox-with-field-label
  "Checkbox inside Field wrapped by FieldLabel with title and description.

  Radix primitive: @radix-ui/react-checkbox

  Wrapping a horizontal Field inside field-label makes the entire row clickable.
  Use field-title and field-description for structured labelling."
  []
  (mm-portfolio-utils/wrap-component
   [:div {:class "p-6"}
    [field/field-label {}
     [field/field {:orientation :horizontal}
      [sut/checkbox {:id "toggle-checkbox-2"
                     :name "toggle-checkbox-2"}]
      [field/field-content {}
       [field/field-title {} "Enable notifications"]
       [field/field-description {}
        "You can enable or disable notifications at any time."]]]]]))

(defscene
  checkbox-disabled
  "Disabled checkbox state.

  Radix primitive: @radix-ui/react-checkbox

  Disabled checkboxes are muted and non-interactive."
  []
  (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                      [:div {:class "flex items-center gap-2"}
                                       [sut/checkbox {:id "notifications"
                                                      :disabled true}]
                                       [:span {:class "text-sm text-muted-foreground"}
                                        "Enable notifications"]]]))

(defscene
  checkbox-with-text
  "Checkbox with supporting text.

  Radix primitive: @radix-ui/react-checkbox

  Use descriptive text for legal agreements or onboarding flows."
  []
  (mm-portfolio-utils/wrap-component
   [:div {:class "p-6"}
    [:div {:class "flex items-start gap-2"}
     [sut/checkbox {:id "terms-3"}]
     [:div {:class "grid gap-1.5 leading-none"}
      [label/label {:html-for "terms-3"}
       "Accept terms and conditions"]
      [:p {:class "text-muted-foreground text-sm"}
       "You agree to our Terms of Service and Privacy Policy."]]]]))

(defscene
  field-checkbox
  "Checkboxes inside Field layout.

  Radix primitive: @radix-ui/react-checkbox

  The Field components provide structured form layouts."
  []
  (mm-portfolio-utils/wrap-component
   [:div {:class "p-6 max-w-md"}
    [field/field-group {}
     [field/field-set {}
      [field/field-legend {:variant :label}
       "Show these items on the desktop"]
      [field/field-description {}
       "Select the items you want to show on the desktop."]
      [field/field-group {:class "gap-3"}
       [field/field {:orientation :horizontal}
        [sut/checkbox {:id "hard-disks"}]
        [field/field-label {:html-for "hard-disks"
                            :class "font-normal"}
         "Hard disks"]]
       [field/field {:orientation :horizontal}
        [sut/checkbox {:id "external-disks"}]
        [field/field-label {:html-for "external-disks"
                            :class "font-normal"}
         "External disks"]]
       [field/field {:orientation :horizontal}
        [sut/checkbox {:id "cds"}]
        [field/field-label {:html-for "cds"
                            :class "font-normal"}
         "CDs, DVDs, and iPods"]]
       [field/field {:orientation :horizontal}
        [sut/checkbox {:id "servers"}]
        [field/field-label {:html-for "servers"
                            :class "font-normal"}
         "Connected servers"]]]]
     [field/field-separator {}]
     [field/field {:orientation :horizontal}
      [sut/checkbox {:id "icloud"
                     :default-checked true}]
      [field/field-content {}
       [field/field-label {:html-for "icloud"}
        "Sync Desktop & Documents folders"]
       [field/field-description {}
        "Your Desktop & Documents folders are being synced with iCloud Drive."]]]]]))

(defscene
  checkbox-indeterminate
  "Indeterminate checkbox for partial selections.

  Radix primitive: @radix-ui/react-checkbox

  Use :checked :indeterminate for partial selection states."
  []
  (let [checked (r/atom :indeterminate)]
    (fn []
      (mm-portfolio-utils/wrap-component [:div {:class "p-6 space-y-2"}
                                          [:div {:class "flex items-center gap-2"}
                                           [sut/checkbox {:checked @checked
                                                          :on-checked-change #(reset! checked %)}]
                                           [:span {:class "text-sm"}
                                            "Select all projects"]]
                                          [:p {:class "text-muted-foreground text-sm"}
                                           (str "Current state: " @checked)]]))))

(defscene
  checkbox-invalid
  "Checkbox with invalid/error state for form validation.

  Radix primitive: @radix-ui/react-checkbox

  Use :aria-invalid true to show destructive ring and border styling.
  Pair with Field and field-error for a complete validation UX."
  []
  (mm-portfolio-utils/wrap-component
   [:div {:class "p-6 space-y-6"}
    [:div {:class "space-y-3"}
     [:p {:class "text-sm font-medium"} "Standalone invalid checkbox"]
     [:div {:class "flex items-center gap-2"}
      [sut/checkbox {:id "invalid-standalone"
                     :aria-invalid true}]
      [label/label {:html-for "invalid-standalone"}
       "I accept the terms and conditions"]]]
    [:div {:class "space-y-3"}
     [:p {:class "text-sm font-medium"} "Invalid checkbox inside Field with error messages"]
     [field/field {}
      [field/field-content {}
       [:div {:class "flex items-center gap-2"}
        [sut/checkbox {:id "invalid-field"
                       :aria-invalid true}]
        [field/field-label {:html-for "invalid-field"}
         "I accept the terms and conditions"]]
       [field/field-error {:errors [{:message "You must accept the terms to continue"}]}]]]]
    [:div {:class "space-y-3"}
     [:p {:class "text-sm font-medium"} "Comparison: valid vs invalid"]
     [:div {:class "flex items-start gap-6"}
      [:div {:class "flex items-center gap-2"}
       [sut/checkbox {:id "valid-example"
                      :default-checked true}]
       [label/label {:html-for "valid-example"}
        "Valid"]]
      [:div {:class "flex items-center gap-2"}
       [sut/checkbox {:id "invalid-example"
                      :aria-invalid true}]
       [label/label {:html-for "invalid-example"}
        "Invalid"]]]]]))

(defscene
  checkbox-form-integration
  "Checkbox used inside a native HTML form with :name, :value, and :required.

  Radix primitive: @radix-ui/react-checkbox

  Use :name and :value for native form submission. Use :required to enforce the field before submit."
  []
  (mm-portfolio-utils/wrap-component
   [:div {:class "p-6 max-w-sm"}
    [:form {:on-submit (fn [e]
                         (.preventDefault e)
                         (let [data (js/FormData. (.-target e))]
                           (js/alert (str "terms: " (.get data "terms")))))}
     [:div {:class "space-y-4"}
      [:div {:class "flex items-start gap-2"}
       [sut/checkbox {:id "terms-form"
                      :name "terms"
                      :value "accepted"
                      :required true}]
       [:div {:class "grid gap-1.5 leading-none"}
        [label/label {:html-for "terms-form"}
         "Accept terms and conditions"]
        [:p {:class "text-muted-foreground text-sm"}
         "Required to continue. Value submitted: \"accepted\"."]]]
      [:button {:type "submit"
                :class "mt-4 rounded-md bg-primary px-4 py-2 text-sm text-primary-foreground hover:bg-primary/90"}
       "Submit form"]]]]))

(defscene
  checkbox-table
  "Checkboxes inside a data table for row selection.

  Combines: Checkbox + Table components

  Common pattern for bulk actions in admin dashboards and data management UIs."
  []
  (let [all-tasks [{:id "TASK-001" :title "Update documentation" :status "Done" :priority "Low"}
                   {:id "TASK-002" :title "Fix login redirect" :status "In Progress" :priority "High"}
                   {:id "TASK-003" :title "Add dark mode" :status "Todo" :priority "Medium"}
                   {:id "TASK-004" :title "Optimize bundle size" :status "In Progress" :priority "High"}
                   {:id "TASK-005" :title "Write unit tests" :status "Todo" :priority "Medium"}]
        selected (r/atom #{})]
    (fn []
      (let [all-ids (set (map :id all-tasks))
            all-selected? (= @selected all-ids)
            some-selected? (and (seq @selected) (not all-selected?))
            toggle-all! (fn [checked?]
                          (reset! selected (if checked? all-ids #{})))
            toggle-row! (fn [id checked?]
                          (swap! selected (if checked? conj disj) id))]
        (mm-portfolio-utils/wrap-component
         [:div {:class "p-6"}
          [:div {:class "mb-3 text-sm text-muted-foreground"}
           (str (count @selected) " of " (count all-tasks) " row(s) selected.")]
          [table/table {}
           [table/table-header {}
            [table/table-row {}
             [table/table-head {:class "w-[50px]"}
              [sut/checkbox {:checked (if some-selected? :indeterminate all-selected?)
                             :on-checked-change toggle-all!
                             :aria-label "Select all"}]]
             [table/table-head {} "Task"]
             [table/table-head {} "Title"]
             [table/table-head {} "Status"]
             [table/table-head {:class "text-right"} "Priority"]]]
           [table/table-body {}
            (for [{:keys [id title status priority]} all-tasks]
              ^{:key id}
              [table/table-row {:class (when (contains? @selected id)
                                         "bg-muted/50")}
               [table/table-cell {}
                [sut/checkbox {:checked (contains? @selected id)
                               :on-checked-change (partial toggle-row! id)
                               :aria-label (str "Select " id)}]]
               [table/table-cell {:class "font-medium"} id]
               [table/table-cell {} title]
               [table/table-cell {} status]
               [table/table-cell {:class "text-right"} priority]])]]])))))
