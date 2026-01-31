(ns mateuszmazurczak.portfolio.ui-components.app-skeleton
  (:require
   [mateuszmazurczak.portfolio.utils            :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.app-skeleton :as sut]
   [portfolio.reagent-18                        :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "App Skeleton"})

(defscene app-loading
  "Full app loading skeleton - displays during initialization.
   
   Shows a complete skeleton layout matching the main app structure:
   - Sidebar with navigation items
   - Header with breadcrumbs
   - Page content with title and cards"
  []
  (mm-portfolio-utils/wrap-component
   [sut/app-loading]))

(defscene sidebar-only
  "Sidebar skeleton component only.
   
   Useful for testing or when you only need the sidebar loading state."
  []
  (mm-portfolio-utils/wrap-component
   [:div {:class "flex h-screen bg-background"}
    [sut/sidebar-skeleton]]))

(defscene main-content-only
  "Main content skeleton (header + page content).
   
   Useful for layouts without a sidebar."
  []
  (mm-portfolio-utils/wrap-component
   [:div {:class "flex h-screen bg-background"}
    [sut/main-content-skeleton]]))
