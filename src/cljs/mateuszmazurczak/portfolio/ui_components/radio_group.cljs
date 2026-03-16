(ns mateuszmazurczak.portfolio.ui-components.radio-group
  (:require
   [mateuszmazurczak.portfolio.utils           :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.field       :as field]
   [mateuszmazurczak.ui.components.label       :as label]
   [mateuszmazurczak.ui.components.radio-group :as sut]
   [portfolio.reagent-18                       :refer-macros [defscene configure-scenes]]
   [reagent.core                               :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Radio Group"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Radio Group component built on Radix UI primitives."
            :npm-install "npm install @radix-ui/react-radio-group lucide-react"
            :source-code (embed-source "mateuszmazurczak.ui.components.radio_group")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/radio_group.cljs"
            :filename "radio_group.cljs"}])

(defscene api-reference
          "Complete reference for all Radio Group component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-6"}
             [:div
              [:p {:class "text-sm text-muted-foreground"}
               "All available props for Radio Group components."]]
             [:div {:class "space-y-4"}
              [mm-portfolio-utils/api-component-card
               {:component-name "radio-group"
                :description "Container for mutually-exclusive options. Handles keyboard navigation and selected value management. Additional props are forwarded to Radix RadioGroup.Root."
                :props [[":value" "string, optional - Controlled selected value."]
                        [":default-value" "string, optional - Uncontrolled initial selected value."]
                        [":on-value-change" "function, optional - Callback when selected value changes: (fn [value] ...)."]
                        [":disabled" "boolean, optional - Disables all items in the group."]
                        [":required" "boolean, optional - Marks group as required for forms."]
                        [":name" "string, optional - Form field name."]
                        [":orientation" "keyword, optional (default :vertical) - :vertical or :horizontal."]
                        [":class" "string, optional - Additional Tailwind classes."]
                        ["additional props" "map entries, optional - Forwarded to Radix RadioGroup.Root."]]}]
              [mm-portfolio-utils/api-component-card
               {:component-name "radio-group-item"
                :description "Single selectable option inside radio-group."
                :props [[":value" "string, required - Value represented by this option."]
                        [":id" "string, optional - ID for associated label :html-for."]
                        [":disabled" "boolean, optional - Disables this option."]
                        [":class" "string, optional - Additional Tailwind classes."]
                        ["additional props" "map entries, optional - Forwarded to Radix RadioGroup.Item."]]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "Each radio-group-item must have a unique :value; without it, selection logic cannot work."]
                [:li "Use matching item :id + label :html-for for accessible click targets."]
                [:li "Prefer a single radio-group per decision domain; avoid nesting groups with same :name."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "(let [billing (r/atom \"monthly\")]\n  [radio-group {:value @billing\n                :on-value-change #(reset! billing %)\n                :name \"billing-cycle\"}\n   [:div {:class \"flex items-center gap-2\"}\n    [radio-group-item {:id \"bill-monthly\" :value \"monthly\"}]\n    [label {:html-for \"bill-monthly\"} \"Monthly\"]]\n   [:div {:class \"flex items-center gap-2\"}\n    [radio-group-item {:id \"bill-yearly\" :value \"yearly\"}]\n    [label {:html-for \"bill-yearly\"} \"Yearly\"]]])"]]
               [:div {:class "flex flex-wrap gap-2 mt-3"}
                [:a {:href "https://www.radix-ui.com/primitives/docs/components/radio-group"
                     :target "_blank"
                     :rel "noopener noreferrer"
                     :class "inline-flex items-center text-sm text-primary hover:underline"}
                 "Radix Radio Group Docs →"]]]]]]))

(defscene
 radio-group-demo
 "Radio group with labeled options.

  Based on shadcn/ui Radio Group — https://ui.shadcn.com/docs/components/radio-group
  Radix primitive: @radix-ui/react-radio-group

  Use for exclusive choices like density or layout."
 []
 (let [value (r/atom "comfortable")]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6"}
       [sut/radio-group {:value @value
                         :on-value-change #(reset! value %)}
        [:div {:class "flex items-center gap-3"}
         [sut/radio-group-item {:value "default"
                                :id "density-default"}]
         [label/label {:html-for "density-default"}
          "Default"]]
        [:div {:class "flex items-center gap-3"}
         [sut/radio-group-item {:value "comfortable"
                                :id "density-comfortable"}]
         [label/label {:html-for "density-comfortable"}
          "Comfortable"]]
        [:div {:class "flex items-center gap-3"}
         [sut/radio-group-item {:value "compact"
                                :id "density-compact"}]
         [label/label {:html-for "density-compact"}
          "Compact"]]]]))))

