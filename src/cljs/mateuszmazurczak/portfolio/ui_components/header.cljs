(ns mateuszmazurczak.portfolio.ui-components.header
  (:require
   [mateuszmazurczak.portfolio.utils      :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.header :as sut]
   [portfolio.reagent-18                  :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Header"})

(defscene
 basic-header
 "Header with logo and navigation items."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "relative h-24 bg-background"}
   [sut/header-comp {:size :full
                     :sticky? false
                     :border? true
                     :logo [:span {:class "text-sm font-semibold"} "Brand"]
                     :right-section [:span {:class "text-xs text-muted-foreground"} "Right"]}
    {:title "Docs" :href "#"}
    {:title "Blog" :href "#"}
    {:title "Contact" :href "#"}]]))