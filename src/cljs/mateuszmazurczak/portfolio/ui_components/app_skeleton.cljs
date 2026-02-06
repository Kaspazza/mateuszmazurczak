(ns mateuszmazurczak.portfolio.ui-components.app-skeleton
  (:require
   [mateuszmazurczak.portfolio.utils            :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.app-skeleton :as sut]
   [portfolio.reagent-18                        :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "App Skeleton"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "App initialization loading screen component."
            :npm-install "No external dependencies"
            :source-code (embed-source mateuszmazurczak.ui.components.app_skeleton)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/app_skeleton.cljs"
            :filename "app_skeleton.cljs"}])

(defscene api-reference
          "Complete reference for all App Skeleton component props and usage patterns."
          []
          (mm-portfolio-utils/wrap-component
           [:div {:class "p-6 max-w-4xl"}
            [:div {:class "space-y-6"}
             [:div
              [:p {:class "text-sm text-muted-foreground"}
               "All available props for App Skeleton components."]]
             [:div {:class "space-y-4"}
              [mm-portfolio-utils/api-component-card {:component-name "navigation-skeleton"
                                                      :description "Navigation skeleton component"
                                                      :props []}]
              [mm-portfolio-utils/api-component-card {:component-name "spacer"
                                                      :description "Spacer component"
                                                      :props []}]
              [mm-portfolio-utils/api-component-card {:component-name "sidebar-user-bottom-profile"
                                                      :description
                                                      "Sidebar user bottom profile component"
                                                      :props []}]
              [mm-portfolio-utils/api-component-card {:component-name "sidebar-header"
                                                      :description "Sidebar header component"
                                                      :props []}]
              [mm-portfolio-utils/api-component-card {:component-name "sidebar-skeleton"
                                                      :description "Sidebar skeleton component"
                                                      :props []}]
              [mm-portfolio-utils/api-component-card {:component-name "header"
                                                      :description "Header component"
                                                      :props []}]
              [mm-portfolio-utils/api-component-card {:component-name "page-content"
                                                      :description "Page content component"
                                                      :props []}]
              [mm-portfolio-utils/api-component-card {:component-name "main-content-skeleton"
                                                      :description "Main content skeleton component"
                                                      :props []}]
              [mm-portfolio-utils/api-component-card {:component-name "app-loading"
                                                      :description "App loading component"
                                                      :props []}]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "[navigation-skeleton {}]"]]]]]]))

(defscene
 app-loading
 "Full app loading skeleton - displays during initialization.
   
   Shows a complete skeleton layout matching the main app structure:
   - Sidebar with navigation items
   - Header with breadcrumbs
   - Page content with title and cards"
 []
 (mm-portfolio-utils/wrap-component [sut/app-loading]))

(defscene
 sidebar-only
 "Sidebar skeleton component only.
   
   Useful for testing or when you only need the sidebar loading state."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "flex h-screen bg-background"}
                                     [sut/sidebar-skeleton]]))

(defscene
 main-content-only
 "Main content skeleton (header + page content).
   
   Useful for layouts without a sidebar."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "flex h-screen bg-background"}
                                     [sut/main-content-skeleton]]))
