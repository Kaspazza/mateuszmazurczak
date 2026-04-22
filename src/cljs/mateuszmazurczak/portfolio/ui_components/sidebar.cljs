(ns mateuszmazurczak.portfolio.ui-components.sidebar
  (:require
   ["lucide-react"                         :refer [Folder Kanban Settings Users]]
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.sidebar :as sut]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]]
   [reagent.core                           :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Sidebar"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Pure presentational sidebar components."
            :npm-install "npm install @radix-ui/react-slot lucide-react"
            :source-code (embed-source "mateuszmazurczak.ui.components.sidebar")
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/sidebar.cljs"
            :filename "sidebar.cljs"}])

(defscene
 api-reference
 "Complete reference for all Sidebar component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Sidebar components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar"
       :description "Sidebar component"
       :props [{:name ":open?"         :type "boolean"  :default nil          :description "Expanded/open state"}
               {:name ":is-mobile"     :type "boolean"  :default nil          :description "Render mobile variant"}
               {:name ":on-open-change" :type "function" :default nil         :description "Callback (fn [open?])"}
               {:name ":side"          :type "string"   :default "\"left\""   :description "One of: 'left' | 'right'"}
               {:name ":variant"       :type "string"   :default "\"sidebar\"" :description "One of: 'sidebar' | 'floating' | 'inset'"}
               {:name ":collapsible"   :type "string"   :default "\"offcanvas\"" :description "One of: 'offcanvas' | 'icon' | 'none'"}
               {:name ":class"         :type "string"   :default nil          :description "Additional Tailwind classes"}
               {:name ":style"         :type "map"      :default nil          :description "Inline style map"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-trigger"
       :description "Sidebar trigger component"
       :props [{:name ":class"    :type "string"   :default nil :description "Additional Tailwind classes"}
               {:name ":on-click" :type "function" :default nil :description "Click handler (fn [event])"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-rail"
       :description "Sidebar rail component"
       :props [{:name ":class"    :type "string"   :default nil :description "Additional Tailwind classes"}
               {:name ":on-click" :type "function" :default nil :description "Click handler (fn [event])"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-inset"
       :description "Sidebar inset component"
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-input"
       :description "Sidebar input component"
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-header"
       :description "Sidebar header component"
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-footer"
       :description "Sidebar footer component"
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-separator"
       :description "Sidebar separator component"
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-content"
       :description "Sidebar content component"
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-group"
       :description "Sidebar group component"
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-group-label"
       :description "Sidebar group label component"
       :props [{:name ":class"    :type "string"  :default nil   :description "Additional Tailwind classes"}
               {:name ":as-child" :type "boolean" :default "false" :description "Render via Radix Slot"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-group-action"
       :description "Sidebar group action component"
       :props [{:name ":class"    :type "string"  :default nil   :description "Additional Tailwind classes"}
               {:name ":as-child" :type "boolean" :default "false" :description "Render via Radix Slot"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-group-content"
       :description "Sidebar group content component"
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-menu"
       :description "Sidebar menu component"
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-menu-item"
       :description "Sidebar menu item component"
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-menu-button"
       :description "Sidebar menu button component"
       :props [{:name ":class"      :type "string"          :default nil      :description "Additional Tailwind classes"}
               {:name ":as-child"   :type "boolean"         :default "false"  :description "Render via Radix Slot"}
               {:name ":is-active?" :type "boolean"         :default nil      :description "Active state"}
               {:name ":tooltip"    :type "string | hiccup" :default nil      :description "Tooltip content"}
               {:name ":variant"    :type "keyword"         :default ":default" :description "One of: :default | :outline"}
               {:name ":size"       :type "keyword"         :default ":default" :description "One of: :default | :sm | :lg"}
               {:name ":collapsed?" :type "boolean"         :default nil      :description "Whether sidebar is collapsed"}
               {:name ":is-mobile"  :type "boolean"         :default nil      :description "Render mobile variant"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-menu-action"
       :description "Sidebar menu action component"
       :props [{:name ":class"         :type "string"  :default nil    :description "Additional Tailwind classes"}
               {:name ":as-child"      :type "boolean" :default "false" :description "Render via Radix Slot"}
               {:name ":show-on-hover?" :type "boolean" :default "false" :description "Show action on hover"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-menu-badge"
       :description "Sidebar menu badge component"
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-menu-skeleton"
       :description "Sidebar menu skeleton component"
       :props [{:name ":class"      :type "string"  :default nil    :description "Additional Tailwind classes"}
               {:name ":show-icon?" :type "boolean" :default "false" :description "Show icon skeleton"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-menu-sub"
       :description "Sidebar menu sub component"
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-menu-sub-item"
       :description "Sidebar menu sub item component"
       :props [{:name ":class" :type "string" :default nil :description "Additional Tailwind classes"}]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "sidebar-menu-sub-button"
       :description "Sidebar menu sub button component"
       :props [{:name ":class"      :type "string"  :default nil    :description "Additional Tailwind classes"}
               {:name ":as-child"   :type "boolean" :default "false" :description "Render via Radix Slot"}
               {:name ":size"       :type "keyword" :default ":md"  :description "One of: :sm | :md"}
               {:name ":is-active?" :type "boolean" :default nil    :description "Active state"}]}]
     [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
      [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
      [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
       [:li "Sidebar behavior depends on provider/root composition; keep sidebar-provider at layout root."]
       [:li "Use :collapsible and :variant together intentionally—they affect both layout and interaction patterns."]
       [:li "For mobile flows, test with :is-mobile true and off-canvas variants to avoid inaccessible navigation states."]]]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[sidebar-provider {}\n [sidebar {:variant :inset}\n  [sidebar-header {}]\n  [sidebar-content {}]\n  [sidebar-footer {}]]\n [sidebar-inset {} [:main {:class \"p-4\"} \"Content\"]]]" ]]]]]]))

(defscene
 sidebar-demo
 "Basic sidebar layout with header, menu, and footer.

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
