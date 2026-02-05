(ns mateuszmazurczak.portfolio.ui-components.navigation
  (:require
   [mateuszmazurczak.portfolio.utils         :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.navigation :as sut]
   [portfolio.reagent-18                     :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Navigation"})

(defscene
 navigation-links
 "Navigation and back navigation links."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "flex flex-col gap-3 p-6"}
   [sut/navigation {:href "#" :text "Go to Docs"}]
   [sut/back-navigation {:href "#" :text "Back to Home" :dark? false}]]))