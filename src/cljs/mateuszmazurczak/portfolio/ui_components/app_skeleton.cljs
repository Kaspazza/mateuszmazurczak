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
            :source-code (embed-source "mateuszmazurczak.ui.components.app_skeleton")
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
              [mm-portfolio-utils/api-component-card
               {:component-name "app-loading"
                :description "Public app bootstrap skeleton. Composes internal sidebar/header/content placeholders into a full-screen loading shell."
                :props [["arguments" "No props. Render as [app-loading]."]]}]
              [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
               [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
               [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
                [:li "Only app-loading should be treated as stable public API."]
                [:li "Other helper functions in this namespace are internal composition details and may change."]
                [:li "Use this during app initialization/loading only; replace with real layout after bootstrap."]]]
              [:div {:class "border rounded-lg p-4 bg-muted/50"}
               [:h4 {:class "text-sm font-semibold mb-2"}
                "Usage Example"]
               [:pre {:class "text-xs overflow-x-auto"}
                [:code "(if app-ready?\n  [main-layout]\n  [app-loading])"]]]]]]))

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
