(ns mateuszmazurczak.portfolio.ui-components.scroll-button
  (:require
   [mateuszmazurczak.portfolio.utils              :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.chat-container :as chat-container]
   [mateuszmazurczak.ui.components.scroll-button  :as sut]
   [portfolio.reagent-18                          :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Scroll Button"})

(defscene
 scroll-button-chat
 "Scroll-to-bottom button inside chat container.

  Custom component — not from shadcn/ui.
  Uses use-stick-to-bottom context from chat-container.

  Appears when user scrolls away from bottom."
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

(defscene
 scroll-button-custom
 "Scroll button with custom styling.

  Custom component — not from shadcn/ui.
  Use class overrides for position or style changes."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [chat-container/chat-container-root {:class "relative h-64 w-full rounded-md border"}
    [chat-container/chat-container-content {:class "p-4 space-y-3"}
     (for [idx (range 1 16)]
       ^{:key idx}
       [:div {:class "rounded-lg bg-muted px-3 py-2 text-sm"}
        (str "Message " idx)])
     [chat-container/chat-container-scroll-anchor {}]]
    [sut/scroll-button {:class "absolute bottom-4 right-4 bg-primary text-primary-foreground"}]]]))

(defscene
 scroll-button-standalone
 "Scroll button placement in custom layout.

  Custom component — not from shadcn/ui.
  Place inside any stick-to-bottom container."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [chat-container/chat-container-root {:class "relative h-40 w-full rounded-md border"}
    [chat-container/chat-container-content {:class "p-4 space-y-3"}
     (for [idx (range 1 12)]
       ^{:key idx}
       [:div {:class "rounded-lg bg-muted px-3 py-2 text-sm"}
        (str "Update " idx)])
     [chat-container/chat-container-scroll-anchor {}]]
    [sut/scroll-button {:class "absolute bottom-3 right-3"}]]]))