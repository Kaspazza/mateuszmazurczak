(ns mateuszmazurczak.portfolio.ui-components.checkbox
  (:require
   [mateuszmazurczak.portfolio.utils         :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.checkbox  :as sut]
   [portfolio.reagent-18                     :refer-macros [defscene configure-scenes]]
   [reagent.core                             :as r]))

(configure-scenes {:collection :ui-components
                   :title "Checkbox"})

(defscene
 checkbox-states
 "Checkbox controlled and disabled states."
 []
 (let [checked? (r/atom true)]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "flex flex-col gap-4 p-6"}
       [:label {:class "flex items-center gap-2 text-sm"}
        [sut/checkbox {:checked @checked?
                       :on-checked-change #(reset! checked? %)}]
        "Subscribe to updates"]
       [:label {:class "flex items-center gap-2 text-sm"}
        [sut/checkbox {:checked true
                       :disabled true}]
        "Disabled" ]]))))