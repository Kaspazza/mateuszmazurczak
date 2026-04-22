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
            :source-code (embed-source "mateuszmazurczak.ui.components.field")
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
     [mm-portfolio-utils/api-component-card
      {:component-name "field-set"
       :description "Semantic fieldset wrapper for grouping related controls."
       :props [{:name ":class"          :type "string"      :default nil :description "Additional Tailwind classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to <fieldset>."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "field-legend"
       :description "Legend/title for a field-set section."
       :props [{:name ":variant"        :type "keyword"     :default ":legend" :description ":legend | :label."}
               {:name ":class"          :type "string"      :default nil       :description "Additional Tailwind classes."}
               {:name "additional props" :type "map entries" :default nil      :description "Forwarded to <legend>."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "field-group"
       :description "Container for vertically grouped fields with consistent spacing."
       :props [[":class" "string, optional - Additional Tailwind classes."]
               ["additional props" "map entries, optional - Forwarded to wrapper <div>."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "field"
       :description "Core field layout wrapper with orientation variants."
       :props [{:name ":orientation"    :type "keyword"     :default ":vertical" :description ":vertical | :horizontal | :responsive."}
               {:name ":class"          :type "string"      :default nil         :description "Additional Tailwind classes."}
               {:name "additional props" :type "map entries" :default nil        :description "Forwarded to wrapper <div>."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "field-content"
       :description "Groups input/control with description and errors."
       :props [{:name ":class"          :type "string"      :default nil :description "Additional Tailwind classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to wrapper <div>."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "field-label"
       :description "Field-aware label wrapper around base label component."
       :props [{:name ":html-for"       :type "string"      :default nil :description "Associates label with input id."}
               {:name ":class"          :type "string"      :default nil :description "Additional Tailwind classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to underlying label."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "field-title"
       :description "Title slot used inside complex field labels/cards."
       :props [{:name ":class"          :type "string"      :default nil :description "Additional Tailwind classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to wrapper <div>."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "field-description"
       :description "Secondary helper text for context and guidance."
       :props [{:name ":class"          :type "string"      :default nil :description "Additional Tailwind classes."}
               {:name "additional props" :type "map entries" :default nil :description "Forwarded to <p>."}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "field-separator"
       :description "Visual separator between field blocks; can render optional children label."
       :props [[":class" "string, optional - Additional Tailwind classes."]
               ["children" "optional - Label content displayed over separator line."]
               ["additional props" "map entries, optional - Forwarded to wrapper <div>."]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "field-error"
       :description "Validation message renderer supporting one or multiple error messages."
       :props [{:name ":errors"  :type "vector<map>" :default nil :description "Error maps with :message keys."}
               {:name ":class"   :type "string"      :default nil :description "Additional Tailwind classes."}
               {:name "children" :type nil           :default nil :description "Custom error content; takes priority over :errors."}]}]
     [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
      [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
      [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
       [:li "Use :html-for on field-label with matching input :id for accessible label click behavior."]
       [:li "field-separator can take children; without children it renders a plain separator line."]
       [:li "Most field helpers forward extra props to underlying DOM nodes for flexibility."]]]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[field-set {}\n [field-legend {} \"Profile\"]\n [field {}\n  [field-label {:html-for \"display-name\"} \"Display name\"]\n  [field-content {}\n   [input {:id \"display-name\"}]\n   [field-description {} \"Shown publicly\"]]]]"]]]]]]))

(defscene
 field-with-input
 "Field with label, input, and description.

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