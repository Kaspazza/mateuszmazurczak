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

(defscene
 footer-in-layout
 "Footer inside a page layout.

  Custom component — not from shadcn/ui.
  Demonstrates the footer anchoring the bottom of a page.

  Useful for marketing or landing pages."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 min-h-[240px] border rounded-md flex flex-col justify-between"}
   [:div {:class "space-y-2"}
    [:h3 {:class "text-lg font-semibold"}
     "Acme Product"]
    [:p {:class "text-sm text-muted-foreground"}
     "Build better workflows with a modern UI kit."]]
   [sut/footer]]))

(defscene
 footer-on-muted
 "Footer on muted background.

  Custom component — not from shadcn/ui.
  Shows how the footer reads on a tinted surface.

  Use for dashboards or side panels."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 bg-muted rounded-md"}
                                     [sut/footer]]))