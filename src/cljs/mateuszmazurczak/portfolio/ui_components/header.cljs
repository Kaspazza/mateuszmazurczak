(ns mateuszmazurczak.portfolio.ui-components.header
  (:require
   [mateuszmazurczak.portfolio.utils            :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.header       :as sut]
   [mateuszmazurczak.ui.components.theme-toggle :as theme-toggle]
   [portfolio.reagent-18                        :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Header"})

(defscene
 header-basic
 "Basic header with logo and right section.

  Custom component — not from shadcn/ui.
  Pure presentation wrapper for site headers.

  Use header-comp to supply navigation items."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "relative h-24 bg-background"}
   [sut/header-comp {:size :full
                     :sticky? false
                     :border? true
                     :logo [:span {:class "text-sm font-semibold"}
                            "Brand"]
                     :right-section [:span {:class "text-xs text-muted-foreground"}
                                     "Right"]}
    {:title "Docs"
     :href "#"}
    {:title "Blog"
     :href "#"}
    {:title "Contact"
     :href "#"}]]))

(defscene
 header-sticky
 "Sticky header with border.

  Custom component — not from shadcn/ui.
  Sticky headers remain visible on scroll.

  Use :sticky? true for fixed navigation."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "relative h-24 bg-background"}
                                     [sut/header-comp {:size :full
                                                       :sticky? true
                                                       :border? true
                                                       :logo [:span {:class "text-sm font-semibold"}
                                                              "Brand"]
                                                       :right-section [:span {:class "text-xs"}
                                                                       "Account"]}
                                      {:title "Overview"
                                       :href "#"}
                                      {:title "Projects"
                                       :href "#"}
                                      {:title "Settings"
                                       :href "#"}]]))

(defscene
 header-half-width
 "Header with half-width layout.

  Custom component — not from shadcn/ui.
  Use :size :half for constrained layouts."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "relative h-24 bg-background"}
                                     [sut/header-comp {:size :half
                                                       :sticky? false
                                                       :border? false
                                                       :logo [:span {:class "text-sm font-semibold"}
                                                              "Brand"]
                                                       :right-section [:span {:class "text-xs"}
                                                                       "Sign In"]}
                                      {:title "Features"
                                       :href "#"}
                                      {:title "Pricing"
                                       :href "#"}]]))

(defscene
 header-with-theme-toggle
 "Header with theme toggle in right section.

  Custom component — not from shadcn/ui.
  Demonstrates composition with theme toggle control."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "relative h-24 bg-background"}
                                     [sut/header-comp {:size :full
                                                       :sticky? false
                                                       :border? true
                                                       :logo [:span {:class "text-sm font-semibold"}
                                                              "Brand"]
                                                       :right-section [theme-toggle/theme-toggle]}
                                      {:title "Home"
                                       :href "#"}
                                      {:title "Docs"
                                       :href "#"}]]))