(ns mateuszmazurczak.portfolio.ui-components.image
  (:require
   [mateuszmazurczak.portfolio.utils     :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.image :as sut]
   [portfolio.reagent-18                 :refer-macros [defscene configure-scenes]])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Image"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description
            "Optimized image components with lazy loading, responsive images, and modern formats."
            :npm-install "No external dependencies"
            :source-code (embed-source mateuszmazurczak.ui.components.image)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/image.cljs"
            :filename "image.cljs"}])

(defscene
 api-reference
 "Complete reference for all Image component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Image components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card {:component-name "optimized-img"
                                             :description "Optimized img component"
                                             :props
                                             [[":src" "any, optional - Component prop"]
                                              [":alt" "any, optional - Component prop"]
                                              [":width" "any, optional - Component prop"]
                                              [":height" "any, optional - Component prop"]
                                              [":loading" "any, optional - Component prop"]
                                              [":fetchpriority" "any, optional - Component prop"]
                                              [":class" "any, optional - Component prop"]
                                              [":on-load" "any, optional - Component prop"]
                                              [":on-error" "any, optional - Component prop"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "progressive-img"
                                             :description "Progressive img component"
                                             :props
                                             [[":src" "any, optional - Component prop"]
                                              [":placeholder" "any, optional - Component prop"]
                                              [":alt" "any, optional - Component prop"]
                                              [":width" "any, optional - Component prop"]
                                              [":height" "any, optional - Component prop"]
                                              [":loading" "any, optional - Component prop"]
                                              [":class" "any, optional - Component prop"]
                                              [":img-class" "any, optional - Component prop"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "responsive-img"
                                             :description "Responsive img component"
                                             :props [[":sources" "any, optional - Component prop"]
                                                     [":src" "any, optional - Component prop"]
                                                     [":alt" "any, optional - Component prop"]
                                                     [":width" "any, optional - Component prop"]
                                                     [":height" "any, optional - Component prop"]
                                                     [":loading" "any, optional - Component prop"]
                                                     [":class" "any, optional - Component prop"]]}]
     [mm-portfolio-utils/api-component-card {:component-name "avatar-img"
                                             :description "Avatar img component"
                                             :props [[":src" "any, optional - Component prop"]
                                                     [":alt" "any, optional - Component prop"]
                                                     [":size" "any, optional - Component prop"]
                                                     [":class" "any, optional - Component prop"]]}]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[optimized-img {}]"]]]]]]))

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