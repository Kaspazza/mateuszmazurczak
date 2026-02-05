(ns mateuszmazurczak.portfolio.ui-components.button
  (:require
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button  :as sut]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Button"})

(defscene
 button-variants
 "Button variants and sizes."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "flex flex-col gap-4 p-6"}
   [:div {:class "flex flex-wrap gap-2"}
    [sut/button {} "Default"]
    [sut/button {:variant :secondary} "Secondary"]
    [sut/button {:variant :outline} "Outline"]
    [sut/button {:variant :ghost} "Ghost"]
    [sut/button {:variant :destructive} "Destructive"]]
   [:div {:class "flex flex-wrap gap-2"}
    [sut/button {:size :sm} "Small"]
    [sut/button {:size :default} "Default"]
    [sut/button {:size :lg} "Large"]]]))