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
            :source-code (embed-source mateuszmazurczak.ui.components.radio_group)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/radio_group.cljs"
            :filename "radio_group.cljs"}])

(defscene
 api-reference
 "Complete reference for all Radio Group component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Radio Group components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card {:component-name "radio-group"
                                             :description "Radio group component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "radio-group-item"
                                             :description "Radio group item component"
                                             :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[radio-group {}]"]]]]]]))

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