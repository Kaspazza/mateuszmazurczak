(ns mateuszmazurczak.portfolio.ui-components.badge
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.badge  :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Badge"})

(defscene
 badge-variants
 "Badge variants for status tags."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "flex flex-wrap gap-2 p-6"}
   [sut/badge {} "Default"]
   [sut/badge {:variant :secondary} "Secondary"]
   [sut/badge {:variant :destructive} "Error"]
   [sut/badge {:variant :outline} "Outline"]]))