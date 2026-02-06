(ns mateuszmazurczak.portfolio.ui-components.input
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button :as button]
   [mateuszmazurczak.ui.components.input  :as sut]
   [mateuszmazurczak.ui.components.label  :as label]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]]
   [reagent.core                          :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Input"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Input component for forms."
            :npm-install "No external dependencies"
            :source-code (embed-source mateuszmazurczak.ui.components.input)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/input.cljs"
            :filename "input.cljs"}])

(defscene api-reference
          "Complete reference for all Input component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-6"}
             [:div
              [:p {:class "text-sm text-muted-foreground"}
               "All available props for Input components."]]
             [:div {:class "space-y-4"}
              [mm-portfolio-utils/api-component-card {:component-name "input"
                                                      :description "Input component"
                                                      :props
                                                      [[":class" "any, optional - Component prop"]
                                                       [":type" "any, optional - Component prop"]]}]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[input {}]"]]]]]]))

(defscene
 input-demo
 "Basic input for standard text or email entry.

  Based on shadcn/ui Input — https://ui.shadcn.com/docs/components/input
  Native element: <input>

  Our wrapper accepts all standard input props with Tailwind defaults."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-sm"}
                                     [sut/input {:type "email"
                                                 :placeholder "Email"}]]))

(defscene
 input-file
 "File input with a label for upload workflows.

  Based on shadcn/ui Input — https://ui.shadcn.com/docs/components/input
  Native element: <input type='file'>

  File inputs retain the same base styling for consistency."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-sm space-y-2"}
                                     [label/label {:html-for "resume"}
                                      "Resume"]
                                     [sut/input {:id "resume"
                                                 :type "file"}]]))

(defscene
 input-disabled
 "Disabled input state for locked fields.

  Based on shadcn/ui Input — 'https://ui.shadcn.com/docs/components/input'
  Native element: <input>

  Disabled inputs are visually muted and non-interactive."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-sm"}
                                     [sut/input {:disabled true
                                                 :type "email"
                                                 :placeholder "Email"}]]))

(defscene
 input-with-label
 "Input paired with a label.

  Based on shadcn/ui Input — https://ui.shadcn.com/docs/components/input
  Native element: <input>

  Use labels for accessible form controls."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-sm space-y-2"}
                                     [label/label {:html-for "contact-email"}
                                      "Email"]
                                     [sut/input {:id "contact-email"
                                                 :type "email"
                                                 :placeholder "Email"}]]))

(defscene
 input-with-button
 "Input combined with an inline action button.

  Based on shadcn/ui Input — https://ui.shadcn.com/docs/components/input
  Native element: <input>

  Useful for newsletter signups or quick actions."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [:div {:class "flex w-full max-w-sm items-center gap-2"}
                                      [sut/input {:type "email"
                                                  :placeholder "Email"}]
                                      (button/button {:type "submit"
                                                      :variant :outline}
                                                     "Subscribe")]]))

(defscene
 input-with-text
 "Input with helper text for guidance.

  Based on shadcn/ui Input — https://ui.shadcn.com/docs/components/input
  Native element: <input>

  Helper text clarifies validation or intent."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-sm space-y-2"}
                                     [label/label {:html-for "support-email"}
                                      "Email"]
                                     [sut/input {:id "support-email"
                                                 :type "email"
                                                 :placeholder "Email"}]
                                     [:p {:class "text-muted-foreground text-sm"}
                                      "Enter your support email address."]]))

(defscene
 input-controlled
 "Controlled input state managed via r/atom.

  Custom example — not from shadcn/ui.
  Demonstrates how to keep input value in app state.

  Use controlled inputs when you need validation or formatting."
 []
 (let [value (r/atom "hello@company.com")]
   (fn []
     (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-sm space-y-2"}
                                         [label/label {:html-for "controlled-email"}
                                          "Contact"]
                                         [sut/input {:id "controlled-email"
                                                     :type "email"
                                                     :value @value
                                                     :on-change
                                                     #(reset! value (.. % -target -value))}]
                                         [:p {:class "text-muted-foreground text-sm"}
                                          (str "Current value: " @value)]]))))
