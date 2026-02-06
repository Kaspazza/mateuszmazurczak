(ns mateuszmazurczak.ui.components.image
  "Optimized image components with lazy loading, responsive images, and modern formats.
  
  Best practices:
  - Lazy loading for below-fold images
  - Explicit width/height to prevent layout shift (CLS)
  - Modern formats (WebP/AVIF) with fallback
  - Responsive images for different screen sizes
  - Loading states
  - Alt text for accessibility

Version: 1.0.0
Last updated: 2026-02-06

Custom component implementation."
  (:require
   [mateuszmazurczak.utils.styles :refer [merge-classes]]
   [reagent.core                  :as r]))

(defn optimized-img
  "Optimized image component with best practices.
  
  Props:
  - `:src` - Image source URL (required)
  - `:alt` - Alt text for accessibility (required)
  - `:width` - Explicit width in pixels (required for CLS prevention)
  - `:height` - Explicit height in pixels (required for CLS prevention)
  - `:loading` - Loading strategy: 'lazy' | 'eager' (default: 'lazy')
  - `:fetchpriority` - Priority hint: 'high' | 'low' | 'auto' (default: 'auto')
  - `:class` - Additional CSS classes
  - `:on-load` - Callback when image loads
  - `:on-error` - Callback when image fails to load
  
  Example (hero image):
  [optimized-img {:src \"/img/hero.webp\"
                  :alt \"Hero image\"
                  :width 800
                  :height 600
                  :loading \"eager\"
                  :fetchpriority \"high\"}]
  
  Example (below-fold image):
  [optimized-img {:src \"/img/article.webp\"
                  :alt \"Article thumbnail\"
                  :width 400
                  :height 300
                  :loading \"lazy\"}]"
  [{:keys [src alt width height loading fetchpriority class on-load on-error]}]
  [:img
   (merge {:src src
           :alt alt
           :width width
           :height height
           :loading (or loading "lazy")
           :decoding "async"
           :class class}
          (when fetchpriority {:fetchpriority fetchpriority})
          (when on-load {:on-load on-load})
          (when on-error {:on-error on-error}))])

(defn progressive-img
  "Image with blur-up/LQIP (Low Quality Image Placeholder) effect.
  Shows a blurred placeholder while the full image loads.
  
  Props:
  - `:src` - Full quality image URL (required)
  - `:placeholder` - Low quality placeholder URL (required, use tiny ~1-5KB image)
  - `:alt` - Alt text for accessibility (required)
  - `:width` - Explicit width in pixels (required)
  - `:height` - Explicit height in pixels (required)
  - `:loading` - Loading strategy: 'lazy' | 'eager' (default: 'lazy')
  - `:class` - Additional CSS classes for container
  - `:img-class` - Additional CSS classes for image
  
  Example:
  [progressive-img {:src \"/img/hero.webp\"
                    :placeholder \"/img/hero-tiny.webp\"
                    :alt \"Hero\"
                    :width 800
                    :height 600}]"
  [_props]
  (let [loaded? (r/atom false)]
    (fn [{:keys [src placeholder alt width height loading class img-class]}]
      [:div {:class (merge-classes "relative overflow-hidden" class)
             :style {:width (str width "px")
                     :height (str height "px")}}
       ;; Placeholder (blurred)
       [:img {:src placeholder
              :alt ""
              :aria-hidden true
              :width width
              :height height
              :class (merge-classes
                      "absolute inset-0 w-full h-full object-cover transition-opacity duration-300"
                      (when @loaded? "opacity-0")
                      img-class)
              :style {:filter "blur(10px)"
                      :transform "scale(1.1)"}}] ; Scale to hide blur edges
       ;; Full quality image
       [:img {:src src
              :alt alt
              :width width
              :height height
              :loading (or loading "lazy")
              :decoding "async"
              :class (merge-classes
                      "absolute inset-0 w-full h-full object-cover transition-opacity duration-300"
                      (when-not @loaded? "opacity-0")
                      img-class)
              :on-load #(reset! loaded? true)}]])))

(defn responsive-img
  "Responsive image using picture element with multiple sources.
  Serves different image sizes based on viewport width and pixel density.
  
  Props:
  - `:sources` - Vector of source maps with :srcset, :media, :type
  - `:src` - Fallback image URL (required)
  - `:alt` - Alt text for accessibility (required)
  - `:width` - Explicit width in pixels (required)
  - `:height` - Explicit height in pixels (required)
  - `:loading` - Loading strategy: 'lazy' | 'eager' (default: 'lazy')
  - `:class` - Additional CSS classes
  
  Example:
  [responsive-img {:sources [{:srcset \"/img/hero-small.webp 400w, /img/hero-medium.webp 800w\"
                              :media \"(max-width: 768px)\"
                              :type \"image/webp\"}
                             {:srcset \"/img/hero-large.webp 1200w, /img/hero-xlarge.webp 1600w\"
                              :type \"image/webp\"}]
                   :src \"/img/hero.jpg\"
                   :alt \"Hero\"
                   :width 1200
                   :height 800}]"
  [{:keys [sources src alt width height loading class]}]
  (into [:picture
         (for [{:keys [srcset media type]} sources]
           ^{:key srcset}
           [:source (merge {:srcset srcset} (when media {:media media}) (when type {:type type}))])]
        [[:img {:src src
                :alt alt
                :width width
                :height height
                :loading (or loading "lazy")
                :decoding "async"
                :class class}]]))

(defn avatar-img
  "Optimized avatar image with proper loading and fallback.
  
  Props:
  - `:src` - Image source URL (required)
  - `:alt` - Alt text for accessibility (required)
  - `:size` - Size in pixels (default: 40)
  - `:class` - Additional CSS classes
  
  Example:
  [avatar-img {:src \"/img/user.webp\" :alt \"User\" :size 48}]"
  [{:keys [src alt size class]
    :or {size 40}}]
  [:img {:src src
         :alt alt
         :width size
         :height size
         :loading "lazy"
         :decoding "async"
         :class (merge-classes "rounded-full object-cover" class)}])
