(ns mateuszmazurczak.portfolio.ui-components.skeleton
  (:require
   [mateuszmazurczak.portfolio.utils       :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.skeleton :as sut]
   [portfolio.reagent-18                   :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Skeleton"})

(defscene
 skeleton-layout
 "Skeleton placeholders for loading states." 
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "flex items-center gap-4 p-6"}
   [sut/skeleton {:class "h-12 w-12 rounded-full"}]
   [:div {:class "space-y-2"}
    [sut/skeleton {:class "h-4 w-64"}]
    [sut/skeleton {:class "h-4 w-48"}]]]))