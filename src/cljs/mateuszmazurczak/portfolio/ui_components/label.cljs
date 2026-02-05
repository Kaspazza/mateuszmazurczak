(ns mateuszmazurczak.portfolio.ui-components.label
  (:require
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.checkbox :as checkbox]
   [mateuszmazurczak.ui.components.input    :as input]
   [mateuszmazurczak.ui.components.label    :as sut]
   [mateuszmazurczak.ui.components.textarea :as textarea]
   [portfolio.reagent-18                    :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Label"})

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