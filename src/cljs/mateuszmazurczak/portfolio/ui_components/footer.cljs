(ns mateuszmazurczak.portfolio.ui-components.footer
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.footer :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Footer"})

(defscene
 footer-basic
 "Footer with social icons.

  Custom component — not from shadcn/ui.
  Simple footer layout for social links.

  This component is app-aware (theme-driven icons)."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 bg-background"}
                                     [sut/footer]]))