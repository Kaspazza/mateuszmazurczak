(ns mateuszmazurczak.ui.components.carousel
  "Carousel component built on top of Embla Carousel.
  https://www.embla-carousel.com/

Version: 1.0.0
Last updated: 2026-02-06

Custom component implementation."
  (:require
   ["embla-carousel-react"                :as embla-carousel]
   ["lucide-react"                        :refer [ArrowLeft ArrowRight]]
   [goog.object                           :as gobj]
   [mateuszmazurczak.ui.components.button :as mateuszmazurczak-button]
   [mateuszmazurczak.utils.styles         :refer [merge-classes]]
   [reagent.core                          :as    r
                                          :refer [defc]]
   [reagent.hooks                         :as rhooks]))

(def ^:private carousel-context (r/atom nil))

(defn- use-carousel
  "Hook to access carousel context. Throws if used outside of a Carousel component."
  []
  (when-not @carousel-context (throw (js/Error. "useCarousel must be used within a <Carousel />")))
  @carousel-context)

(defc carousel
 "Carousel root component. Creates a carousel container with embla-carousel.
  
  Props:
  - `:opts` - Embla carousel options map (see https://www.embla-carousel.com/api/options/)
  - `:plugins` - Array of Embla carousel plugins
  - `:orientation` - Carousel orientation: `:horizontal` (default) or `:vertical`
  - `:set-api` - Callback to receive the carousel API: (fn [api] ...)
  - `:class` - Additional Tailwind classes
  
  Example:
  [carousel {:opts {:align \"start\" :loop true}}
    [carousel-content {}
      [carousel-item {} \"Slide 1\"]
      [carousel-item {} \"Slide 2\"]
      [carousel-item {} \"Slide 3\"]]
    [carousel-previous {}]
    [carousel-next {}]]"
 [{:keys [orientation opts set-api plugins class]
   :or {orientation :horizontal}}
  &
  children]
 (let [use-embla-carousel (or (gobj/get embla-carousel "default") embla-carousel)
       embla-opts (clj->js (assoc opts :axis (if (= orientation :horizontal) "x" "y")))
       embla-plugins (clj->js (or plugins []))
       [carousel-ref api] (use-embla-carousel embla-opts embla-plugins)
       [can-scroll-prev set-can-scroll-prev] (rhooks/use-state false)
       [can-scroll-next set-can-scroll-next] (rhooks/use-state false)
       on-select (rhooks/use-callback (fn [embla-api]
                                        (when embla-api
                                          (set-can-scroll-prev (.canScrollPrev embla-api))
                                          (set-can-scroll-next (.canScrollNext embla-api))))
                                      [])
       scroll-prev (rhooks/use-callback (fn [] (when api (.scrollPrev api))) [api])
       scroll-next (rhooks/use-callback (fn [] (when api (.scrollNext api))) [api])
       handle-key-down
       (rhooks/use-callback
        (fn [event]
          (when (= (.-key event) "ArrowLeft") (.preventDefault event) (scroll-prev))
          (when (= (.-key event) "ArrowRight") (.preventDefault event) (scroll-next)))
        [scroll-prev scroll-next])]
   ;; Set API callback
   (rhooks/use-effect (fn [] (when (and api set-api) (set-api api))) [api set-api])
   ;; Update on select
   (rhooks/use-effect (fn []
                        (when api
                          (on-select api)
                          (.on api "reInit" on-select)
                          (.on api "select" on-select)
                          ;; Cleanup function
                          (fn [] (.off api "select" on-select))))
                      [api on-select])
   ;; Update context
   (reset! carousel-context {:carousel-ref carousel-ref
                             :api api
                             :opts opts
                             :orientation (or orientation
                                              (if (= (:axis opts) "y") :vertical :horizontal))
                             :scroll-prev scroll-prev
                             :scroll-next scroll-next
                             :can-scroll-prev can-scroll-prev
                             :can-scroll-next can-scroll-next})
   (into [:div {:on-key-down-capture handle-key-down
                :class (merge-classes "relative" class)
                :role "region"
                :aria-roledescription "carousel"
                :data-slot "carousel"}]
         children)))
(defn carousel-content
  "Carousel content wrapper. Contains the carousel items.
  
  Props:
  - `:class` - Additional Tailwind classes
  
  Example:
  [carousel-content {}
    [carousel-item {} \"Slide 1\"]
    [carousel-item {} \"Slide 2\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (let [{:keys [carousel-ref orientation]} (use-carousel)
        normalized-children
        (mapcat (fn [child]
                  (if (and (sequential? child) (sequential? (first child))) child [child]))
         children)]
    [:div {:ref carousel-ref
           :class "overflow-hidden"
           :data-slot "carousel-content"}
     (into [:div
            (-> (dissoc props :class)
                (assoc :class (merge-classes
                               "flex"
                               (if (= orientation :horizontal) "-ml-4" "-mt-4 flex-col")
                               class)
                       :data-slot "carousel-content-inner"))]
           normalized-children)]))

(defn carousel-item
  "Carousel item. Individual slide in the carousel.
  
  Props:
  - `:class` - Additional Tailwind classes
  
  Example:
  [carousel-item {}
    [:div \"Slide content\"]]"
  [{:keys [class]
    :as props}
   &
   children]
  (let [{:keys [orientation]} (use-carousel)]
    (into [:div
           (-> (dissoc props :class)
               (assoc :role "group"
                      :aria-roledescription "slide"
                      :data-slot "carousel-item"
                      :class (merge-classes "min-w-0 shrink-0 grow-0 basis-full"
                                            (if (= orientation :horizontal) "pl-4" "pt-4")
                                            class)))]
          children)))

(defn carousel-previous
  "Carousel previous button. Navigates to the previous slide.
  
  Props:
  - `:variant` - Button variant (default: `:outline`)
  - `:size` - Button size (default: `:icon`)
  - `:class` - Additional Tailwind classes
  
  Example:
  [carousel-previous {}]"
  [{:keys [variant size class]
    :or {variant :outline
         size :icon}
    :as props}]
  (let [{:keys [orientation scroll-prev can-scroll-prev]} (use-carousel)]
    [mateuszmazurczak-button/button
     (-> (dissoc props :class)
         (assoc :variant variant
                :size size
                :class (merge-classes "absolute size-8 rounded-full"
                                      (if (= orientation :horizontal)
                                        "top-1/2 -left-12 -translate-y-1/2"
                                        "-top-12 left-1/2 -translate-x-1/2 rotate-90")
                                      class)
                :disabled (not can-scroll-prev)
                :on-click scroll-prev
                :data-slot "carousel-previous"))
     [:> ArrowLeft]
     [:span {:class "sr-only"}
      "Previous slide"]]))

(defn carousel-next
  "Carousel next button. Navigates to the next slide.
  
  Props:
  - `:variant` - Button variant (default: `:outline`)
  - `:size` - Button size (default: `:icon`)
  - `:class` - Additional Tailwind classes
  
  Example:
  [carousel-next {}]"
  [{:keys [variant size class]
    :or {variant :outline
         size :icon}
    :as props}]
  (let [{:keys [orientation scroll-next can-scroll-next]} (use-carousel)]
    [mateuszmazurczak-button/button
     (-> (dissoc props :class)
         (assoc :variant variant
                :size size
                :class (merge-classes "absolute size-8 rounded-full"
                                      (if (= orientation :horizontal)
                                        "top-1/2 -right-12 -translate-y-1/2"
                                        "-bottom-12 left-1/2 -translate-x-1/2 rotate-90")
                                      class)
                :disabled (not can-scroll-next)
                :on-click scroll-next
                :data-slot "carousel-next"))
     [:> ArrowRight]
     [:span {:class "sr-only"}
      "Next slide"]]))
