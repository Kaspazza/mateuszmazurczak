(ns mateuszmazurczak.portfolio.ui-components.notification
  (:require
   [mateuszmazurczak.portfolio.utils           :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button      :as button]
   [mateuszmazurczak.ui.components.notification :as sut]
   [portfolio.reagent-18                       :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Notification"})

(defscene
 toast-notifications
 "Toast notifications with actions."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 space-y-4"}
   [sut/toaster]
      (button/button {:on-click (fn []
                                   (sut/show-toast "Action completed"
                                                   {:description "Everything looks good"
                                                    :action {:label "Undo"
                                                             :on-click (fn [] (js/console.log "undo"))}}))}
                     "Show Toast")]))