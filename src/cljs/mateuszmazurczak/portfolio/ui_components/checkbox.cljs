(ns mateuszmazurczak.portfolio.ui-components.checkbox
  (:require
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.checkbox :as sut]
   [mateuszmazurczak.ui.components.field    :as field]
   [mateuszmazurczak.ui.components.label    :as label]
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
            :source-code (embed-source mateuszmazurczak.ui.components.checkbox)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/checkbox.cljs"
            :filename "checkbox.cljs"}])

(defscene api-reference
          "Complete reference for all Checkbox component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-4xl"}
                                              [:div {:class "space-y-6"}
                                               [:div
                                                [:p {:class "text-sm text-muted-foreground"}
                                                 "All available props for Checkbox components."]]
                                               [:div {:class "space-y-4"}
                                                [mm-portfolio-utils/api-component-card
                                                 {:component-name "checkbox"
                                                  :description "Checkbox component"
                                                  :props [[":class"
                                                           "any, optional - Component prop"]]}]
                                                [:div {:class "border rounded-lg p-4 bg-muted/50"}
                                                 [:h4 {:class "text-sm font-semibold mb-2"}
                                                  "Usage Example"]
                                                 [:pre {:class "text-xs overflow-x-auto"}
                                                  [:code "[checkbox {}]"]]]]]]))

(defscene
 checkbox-demo
 "Checkbox with label and descriptive text.

  Based on shadcn/ui Checkbox — https://ui.shadcn.com/docs/components/checkbox
  Radix primitive: @radix-ui/react-checkbox

  Use labels and helper text for clarity."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 space-y-4"}
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
      "By clicking this checkbox, you agree to the terms and conditions."]]]]))

(defscene
 checkbox-disabled
 "Disabled checkbox state.

  Based on shadcn/ui Checkbox — https://ui.shadcn.com/docs/components/checkbox
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

  Based on shadcn/ui Checkbox — https://ui.shadcn.com/docs/components/checkbox
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

  Based on shadcn/ui Field + Checkbox —
  https://ui.shadcn.com/docs/components/field
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

  Custom example — not from shadcn/ui.
  Radix primitive: @radix-ui/react-checkbox

  Use :checked \"indeterminate\" for partial selection states."
 []
 (let [checked (r/atom "indeterminate")]
   (fn []
     (mm-portfolio-utils/wrap-component [:div {:class "p-6 space-y-2"}
                                         [:div {:class "flex items-center gap-2"}
                                          [sut/checkbox {:checked @checked
                                                         :on-checked-change #(reset! checked %)}]
                                          [:span {:class "text-sm"}
                                           "Select all projects"]]
                                         [:p {:class "text-muted-foreground text-sm"}
                                          (str "Current state: " @checked)]]))))