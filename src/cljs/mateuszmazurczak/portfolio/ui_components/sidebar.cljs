(ns mateuszmazurczak.portfolio.ui-components.sidebar
  (:require
   [mateuszmazurczak.portfolio.utils     :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.sidebar :as sut]
   [portfolio.reagent-18                 :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Sidebar"})

(defscene
 basic-sidebar
 "Sidebar layout with header, menu, and footer." 
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/sidebar {:open? true
                 :is-mobile false
                 :collapsible "none"
                 :variant "sidebar"}
    [sut/sidebar-header {}
     [:div {:class "text-sm font-semibold px-2"} "Workspace"]]
    [sut/sidebar-content {}
     [sut/sidebar-menu {}
      [sut/sidebar-menu-item {}
       [sut/sidebar-menu-button {:is-active? true} "Dashboard"]]
      [sut/sidebar-menu-item {}
       [sut/sidebar-menu-button {} "Projects"]]
      [sut/sidebar-menu-item {}
       [sut/sidebar-menu-button {} "Settings"]]]]
    [sut/sidebar-footer {}
     [:div {:class "text-xs text-muted-foreground px-2"} "v1.0"]]]]))