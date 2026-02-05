(ns mateuszmazurczak.portfolio.ui-components.scroll-button
  (:require
   [mateuszmazurczak.portfolio.utils             :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.chat-container :as chat-container]
   [mateuszmazurczak.ui.components.scroll-button :as sut]
   [portfolio.reagent-18                         :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Scroll Button"})

(defscene
 scroll-to-bottom-button
 "Scroll button inside chat container." 
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [chat-container/chat-container-root {:class "relative h-64 w-full rounded-md border"}
    [chat-container/chat-container-content {:class "p-4 space-y-3"}
     (for [idx (range 1 20)]
       ^{:key idx}
       [:div {:class "rounded-lg bg-muted px-3 py-2 text-sm"}
        (str "Line " idx)])
     [chat-container/chat-container-scroll-anchor {}]]
    [sut/scroll-button {:class "absolute bottom-4 right-4"}]]]))