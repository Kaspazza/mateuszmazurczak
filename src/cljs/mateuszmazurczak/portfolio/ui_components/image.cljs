(ns mateuszmazurczak.portfolio.ui-components.image
  (:require
   [mateuszmazurczak.portfolio.utils     :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.image :as sut]
   [portfolio.reagent-18                 :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Image"})

(defscene
 image-variants
 "Optimized image variants." 
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "flex flex-col gap-6 p-6"}
   [sut/optimized-img {:src "https://placehold.co/320x200/png"
                       :alt "Placeholder landscape"
                       :width 320
                       :height 200
                       :class "rounded-lg border"}]
   [sut/avatar-img {:src "https://placehold.co/96x96/png"
                    :alt "Avatar"
                    :size 64}]]))