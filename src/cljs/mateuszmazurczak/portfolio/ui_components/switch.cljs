(ns mateuszmazurczak.portfolio.ui-components.switch
  (:require
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.switch  :as sut]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]]
   [reagent.core                           :as r]))

(configure-scenes {:collection :ui-components
                   :title "Switch"})

(defscene
 toggle-switch
 "Switch component with controlled state." 
 []
 (let [enabled? (r/atom true)]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "flex items-center gap-3 p-6"}
       [sut/switch {:checked @enabled?
                    :on-checked-change #(reset! enabled? %)}]
       [:span {:class "text-sm"} (if @enabled? "Enabled" "Disabled")]]))))