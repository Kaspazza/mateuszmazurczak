(ns mateuszmazurczak.portfolio.ui-components.select
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.select :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]]
   [reagent.core                          :as r]))

(configure-scenes {:collection :ui-components
                   :title "Select"})

(defscene
 basic-select
 "Select with grouped items." 
 []
  (let [value (r/atom "apple")]
    (fn []
      (mm-portfolio-utils/wrap-component
       [:div {:class "p-6"}
        [sut/select {:value @value
                     :on-value-change #(reset! value %)}
         [sut/select-trigger {:class "min-w-[180px]"}
          [sut/select-value {:placeholder "Select fruit"}]]
         [sut/select-content {}
          [sut/select-label {} "Fruits"]
          [sut/select-item {:value "apple"} "Apple"]
          [sut/select-item {:value "banana"} "Banana"]
          [sut/select-item {:value "orange"} "Orange"]
          [sut/select-separator {}]
          [sut/select-label {} "Vegetables"]
          [sut/select-item {:value "carrot"} "Carrot"]]]]))))
