(ns mateuszmazurczak.portfolio.ui-components.label
  (:require
   [mateuszmazurczak.portfolio.utils     :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.input :as input]
   [mateuszmazurczak.ui.components.label :as sut]
   [portfolio.reagent-18                 :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Label"})

(defscene
 basic-label
 "Label with associated input." 
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "flex flex-col gap-2 p-6 max-w-sm"}
   [sut/label {:html-for "email"} "Email address"]
   [input/input {:id "email"
                 :type "email"
                 :placeholder "you@example.com"}]]))