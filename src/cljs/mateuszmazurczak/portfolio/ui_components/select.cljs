(ns mateuszmazurczak.portfolio.ui-components.select
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.field  :as field]
   [mateuszmazurczak.ui.components.select :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]]
   [reagent.core                          :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Select"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "https://www.radix-ui.com/primitives/docs/components/select."
            :npm-install "npm install @radix-ui/react-select"
            :source-code (embed-source mateuszmazurczak.ui.components.select)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/select.cljs"
            :filename "select.cljs"}])

(defscene
 api-reference
 "Complete reference for all Select component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Select components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card {:component-name "select"
                                             :description "Select component"
                                             :props []}]
     [mm-portfolio-utils/api-component-card {:component-name "select-group"
                                             :description "Select group component"
                                             :props []}]
     [mm-portfolio-utils/api-component-card {:component-name "select-value"
                                             :description "Select value component"
                                             :props []}]
     [mm-portfolio-utils/api-component-card
      {:component-name "select-trigger"
       :description "Select trigger component"
       :props [[":class" "string, optional - Additional Tailwind classes"]
               [":size" "string, optional (default 'default'). One of: 'default' | 'sm'"]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "select-content"
       :description "Select content component"
       :props [[":class" "string, optional - Additional Tailwind classes"]
               [":position" "string, optional (default 'popper') - Popper strategy value"]
               [":align"
                "string, optional (default 'center'). One of: 'start' | 'center' | 'end'"]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "select-label"
       :description "Select label component"
       :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "select-item"
       :description "Select item component"
       :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "select-separator"
       :description "Select separator component"
       :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[select {}]"]]]]]]))

(defscene
 select-demo
 "Basic select with grouped items.

  Based on shadcn/ui Select — https://ui.shadcn.com/docs/components/select
  Radix primitive: @radix-ui/react-select

  Our wrapper exposes select, trigger, content, and items as components."
 []
 (let [value (r/atom "banana")]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6"}
       [sut/select {:value @value
                    :on-value-change #(reset! value %)}
        [sut/select-trigger {:class "w-[180px]"}
         [sut/select-value {:placeholder "Select a fruit"}]]
        [sut/select-content {}
         [sut/select-group {}
          [sut/select-label {}
           "Fruits"]
          [sut/select-item {:value "apple"}
           "Apple"]
          [sut/select-item {:value "banana"}
           "Banana"]
          [sut/select-item {:value "blueberry"}
           "Blueberry"]
          [sut/select-item {:value "grapes"}
           "Grapes"]
          [sut/select-item {:value "pineapple"}
           "Pineapple"]]]]]))))

(defscene
 select-scrollable
 "Scrollable select content with multiple groups.

  Based on shadcn/ui Select — https://ui.shadcn.com/docs/components/select
  Radix primitive: @radix-ui/react-select

  Long lists automatically scroll within the content panel."
 []
 (let [value (r/atom "cet")]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6"}
       [sut/select {:value @value
                    :on-value-change #(reset! value %)}
        [sut/select-trigger {:class "w-[280px]"}
         [sut/select-value {:placeholder "Select a timezone"}]]
        [sut/select-content {}
         [sut/select-group {}
          [sut/select-label {}
           "North America"]
          [sut/select-item {:value "est"}
           "Eastern Standard Time (EST)"]
          [sut/select-item {:value "cst"}
           "Central Standard Time (CST)"]
          [sut/select-item {:value "mst"}
           "Mountain Standard Time (MST)"]
          [sut/select-item {:value "pst"}
           "Pacific Standard Time (PST)"]
          [sut/select-item {:value "akst"}
           "Alaska Standard Time (AKST)"]
          [sut/select-item {:value "hst"}
           "Hawaii Standard Time (HST)"]]
         [sut/select-group {}
          [sut/select-label {}
           "Europe & Africa"]
          [sut/select-item {:value "gmt"}
           "Greenwich Mean Time (GMT)"]
          [sut/select-item {:value "cet"}
           "Central European Time (CET)"]
          [sut/select-item {:value "eet"}
           "Eastern European Time (EET)"]
          [sut/select-item {:value "west"}
           "Western European Summer Time (WEST)"]
          [sut/select-item {:value "cat"}
           "Central Africa Time (CAT)"]
          [sut/select-item {:value "eat"}
           "East Africa Time (EAT)"]]]]]))))

(defscene
 field-select
 "Select inside Field layout with helper copy.

  Based on shadcn/ui Field + Select —
  https://ui.shadcn.com/docs/components/field
  Radix primitive: @radix-ui/react-select

  Useful for richer forms with descriptions."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-md"}
   [field/field {}
    [field/field-label {}
     "Department"]
    [sut/select {:default-value "design"}
     [sut/select-trigger {}
      [sut/select-value {:placeholder "Choose department"}]]
     [sut/select-content {}
      [sut/select-item {:value "engineering"}
       "Engineering"]
      [sut/select-item {:value "design"}
       "Design"]
      [sut/select-item {:value "marketing"}
       "Marketing"]
      [sut/select-item {:value "sales"}
       "Sales"]
      [sut/select-item {:value "support"}
       "Customer Support"]
      [sut/select-item {:value "hr"}
       "Human Resources"]]]
    [field/field-description {}
     "Select your department or area of work."]]]))

(defscene
 select-disabled-items
 "Select with disabled items.

  Custom example — not from shadcn/ui.
  Radix primitive: @radix-ui/react-select

  Use :disabled on items that are not selectable."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/select {:default-value "pro"}
                                      [sut/select-trigger {:class "w-[200px]"}
                                       [sut/select-value {:placeholder "Choose a plan"}]]
                                      [sut/select-content {}
                                       [sut/select-item {:value "starter"}
                                        "Starter"]
                                       [sut/select-item {:value "pro"}
                                        "Pro"]
                                       [sut/select-item {:value "enterprise"
                                                         :disabled true}
                                        "Enterprise (contact us)"]]]]))