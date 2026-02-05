(ns mateuszmazurczak.portfolio.ui-components.radio-group
  (:require
   [mateuszmazurczak.portfolio.utils           :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.field       :as field]
   [mateuszmazurczak.ui.components.label       :as label]
   [mateuszmazurczak.ui.components.radio-group :as sut]
   [portfolio.reagent-18                       :refer-macros [defscene configure-scenes]]
   [reagent.core                               :as r]))

(configure-scenes {:collection :ui-components
                   :title "Radio Group"})

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