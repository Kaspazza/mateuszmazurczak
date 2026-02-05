(ns mateuszmazurczak.portfolio.ui-components.image
  (:require
   [mateuszmazurczak.portfolio.utils     :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.image :as sut]
   [portfolio.reagent-18                 :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Image"})

(defscene
 optimized-image
 "Optimized image with explicit sizing.

  Custom component — not from shadcn/ui.
  Encourages explicit width/height to prevent layout shift.

  Use for hero and content images."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/optimized-img {:src "https://placehold.co/320x200/png"
                                                         :alt "Placeholder landscape"
                                                         :width 320
                                                         :height 200
                                                         :class "rounded-lg border"}]]))

(defscene
 avatar-image
 "Avatar image helper.

  Custom component — not from shadcn/ui.
  Use for small circular user images."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6 flex items-center gap-4"}
                                     [sut/avatar-img {:src "https://placehold.co/96x96/png"
                                                      :alt "Avatar"
                                                      :size 40}]
                                     [sut/avatar-img {:src "https://placehold.co/96x96/png"
                                                      :alt "Avatar"
                                                      :size 64}]
                                     [sut/avatar-img {:src "https://placehold.co/96x96/png"
                                                      :alt "Avatar"
                                                      :size 80}]]))

(defscene
 responsive-image
 "Responsive image with multiple sources.

  Custom component — not from shadcn/ui.
  Use picture sources for multiple sizes and formats."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/responsive-img
    {:sources [{:srcset
                "https://placehold.co/320x200/webp 320w, https://placehold.co/640x400/webp 640w"
                :media "(max-width: 768px)"
                :type "image/webp"}]
     :src "https://placehold.co/640x400/png"
     :alt "Responsive"
     :width 640
     :height 400
     :class "rounded-lg border"}]]))

(defscene
 progressive-image
 "Progressive image with placeholder.

  Custom component — not from shadcn/ui.
  Uses a blurred placeholder until load completes."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/progressive-img {:src "https://placehold.co/320x200/png"
                                                           :placeholder
                                                           "https://placehold.co/32x20/png"
                                                           :alt "Progressive"
                                                           :width 320
                                                           :height 200
                                                           :class "rounded-lg border"}]]))