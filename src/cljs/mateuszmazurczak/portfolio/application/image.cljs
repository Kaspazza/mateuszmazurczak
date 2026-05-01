(ns mateuszmazurczak.portfolio.application.image
  (:require
   [mateuszmazurczak.portfolio.utils     :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.image :as sut]
   [portfolio.reagent-18                 :refer-macros [defscene configure-scenes]]
   [reagent.core                         :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :application
                   :title "Image"})

(defscene installation
  "Install dependencies and copy the component code into your project."
  []
  [mm-portfolio-utils/installation-scene
   {:description
    "Optimized image components with lazy loading, responsive images, and modern formats."
    :npm-install "No external dependencies"
    :source-code (embed-source "mateuszmazurczak.ui.components.image")
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
      [mm-portfolio-utils/api-component-card
       {:component-name "optimized-img"
        :description "Optimized img component"
        :props [{:name ":src"           :type "string"   :default nil      :description "Image source URL"}
                {:name ":alt"           :type "string"   :default nil      :description "Alt text for accessibility"}
                {:name ":width"         :type "number"   :default nil      :description "Explicit image width in pixels"}
                {:name ":height"        :type "number"   :default nil      :description "Explicit image height in pixels"}
                {:name ":loading"       :type "string"   :default "'lazy'" :description "One of: 'lazy' | 'eager'"}
                {:name ":fetchpriority" :type "string"   :default "'auto'" :description "One of: 'high' | 'low' | 'auto'"}
                {:name ":class"         :type "string"   :default nil      :description "Additional CSS classes"}
                {:name ":on-load"       :type "function" :default nil      :description "Callback on successful image load"}
                {:name ":on-error"      :type "function" :default nil      :description "Callback on image load error"}]}]
      [mm-portfolio-utils/api-component-card
       {:component-name "progressive-img"
        :description "Progressive img component"
        :props [{:name ":src"         :type "string" :default nil      :description "Image source URL"}
                {:name ":placeholder" :type "string" :default nil      :description "Placeholder image URL"}
                {:name ":alt"         :type "string" :default nil      :description "Alt text for accessibility"}
                {:name ":width"       :type "number" :default nil      :description "Explicit image width in pixels"}
                {:name ":height"      :type "number" :default nil      :description "Explicit image height in pixels"}
                {:name ":loading"     :type "string" :default "'lazy'" :description "One of: 'lazy' | 'eager'"}
                {:name ":class"       :type "string" :default nil      :description "Additional CSS classes"}
                {:name ":img-class"   :type "string" :default nil      :description "Additional CSS classes for img element"}]}]
      [mm-portfolio-utils/api-component-card
       {:component-name "responsive-img"
        :description "Responsive img component"
        :props [{:name ":sources" :type "vector<map>" :default nil      :description "Picture sources with :srcset and optional :media/:type"}
                {:name ":src"     :type "string"      :default nil      :description "Image source URL"}
                {:name ":alt"     :type "string"      :default nil      :description "Alt text for accessibility"}
                {:name ":width"   :type "number"      :default nil      :description "Explicit image width in pixels"}
                {:name ":height"  :type "number"      :default nil      :description "Explicit image height in pixels"}
                {:name ":loading" :type "string"      :default "'lazy'" :description "One of: 'lazy' | 'eager'"}
                {:name ":class"   :type "string"      :default nil      :description "Additional CSS classes"}]}]
      [mm-portfolio-utils/api-component-card
       {:component-name "avatar-img"
        :description "Avatar image helper with fixed square size and rounded styling."
        :props [{:name ":src"   :type "string" :default nil  :description "Image source URL"}
                {:name ":alt"   :type "string" :default nil  :description "Alt text for accessibility"}
                {:name ":size"  :type "number" :default "40" :description "Avatar size in pixels"}
                {:name ":class" :type "string" :default nil  :description "Additional CSS classes"}]}]
      [:div {:class "border rounded-lg p-4 bg-amber-500/10 border-amber-500/30 mb-4"}
       [:h4 {:class "text-sm font-semibold mb-2"} "⚠️ Important Notes"]
       [:ul {:class "text-xs text-muted-foreground space-y-1 list-disc pl-4"}
        [:li "optimized-img expects explicit :width and :height to prevent layout shift."]
        [:li "Use :on-error to capture failed loads and render fallback UI when needed."]
        [:li "responsive-img sources should be ordered from most specific to least specific."]]]
      [:div {:class "border rounded-lg p-4 bg-muted/50"}
       [:h4 {:class "text-sm font-semibold mb-2"}
        "Usage Example"]
       [:pre {:class "text-xs overflow-x-auto"}
        [:code "[optimized-img {:src \"https://placehold.co/640x360/png\"\n                :alt \"Demo banner\"\n                :width 640\n                :height 360\n                :class \"rounded-md border\"}]"]]]]]]))

(defscene
  optimized-image
  "Optimized image with explicit sizing.
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

(defscene
  image-error-state
  "Image load error handling.
  Demonstrates :on-error callback and fallback messaging."
  []
  (let [failed? (r/atom false)]
    (fn []
      (mm-portfolio-utils/wrap-component
       [:div {:class "p-6 space-y-2"}
        [sut/optimized-img {:src "https://invalid.example.com/missing-image.png"
                            :alt "Broken image demo"
                            :width 320
                            :height 200
                            :class "rounded-lg border"
                            :on-error #(reset! failed? true)}]
        (when @failed?
          [:p {:class "text-sm text-destructive"}
           "Image failed to load. Showing fallback copy."])]))))
