(ns mateuszmazurczak.portfolio.ui-components.label
  (:require
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.checkbox :as checkbox]
   [mateuszmazurczak.ui.components.input    :as input]
   [mateuszmazurczak.ui.components.label    :as sut]
   [mateuszmazurczak.ui.components.textarea :as textarea]
   [portfolio.reagent-18                    :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Label"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Label component for form fields with accessibility support."
            :npm-install "npm install @radix-ui/react-label"
            :source-code (embed-source "mateuszmazurczak.ui.components.label")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/label.cljs"
            :filename "label.cljs"}])

(defscene api-reference
          "Complete reference for all Label component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-6"}
             [:div
              [:p {:class "text-sm text-muted-foreground"}
               "All available props for Label components."]]
             [:div {:class "space-y-4"}
              [mm-portfolio-utils/api-component-card
               {:component-name "label"
                :description "Accessible form label wrapper around Radix Label primitive."
                :props [[":class" "string, optional - Additional Tailwind classes."]
                        [":html-for" "string, optional - Associates label with control id."]
                        ["additional props" "map entries, optional - Forwarded to underlying label element."]]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "For proper click/focus behavior, pair :html-for with matching input :id."]
                [:li "Labels support nested interactive layouts, but keep text concise for accessibility."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[:div {:class \"space-y-2\"}\n [label {:html-for \"email\"} \"Email\"]\n [input {:id \"email\" :type \"email\" :placeholder \"you@example.com\"}]]" ]]]]]]))

(defscene
 label-demo
 "Label paired with a checkbox.

  Based on shadcn/ui Label — https://ui.shadcn.com/docs/components/label
  Radix primitive: @radix-ui/react-label

  Labels improve accessibility and click targets."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [:div {:class "flex items-center gap-2"}
                                      [checkbox/checkbox {:id "terms"}]
                                      [sut/label {:html-for "terms"}
                                       "Accept terms and conditions"]]]))

(defscene
 input-with-label
 "Label with input field.

  Based on shadcn/ui Input — https://ui.shadcn.com/docs/components/input
  Radix primitive: @radix-ui/react-label

  Keep labels close to inputs for clarity."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-sm space-y-2"}
                                     [sut/label {:html-for "email"}
                                      "Email"]
                                     [input/input {:id "email"
                                                   :type "email"
                                                   :placeholder "Email"}]]))

(defscene
 textarea-with-label
 "Label with textarea for multi-line input.

  Based on shadcn/ui Textarea — https://ui.shadcn.com/docs/components/textarea
  Radix primitive: @radix-ui/react-label

  Use labels to describe longer-form fields."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 max-w-sm space-y-2"}
                                     [sut/label {:html-for "message"}
                                      "Your message"]
                                     [textarea/textarea {:id "message"
                                                         :placeholder "Type your message here."}]]))