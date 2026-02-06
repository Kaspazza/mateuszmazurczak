(ns mateuszmazurczak.portfolio.ui-components.textarea
  (:require
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button   :as button]
   [mateuszmazurczak.ui.components.label    :as label]
   [mateuszmazurczak.ui.components.textarea :as sut]
   [portfolio.reagent-18                    :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Textarea"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Textarea component for forms."
            :npm-install "No external dependencies"
            :source-code (embed-source mateuszmazurczak.ui.components.textarea)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/textarea.cljs"
            :filename "textarea.cljs"}])

(defscene api-reference
          "Complete reference for all Textarea component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-6"}
             [:div
              [:p {:class "text-sm text-muted-foreground"}
               "All available props for Textarea components."]]
             [:div {:class "space-y-4"}
              [mm-portfolio-utils/api-component-card
               {:component-name "textarea"
                :description "Textarea component"
                :props [[":class" "any, optional - Component prop"]
                        [":auto-size?" "any, optional - Component prop"]]}]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[textarea {}]"]]]]]]))

(defscene
 textarea-demo
 "Basic textarea for multi-line input.

  Based on shadcn/ui Textarea — https://ui.shadcn.com/docs/components/textarea
  Native element: <textarea>

  Uses Tailwind styling and supports auto-sizing via :auto-size?."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-sm"}
                                     [sut/textarea {:placeholder "Type your message here."}]]))

(defscene
 textarea-disabled
 "Disabled textarea for read-only content.

  Based on shadcn/ui Textarea — https://ui.shadcn.com/docs/components/textarea
  Native element: <textarea>

  Disabled state applies muted styling and blocks input."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-sm"}
                                     [sut/textarea {:placeholder "Type your message here."
                                                    :disabled true}]]))

(defscene
 textarea-with-label
 "Textarea with a label for accessibility.

  Based on shadcn/ui Textarea — https://ui.shadcn.com/docs/components/textarea
  Native element: <textarea>

  Use labels for longer form inputs and clarity."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-sm space-y-2"}
                                     [label/label {:html-for "message"}
                                      "Your message"]
                                     [sut/textarea {:id "message"
                                                    :placeholder "Type your message here."}]]))

(defscene
 textarea-with-button
 "Textarea with a submit button.

  Based on shadcn/ui Textarea — https://ui.shadcn.com/docs/components/textarea
  Native element: <textarea>

  Useful for support forms or quick feedback widgets."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-sm space-y-2"}
                                     [sut/textarea {:placeholder "Type your message here."}]
                                     (button/button {} "Send message")]))

(defscene
 textarea-with-text
 "Textarea with helper text.

  Based on shadcn/ui Textarea — https://ui.shadcn.com/docs/components/textarea
  Native element: <textarea>

  Helper text clarifies what happens after submission."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-sm space-y-2"}
                                     [label/label {:html-for "message-2"}
                                      "Your message"]
                                     [sut/textarea {:id "message-2"
                                                    :placeholder "Type your message here."}]
                                     [:p {:class "text-muted-foreground text-sm"}
                                      "Your message will be routed to the support team."]]))