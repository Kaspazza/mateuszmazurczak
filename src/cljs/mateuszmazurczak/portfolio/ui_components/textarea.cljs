(ns mateuszmazurczak.portfolio.ui-components.textarea
  (:require
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button   :as button]
   [mateuszmazurczak.ui.components.label    :as label]
   [mateuszmazurczak.ui.components.textarea :as sut]
   [portfolio.reagent-18                    :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Textarea"})

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