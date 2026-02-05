(ns mateuszmazurczak.portfolio.ui-components.notification
  (:require
   [mateuszmazurczak.portfolio.utils            :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button       :as button]
   [mateuszmazurczak.ui.components.notification :as sut]
   [portfolio.reagent-18                        :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Notification"})

(defscene
 toast-basic
 "Basic toast notification.

  Custom component — not from shadcn/ui.
  Uses Sonner for toast rendering.

  Call show-toast with a simple message."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 space-y-4"}
   [sut/toaster]
   (button/button {:on-click (fn [] (sut/show-toast "Event has been created"))} "Show Toast")]))

(defscene
 toast-with-description
 "Toast with description text.

  Custom component — not from shadcn/ui.
  Use :description to provide context."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 space-y-4"}
   [sut/toaster]
   (button/button {:on-click (fn []
                               (sut/show-toast "Event created"
                                               {:description "Sunday, December 3 at 9:00 AM"}))}
                  "Show Detailed Toast")]))

(defscene
 toast-with-action
 "Toast with action button.

  Custom component — not from shadcn/ui.
  Use :action for undo or follow-up steps."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 space-y-4"}
   [sut/toaster]
   (button/button {:on-click (fn []
                               (sut/show-toast "File deleted"
                                               {:action {:label "Undo"
                                                         :on-click (fn []
                                                                     (js/console.log "undo"))}}))}
                  "Show Action Toast")]))

(defscene
 toast-duration
 "Toast with custom duration.

  Custom component — not from shadcn/ui.
  Use :duration for longer or shorter visibility."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 space-y-4"}
   [sut/toaster]
   (button/button {:on-click (fn [] (sut/show-toast "Auto closes in 10 seconds" {:duration 10000}))}
                  "Show Long Toast")]))