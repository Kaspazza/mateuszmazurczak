(ns mateuszmazurczak.portfolio.ui-components.popover
  (:require
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button   :as button]
   [mateuszmazurczak.ui.components.popover  :as sut]
   [portfolio.reagent-18                    :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Popover"})

(defscene
 basic-popover
 "Popover with trigger and content."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/popover {}
    [sut/popover-trigger {:asChild true}
     (button/button {:variant :outline} "Open Popover")]
    [sut/popover-content {}
     [:div {:class "space-y-1"}
      [:h4 {:class "text-sm font-medium"} "Dimensions"]
      [:p {:class "text-sm text-muted-foreground"}
       "Set size and spacing for the layout."]]]]]))