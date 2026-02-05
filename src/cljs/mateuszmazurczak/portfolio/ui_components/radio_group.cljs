(ns mateuszmazurczak.portfolio.ui-components.radio-group
  (:require
   [mateuszmazurczak.portfolio.utils         :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.label     :as label]
   [mateuszmazurczak.ui.components.radio-group :as sut]
   [portfolio.reagent-18                     :refer-macros [defscene configure-scenes]]
   [reagent.core                             :as r]))

(configure-scenes {:collection :ui-components
                   :title "Radio Group"})

(defscene
 basic-radio-group
 "Radio group with labels." 
 []
  (let [value (r/atom "option-1")]
    (fn []
      (mm-portfolio-utils/wrap-component
       [:div {:class "p-6"}
        [sut/radio-group {:value @value
                          :on-value-change #(reset! value %)}
         [:div {:class "flex items-center gap-2"}
          [sut/radio-group-item {:value "option-1" :id "opt-1"}]
          [label/label {:html-for "opt-1"} "Option 1"]]
         [:div {:class "flex items-center gap-2"}
          [sut/radio-group-item {:value "option-2" :id "opt-2"}]
          [label/label {:html-for "opt-2"} "Option 2"]]]]))))
