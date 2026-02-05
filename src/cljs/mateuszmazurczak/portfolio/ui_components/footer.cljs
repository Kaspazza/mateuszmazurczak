(ns mateuszmazurczak.portfolio.ui-components.footer
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.footer :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Footer"})

(defscene
 basic-footer
 "Footer component with social icons."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 bg-background"}
   [sut/footer]]))