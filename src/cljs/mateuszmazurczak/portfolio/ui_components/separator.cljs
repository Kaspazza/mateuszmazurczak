(ns mateuszmazurczak.portfolio.ui-components.separator
  (:require
   [mateuszmazurczak.portfolio.utils         :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.separator :as sut]
   [portfolio.reagent-18                     :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Separator"})

(defscene
 separator-orientations
 "Horizontal and vertical separators." 
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "flex flex-col gap-6 p-6"}
   [:div {:class "space-y-2"}
    [:span {:class "text-sm"} "Section A"]
    [sut/separator {}]
    [:span {:class "text-sm"} "Section B"]]
   [:div {:class "flex h-6 items-center gap-3"}
    [:span {:class "text-sm"} "Item 1"]
    [sut/separator {:orientation :vertical}]
    [:span {:class "text-sm"} "Item 2"]]]))