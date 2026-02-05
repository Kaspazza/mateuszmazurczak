(ns mateuszmazurczak.portfolio.ui-components.message
  (:require
   ["lucide-react"                       :refer [Copy]]
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button :as button]
   [mateuszmazurczak.ui.components.message :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Message"})

(defscene
 basic-message
 "Chat message with avatar and actions."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/message {}
    [sut/message-avatar {:src "https://placehold.co/40x40/png"
                         :alt "User"
                         :fallback "JD"}]
    [:div {:class "flex flex-col gap-2"}
     [sut/message-content {:markdown? true}
      "Hello! This is a **markdown** message."]
     [sut/message-actions {}
      [sut/message-action {:tooltip "Copy"}
       (button/button {:variant :ghost
                       :size :icon}
                      [:> Copy {:class "size-4"}])]]]]]))