(ns mateuszmazurczak.portfolio.ui-components.field
  (:require
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.field    :as sut]
   [mateuszmazurczak.ui.components.input    :as input]
   [mateuszmazurczak.ui.components.select   :as select]
   [mateuszmazurczak.ui.components.textarea :as textarea]
   [portfolio.reagent-18                    :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Field"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Field component for building accessible form layouts."
            :npm-install "No external dependencies"
            :source-code (embed-source mateuszmazurczak.ui.components.field)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/field.cljs"
            :filename "field.cljs"}])

(defscene
 api-reference
 "Complete reference for all Field component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Field components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card {:component-name "field-set"
                                             :description "Field set component"
                                             :props [[":class" "any, optional - Component prop"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "field-legend"
                                             :description "Field legend component"
                                             :props [[":class" "any, optional - Component prop"]
                                                     [":variant"
                                                      "any, optional - Component prop"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "field-group"
                                             :description "Field group component"
                                             :props [[":class" "any, optional - Component prop"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "field"
                                             :description "Field component"
                                             :props [[":class" "any, optional - Component prop"]
                                                     [":orientation"
                                                      "any, optional - Component prop"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "field-content"
                                             :description "Field content component"
                                             :props [[":class" "any, optional - Component prop"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "field-label"
                                             :description "Field label component"
                                             :props [[":class" "any, optional - Component prop"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "field-title"
                                             :description "Field title component"
                                             :props [[":class" "any, optional - Component prop"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "field-description"
                                             :description "Field description component"
                                             :props [[":class" "any, optional - Component prop"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "field-separator"
                                             :description "Field separator component"
                                             :props [[":class" "any, optional - Component prop"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "field-error"
                                             :description "Field error component"
                                             :props [[":class" "any, optional - Component prop"]
                                                     [":errors" "any, optional - Component prop"]]}]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[field-set {}]"]]]]]]))

(defscene
 field-with-input
 "Field with label, input, and description.

  Based on shadcn/ui Field — https://ui.shadcn.com/docs/components/field
  Custom component for form layouts.

  Use field-content to group controls and description text."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-md"}
                                     [sut/field-set {}
                                      [sut/field-legend {}
                                       "Profile"]
                                      [sut/field {}
                                       [sut/field-label {:html-for "name"}
                                        "Name"]
                                       [sut/field-content {}
                                        [input/input {:id "name"
                                                      :placeholder "Jane Doe"}]
                                        [sut/field-description {}
                                         "Use your full name for display purposes."]]]]]))

(defscene
 field-with-textarea
 "Field with textarea.

  Based on shadcn/ui Field — https://ui.shadcn.com/docs/components/field
  Custom component for form layouts.

  Use for multi-line inputs and richer descriptions."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-md"}
                                     [sut/field {}
                                      [sut/field-label {:html-for "bio"}
                                       "Bio"]
                                      [sut/field-content {}
                                       [textarea/textarea {:id "bio"
                                                           :placeholder "Tell us about yourself"}]
                                       [sut/field-description {}
                                        "A short bio will appear on your profile."]]]]))

(defscene
 field-with-select
 "Field with select control.

  Based on shadcn/ui Field — https://ui.shadcn.com/docs/components/field
  Custom component for form layouts.

  Combine field + select for structured inputs."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-md"}
                                     [sut/field {}
                                      [sut/field-label {}
                                       "Department"]
                                      [select/select {:default-value "design"}
                                       [select/select-trigger {}
                                        [select/select-value {:placeholder "Choose department"}]]
                                       [select/select-content {}
                                        [select/select-item {:value "engineering"}
                                         "Engineering"]
                                        [select/select-item {:value "design"}
                                         "Design"]
                                        [select/select-item {:value "marketing"}
                                         "Marketing"]]]
                                      [sut/field-description {}
                                       "Select your department or area of work."]]]))

(defscene
 field-with-error
 "Field displaying validation errors.

  Based on shadcn/ui Field — https://ui.shadcn.com/docs/components/field
  Custom component for form layouts.

  Use field-error to show validation messages."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-md"}
                                     [sut/field {}
                                      [sut/field-label {:html-for "email"}
                                       "Email"]
                                      [sut/field-content {}
                                       [input/input {:id "email"
                                                     :placeholder "name@example.com"
                                                     :aria-invalid true}]
                                       [sut/field-error {:errors [{:message "Email is required"}
                                                                  {:message
                                                                   "Email must be valid"}]}]]]]))

(defscene
 fieldset-multiple-fields
 "Field set with multiple related fields.

  Based on shadcn/ui Field — https://ui.shadcn.com/docs/components/field
  Custom component for form layouts.

  Use field-set + field-group for multi-field sections."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-md"}
   [sut/field-set {}
    [sut/field-legend {}
     "Billing"]
    [sut/field-group {}
     [sut/field {}
      [sut/field-label {:html-for "card"}
       "Card Number"]
      [sut/field-content {}
       [input/input {:id "card"
                     :placeholder "1234 5678 9012 3456"}]]]
     [sut/field {:orientation :responsive}
      [sut/field-label {:html-for "zip"}
       "ZIP"]
      [sut/field-content {}
       [input/input {:id "zip"
                     :placeholder "94105"}]]]]]]))