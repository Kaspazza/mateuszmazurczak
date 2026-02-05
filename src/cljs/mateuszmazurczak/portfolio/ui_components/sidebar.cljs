(ns mateuszmazurczak.portfolio.ui-components.sidebar
  (:require
   ["lucide-react"                         :refer [Folder Kanban Settings Users]]
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.sidebar :as sut]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]]
   [reagent.core                           :as r]))

(configure-scenes {:collection :ui-components
                   :title "Sidebar"})

(defscene
 sidebar-demo
 "Basic sidebar layout with header, menu, and footer.

  Based on shadcn/ui Sidebar — https://ui.shadcn.com/docs/components/sidebar
  Custom component with composable slots.

  Use sidebar-content and sidebar-menu to structure navigation."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/sidebar {:open? true
                 :is-mobile false
                 :collapsible "none"
                 :variant "sidebar"}
    [sut/sidebar-header {}
     [:div {:class "text-sm font-semibold px-2"}
      "Workspace"]]
    [sut/sidebar-content {}
     [sut/sidebar-menu {}
      [sut/sidebar-menu-item {}
       [sut/sidebar-menu-button {:is-active? true}
        [:> Kanban]
        "Dashboard"]]
      [sut/sidebar-menu-item {}
       [sut/sidebar-menu-button {}
        [:> Folder]
        "Projects"]]
      [sut/sidebar-menu-item {}
       [sut/sidebar-menu-button {}
        [:> Users]
        "Team"]]
      [sut/sidebar-menu-item {}
       [sut/sidebar-menu-button {}
        [:> Settings]
        "Settings"]]]]
    [sut/sidebar-footer {}
     [:div {:class "text-xs text-muted-foreground px-2"}
      "v1.0"]]]]))

(defscene
 sidebar-menu
 "Menu items with actions and badges.

  Based on shadcn/ui Sidebar — https://ui.shadcn.com/docs/components/sidebar
  Custom component with optional menu actions.

  Use menu-action and menu-badge for per-item utilities."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/sidebar {:open? true
                 :is-mobile false
                 :collapsible "none"}
    [sut/sidebar-content {}
     [sut/sidebar-menu {}
      [sut/sidebar-menu-item {}
       [sut/sidebar-menu-button {:is-active? true}
        "Inbox"]
       [sut/sidebar-menu-badge {}
        "12"]
       [sut/sidebar-menu-action {:show-on-hover? true}
        "✎"]]
      [sut/sidebar-menu-item {}
       [sut/sidebar-menu-button {}
        "Notifications"]
       [sut/sidebar-menu-badge {}
        "3"]]
      [sut/sidebar-menu-item {}
       [sut/sidebar-menu-button {}
        "Archives"]]]]]]))

(defscene
 sidebar-menu-sub
 "Sidebar submenu with nested links.

  Based on shadcn/ui Sidebar — https://ui.shadcn.com/docs/components/sidebar
  Custom component with nested menu-sub elements.

  Use menu-sub for secondary navigation."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/sidebar {:open? true
                 :is-mobile false
                 :collapsible "none"}
    [sut/sidebar-content {}
     [sut/sidebar-menu {}
      [sut/sidebar-menu-item {}
       [sut/sidebar-menu-button {:is-active? true}
        "Projects"]
       [sut/sidebar-menu-sub {}
        [sut/sidebar-menu-sub-item {}
         [sut/sidebar-menu-sub-button {}
          "Roadmap"]]
        [sut/sidebar-menu-sub-item {}
         [sut/sidebar-menu-sub-button {}
          "Backlog"]]
        [sut/sidebar-menu-sub-item {}
         [sut/sidebar-menu-sub-button {}
          "Archive"]]]]
      [sut/sidebar-menu-item {}
       [sut/sidebar-menu-button {}
        "Analytics"]]]]]]))

(defscene
 sidebar-menu-collapsible
 "Collapsible sidebar (icon mode).

  Based on shadcn/ui Sidebar — https://ui.shadcn.com/docs/components/sidebar
  Custom component supports :collapsible 'icon'.

  When collapsed, icons remain visible while labels hide."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/sidebar {:open? false
                 :is-mobile false
                 :collapsible "icon"}
    [sut/sidebar-content {}
     [sut/sidebar-menu {}
      [sut/sidebar-menu-item {}
       [sut/sidebar-menu-button {:tooltip "Dashboard"}
        [:> Kanban]
        "Dashboard"]]
      [sut/sidebar-menu-item {}
       [sut/sidebar-menu-button {:tooltip "Projects"}
        [:> Folder]
        "Projects"]]
      [sut/sidebar-menu-item {}
       [sut/sidebar-menu-button {:tooltip "Team"}
        [:> Users]
        "Team"]]]]]]))

(defscene
 sidebar-controlled
 "Controlled open/close state.

  Based on shadcn/ui Sidebar — https://ui.shadcn.com/docs/components/sidebar
  Custom component expects open? and callbacks from the consumer.

  Use controlled state for responsive layouts."
 []
 (let [open? (r/atom true)]
   (fn []
     (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                         [:button {:class "mb-4 text-sm underline"
                                                   :on-click #(swap! open? not)}
                                          (if @open? "Collapse" "Expand")]
                                         [sut/sidebar {:open? @open?
                                                       :is-mobile false
                                                       :collapsible "offcanvas"}
                                          [sut/sidebar-content {}
                                           [sut/sidebar-menu {}
                                            [sut/sidebar-menu-item {}
                                             [sut/sidebar-menu-button {:is-active? true}
                                              "Overview"]]
                                            [sut/sidebar-menu-item {}
                                             [sut/sidebar-menu-button {}
                                              "Reports"]]]]]]))))

(defscene
 sidebar-mobile
 "Mobile sidebar rendered as a sheet.

  Based on shadcn/ui Sidebar — https://ui.shadcn.com/docs/components/sidebar
  Custom component toggles to a sheet when :is-mobile is true.

  Use for off-canvas mobile navigation."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/sidebar {:open? true
                                                   :is-mobile true
                                                   :collapsible "offcanvas"}
                                      [sut/sidebar-header {}
                                       [:div {:class "text-sm font-semibold px-2"}
                                        "Mobile Nav"]]
                                      [sut/sidebar-content {}
                                       [sut/sidebar-menu {}
                                        [sut/sidebar-menu-item {}
                                         [sut/sidebar-menu-button {:is-active? true}
                                          "Home"]]
                                        [sut/sidebar-menu-item {}
                                         [sut/sidebar-menu-button {}
                                          "Settings"]]]]]]))
