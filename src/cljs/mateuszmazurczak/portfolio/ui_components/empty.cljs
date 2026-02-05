(ns mateuszmazurczak.portfolio.ui-components.empty
  (:require
   ["lucide-react"                         :refer [Inbox]]
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button   :as button]
   [mateuszmazurczak.ui.components.empty    :as sut]
   [portfolio.reagent-18                    :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Empty State"})

(defscene
 basic-empty-state
 "Empty state with icon, text, and action."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/empty {}
    [sut/empty-header {}
     [sut/empty-media {:variant :icon}
      [:> Inbox]]
     [sut/empty-title {} "No messages"]
     [sut/empty-description {}
      "You have no messages yet. Start a conversation to see activity here."]]
    [sut/empty-content {}
     (button/button {:variant :default} "New Message")]]]))