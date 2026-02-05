(ns mateuszmazurczak.portfolio.ui-components.theme-toggle
  (:require
   [mateuszmazurczak.portfolio.utils            :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.header       :as header]
   [mateuszmazurczak.ui.components.theme-toggle :as sut]
   [portfolio.reagent-18                        :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Theme Toggle"})

(defscene
 theme-toggle-basic
 "Theme toggle button.

  Custom component — not from shadcn/ui.
  Uses app theme state to toggle light/dark mode.

  Include in headers or settings panels."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/theme-toggle]]))

(defscene
 theme-toggle-in-header
 "Theme toggle inside a header.

  Custom component — not from shadcn/ui.
  Demonstrates composition with header component."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "relative h-20 bg-background"}
                                     [header/header-comp {:size :full
                                                          :sticky? false
                                                          :border? true
                                                          :logo [:span {:class
                                                                        "text-sm font-semibold"}
                                                                 "Brand"]
                                                          :right-section [sut/theme-toggle]}
                                      {:title "Home"
                                       :href "#"}
                                      {:title "Docs"
                                       :href "#"}]]))

(defscene
 theme-toggle-settings-row
 "Theme toggle in a settings row.

  Custom component — not from shadcn/ui.
  Shows the toggle alongside descriptive text.

  Useful for preference screens or settings panels."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-sm"}
   [:div {:class "flex items-center justify-between rounded-md border px-4 py-3"}
    [:div {:class "space-y-1"}
     [:p {:class "text-sm font-medium"}
      "Dark mode"]
     [:p {:class "text-xs text-muted-foreground"}
      "Switch theme for the application."]]
    [sut/theme-toggle]]]))