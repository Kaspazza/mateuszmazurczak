(ns mateuszmazurczak.portfolio.ui-components.sheet
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button :as button]
   [mateuszmazurczak.ui.components.sheet  :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]]
   [reagent.core                          :as r]))

(configure-scenes {:collection :ui-components
                   :title "Sheet"})

(defscene
 basic-sheet
 "Sheet sliding from the right." 
 []
 (let [open? (r/atom false)]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6"}
       [:> sut/sheet {:open @open?
                      :on-open-change #(reset! open? %)}
        [:> sut/sheet-trigger {:as-child true}
         (button/button {:variant :outline} "Open Sheet")]
        [sut/sheet-content {:side :right}
         [sut/sheet-header {}
          [sut/sheet-title {} "Settings"]
          [sut/sheet-description {} "Adjust your preferences."]]
         [:div {:class "py-4 text-sm"}
          "Sheet content goes here."]
         [sut/sheet-footer {}
          (button/button {:variant :outline
                          :on-click #(reset! open? false)}
                         "Close")]]]]))))