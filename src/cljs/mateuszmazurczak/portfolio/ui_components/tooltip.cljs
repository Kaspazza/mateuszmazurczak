(ns mateuszmazurczak.portfolio.ui-components.tooltip
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button :as button]
   [mateuszmazurczak.ui.components.tooltip :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Tooltip"})

(defscene
 basic-tooltip
 "Tooltip with a button trigger." 
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/tooltip {:trigger [button/button {:variant :outline} "Hover me"]
                 :content "Helpful tooltip text"}]]))