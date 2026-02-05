(ns mateuszmazurczak.portfolio.ui-components.chat-container
  (:require
   [mateuszmazurczak.portfolio.utils             :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.chat-container :as sut]
   [portfolio.reagent-18                         :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Chat Container"})

(defscene
 basic-chat-container
 "Chat container with scroll anchor."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/chat-container-root {:class "h-64 w-full rounded-md border"}
    [sut/chat-container-content {:class "p-4 space-y-3"}
     (for [idx (range 1 15)]
       ^{:key idx}
       [:div {:class "rounded-lg bg-muted px-3 py-2 text-sm"}
        (str "Message " idx)])
     [sut/chat-container-scroll-anchor {}]]]]))