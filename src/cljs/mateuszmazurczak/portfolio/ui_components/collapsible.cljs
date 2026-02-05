(ns mateuszmazurczak.portfolio.ui-components.collapsible
  (:require
   [mateuszmazurczak.portfolio.utils           :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.button      :as button]
   [mateuszmazurczak.ui.components.collapsible :as sut]
   [portfolio.reagent-18                       :refer-macros [defscene configure-scenes]]
   [reagent.core                               :as r]))

(configure-scenes {:collection :ui-components
                   :title "Collapsible"})

(defscene basic-collapsible
          "Collapsible content with toggle."
          []
          (let [open? (r/atom false)]
            (fn []
              (mm-portfolio-utils/wrap-component
               [:div {:class "p-6"}
                [sut/collapsible {:open @open?
                                  :on-open-change #(reset! open? %)}
                 [sut/collapsible-trigger {}
                  (button/button {:variant :outline} (if @open? "Hide details" "Show details"))]
                 [sut/collapsible-content {:class "mt-4"}
                  [:div {:class "rounded-md border bg-muted p-4 text-sm"}
                   "This content expands and collapses."]]]]))))