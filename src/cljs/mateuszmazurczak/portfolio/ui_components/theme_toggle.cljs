(ns mateuszmazurczak.portfolio.ui-components.theme-toggle
  (:require
   [mateuszmazurczak.portfolio.utils         :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.theme-toggle :as sut]
   [portfolio.reagent-18                     :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Theme Toggle"})

(defscene
 basic-theme-toggle
 "Theme toggle button." 
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/theme-toggle]]))