(defscene
 field-radio
 "Radio group embedded in Field layout.

  Based on shadcn/ui Field + Radio Group —
  https://ui.shadcn.com/docs/components/field
  Radix primitive: @radix-ui/react-radio-group

  Ideal for pricing or plan selection with supporting copy."
 []
 (let [value (r/atom "monthly")]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6 max-w-md"}
       [field/field-set {}
        [field/field-label {}
         "Subscription Plan"]
        [field/field-description {}
         "Yearly and lifetime plans offer significant savings."]
        [sut/radio-group {:value @value
                          :on-value-change #(reset! value %)}
         [field/field {:orientation :horizontal}
          [sut/radio-group-item {:value "monthly"
                                 :id "plan-monthly"}]
          [field/field-label {:html-for "plan-monthly"
                              :class "font-normal"}
           "Monthly ($9.99/month)"]]
         [field/field {:orientation :horizontal}
          [sut/radio-group-item {:value "yearly"
                                 :id "plan-yearly"}]
          [field/field-label {:html-for "plan-yearly"
                              :class "font-normal"}
           "Yearly ($99.99/year)"]]
         [field/field {:orientation :horizontal}
          [sut/radio-group-item {:value "lifetime"
                                 :id "plan-lifetime"}]
          [field/field-label {:html-for "plan-lifetime"
                              :class "font-normal"}
           "Lifetime ($299.99)"]]]]]))))

(defscene
 radio-group-disabled
 "Disabled radio items in a group.

  Custom example — not from shadcn/ui.
  Radix primitive: @radix-ui/react-radio-group

  Use disabled options for unavailable choices."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/radio-group {:default-value "standard"}
                                      [:div {:class "flex items-center gap-3"}
                                       [sut/radio-group-item {:value "standard"
                                                              :id "plan-standard"}]
                                       [label/label {:html-for "plan-standard"}
                                        "Standard"]]
                                      [:div {:class "flex items-center gap-3"}
                                       [sut/radio-group-item {:value "premium"
                                                              :id "plan-premium"
                                                              :disabled true}]
                                       [label/label {:html-for "plan-premium"
                                                     :class "text-muted-foreground"}
                                        "Premium (coming soon)"]]]]))

(defscene
 radio-group-invalid
 "Invalid radio group state with validation hint.

  Based on shadcn/ui Radio Group — https://ui.shadcn.com/docs/components/radio-group
  Radix primitive: @radix-ui/react-radio-group

  Apply :aria-invalid on items and show an explicit error message."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 space-y-2"}
                                     [sut/radio-group {:default-value nil}
                                      [:div {:class "flex items-center gap-3"}
                                       [sut/radio-group-item {:value "free"
                                                              :id "plan-free"
                                                              :aria-invalid true}]
                                       [label/label {:html-for "plan-free"}
                                        "Free"]]
                                      [:div {:class "flex items-center gap-3"}
                                       [sut/radio-group-item {:value "pro"
                                                              :id "plan-pro"
                                                              :aria-invalid true}]
                                       [label/label {:html-for "plan-pro"}
                                        "Pro"]]]
                                     [:p {:class "text-destructive text-sm"}
                                      "Please select a billing plan."]]))

(defscene
 radio-group-horizontal
 "Horizontal layout variant.

  Custom example — not from shadcn/ui.
  Radix primitive: @radix-ui/react-radio-group

  Use :orientation :horizontal for inline radio groups."
 []
 (let [value (r/atom "monthly")]
   (fn []
     (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                         [sut/radio-group {:value @value
                                                           :orientation :horizontal
                                                           :class "flex items-center gap-6"
                                                           :on-value-change #(reset! value %)}
                                          [:div {:class "flex items-center gap-2"}
                                           [sut/radio-group-item {:value "monthly"
                                                                  :id "cycle-monthly"}]
                                           [label/label {:html-for "cycle-monthly"}
                                            "Monthly"]]
                                          [:div {:class "flex items-center gap-2"}
                                           [sut/radio-group-item {:value "yearly"
                                                                  :id "cycle-yearly"}]
                                           [label/label {:html-for "cycle-yearly"}
                                            "Yearly"]]]]))))