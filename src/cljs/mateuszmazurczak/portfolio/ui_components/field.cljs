(ns mateuszmazurczak.portfolio.ui-components.field
  (:require
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.field    :as sut]
   [mateuszmazurczak.ui.components.input    :as input]
   [mateuszmazurczak.ui.components.select   :as select]
   [mateuszmazurczak.ui.components.textarea :as textarea]
   [portfolio.reagent-18                    :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Field"})

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