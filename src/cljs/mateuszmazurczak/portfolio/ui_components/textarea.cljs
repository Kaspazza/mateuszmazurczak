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
            :source-code (embed-source "mateuszmazurczak.ui.components.textarea")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/textarea.cljs"
            :filename "textarea.cljs"}])

(defscene api-reference
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-4"}
             [mm-portfolio-utils/api-component-card
              {:component-name "textarea"
                :description "Styled native textarea with optional autosizing behavior. Additional props are forwarded to underlying <textarea>."
                :props [{:name ":value"          :type "string"      :default nil    :description "Controlled value."}
                        {:name ":default-value"  :type "string"      :default nil    :description "Uncontrolled initial value."}
                        {:name ":placeholder"    :type "string"      :default nil    :description "Placeholder text."}
                        {:name ":disabled"       :type "boolean"     :default nil    :description "Disables textarea."}
                        {:name ":required"       :type "boolean"     :default nil    :description "Marks textarea as required."}
                        {:name ":rows"           :type "number"      :default nil    :description "Visible row count."}
                        {:name ":cols"           :type "number"      :default nil    :description "Visible column count."}
                        {:name ":on-change"      :type "function"    :default nil    :description "Change handler."}
                        {:name ":on-blur"        :type "function"    :default nil    :description "Blur handler."}
                        {:name ":on-focus"       :type "function"    :default nil    :description "Focus handler."}
                        {:name ":auto-size?"     :type "boolean"     :default "true" :description "Uses field-sizing-content when true."}
                        {:name ":class"          :type "string"      :default nil    :description "Additional Tailwind classes."}
                        {:name "additional props" :type "map entries" :default nil   :description "Forwarded to native <textarea>."}]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "Default :auto-size? is true (not false). Disable it when you need manual resize handles."]
                [:li "Use :aria-invalid true to trigger built-in destructive validation styles."]
                [:li "Prefer controlled mode for forms with validation/state sync."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[:div {:class \"space-y-2 max-w-sm\"}\n [textarea {:placeholder \"Tell us about your project\"}]\n [textarea {:aria-invalid true\n            :default-value \"too short\"\n            :auto-size? false}]]"]]]]]]))

(defscene
 textarea-demo
 "Basic textarea for multi-line input.

  Native element: <textarea>

  Uses Tailwind styling and supports auto-sizing via :auto-size?."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-sm"}
                                     [sut/textarea {:placeholder "Type your message here."}]]))

(defscene
 textarea-disabled
 "Disabled textarea for read-only content.

  Native element: <textarea>

  Disabled state applies muted styling and blocks input."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-sm"}
                                     [sut/textarea {:placeholder "Type your message here."
                                                    :disabled true}]]))

(defscene
 textarea-with-label
 "Textarea with a label for accessibility.

  Native element: <textarea>

  Use labels for longer form inputs and clarity."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-sm space-y-2"}
                                     [label/label {:html-for "message"}
                                      "Your message"]
                                     [sut/textarea {:id "message"
                                                    :placeholder "Type your message here."}]]))

(defscene
 textarea-invalid
 "Invalid textarea state with inline error.

  Native element: <textarea>

  Pair :aria-invalid with error messaging for clear validation feedback."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-sm space-y-2"}
                                     [label/label {:html-for "feedback-invalid"}
                                      "Feedback"]
                                     [sut/textarea {:id "feedback-invalid"
                                                    :aria-invalid true
                                                    :default-value "bad"
                                                    :placeholder "Tell us what happened..."}]
                                     [:p {:class "text-destructive text-sm"}
                                      "Feedback should be at least 10 characters."]]))

(defscene
 textarea-with-button
 "Textarea with a submit button.

  Native element: <textarea>

  Useful for support forms or quick feedback widgets."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-sm space-y-2"}
                                     [sut/textarea {:placeholder "Type your message here."}]
                                     (button/button {} "Send message")]))

(defscene
 textarea-with-text
 "Textarea with helper text.

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