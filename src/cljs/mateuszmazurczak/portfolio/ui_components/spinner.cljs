(ns mateuszmazurczak.portfolio.ui-components.spinner
  (:require
   [mateuszmazurczak.portfolio.utils     :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.spinner :as sut]
   [portfolio.reagent-18                 :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Spinner"})

(defscene
 basic-spinner
 "Inline spinner indicator." 
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/spinner {:class "size-6"}]]))