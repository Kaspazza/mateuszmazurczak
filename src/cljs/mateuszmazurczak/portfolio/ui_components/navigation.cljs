(ns mateuszmazurczak.portfolio.ui-components.navigation
  (:require
   [mateuszmazurczak.portfolio.utils          :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.navigation :as sut]
   [portfolio.reagent-18                      :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Navigation"})

(defscene
 navigation-forward
 "Forward navigation link.

  Custom component — not from shadcn/ui.
  Simple anchor for primary navigation.

  Use :href or :on-click for routing."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/navigation {:href "#"
                                                      :text "Go to Docs"}]]))

(defscene
 navigation-back
 "Back navigation link with arrow.

  Custom component — not from shadcn/ui.
  Useful for returning to parent pages."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/back-navigation {:href "#"
                                                           :text "Back to Home"
                                                           :dark? false}]]))

(defscene
 navigation-dark-mode
 "Back navigation in dark mode variant.

  Custom component — not from shadcn/ui.
  Use :dark? true to switch text color for dark backgrounds."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 bg-neutral-900"}
                                     [sut/back-navigation {:href "#"
                                                           :text "Back to Home"
                                                           :dark? true}]]))