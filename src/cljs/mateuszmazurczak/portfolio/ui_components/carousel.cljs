(ns mateuszmazurczak.portfolio.ui-components.carousel
  (:require
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.carousel :as sut]
   [portfolio.reagent-18                    :refer-macros [defscene configure-scenes]]
   [reagent.core                            :as r])
  (:require-macros [mateuszmazurczak.portfolio.macros :refer [embed-source]]))

(configure-scenes {:collection :ui-components
                   :title "Carousel"})

(defscene installation
          "Install dependencies and copy the component code into your project."
          []
          [mm-portfolio-utils/installation-scene
           {:description "Carousel component built on top of Embla Carousel."
            :npm-install "npm install embla-carousel-react lucide-react"
            :source-code (embed-source mateuszmazurczak.ui.components.carousel)
            :namespace-path "src/cljs/mateuszmazurczak/ui/components/carousel.cljs"
            :filename "carousel.cljs"}])

(defscene
 api-reference
 "Complete reference for all Carousel component props and usage patterns."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-4xl"}
   [:div {:class "space-y-6"}
    [:div
     [:p {:class "text-sm text-muted-foreground"}
      "All available props for Carousel components."]]
    [:div {:class "space-y-4"}
     [mm-portfolio-utils/api-component-card
      {:component-name "carousel-content"
       :description "Carousel content component"
       :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "carousel-item"
       :description "Carousel item component"
       :props [[":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "carousel-previous"
       :description "Carousel previous component"
       :props
       [[":variant"
         "keyword, optional (default :outline). One of: :default | :destructive | :outline | :secondary | :ghost | :link"]
        [":size" "keyword, optional (default :icon). One of: :default | :sm | :lg | :icon"]
        [":class" "string, optional - Additional Tailwind classes"]]}]
     [mm-portfolio-utils/api-component-card
      {:component-name "carousel-next"
       :description "Carousel next component"
       :props
       [[":variant"
         "keyword, optional (default :outline). One of: :default | :destructive | :outline | :secondary | :ghost | :link"]
        [":size" "keyword, optional (default :icon). One of: :default | :sm | :lg | :icon"]
        [":class" "string, optional - Additional Tailwind classes"]]}]
     [:div {:class "border rounded-lg p-4 bg-muted/50"}
      [:h4 {:class "text-sm font-semibold mb-2"}
       "Usage Example"]
      [:pre {:class "text-xs overflow-x-auto"}
       [:code "[carousel-content {}]"]]]]]]))

(defn- slide-card
  [label]
  [:div {:class "p-1"}
   [:div {:class "flex aspect-square items-center justify-center rounded-lg border bg-card"}
    [:span {:class "text-3xl font-semibold"}
     label]]])

(defscene
 carousel-demo
 "Basic carousel with previous/next controls.

  Based on shadcn/ui Carousel — https://ui.shadcn.com/docs/components/carousel
  Library: embla-carousel

  Use for showcasing images or featured content."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/carousel {:class "w-full max-w-xs"}
                                      [sut/carousel-content {}
                                       (for [idx (range 1 6)]
                                         ^{:key idx}
                                         [sut/carousel-item {}
                                          [slide-card idx]])]
                                      [sut/carousel-previous {}]
                                      [sut/carousel-next {}]]]))

(defscene
 carousel-size
 "Carousel with responsive item sizes.

  Based on shadcn/ui Carousel — https://ui.shadcn.com/docs/components/carousel
  Library: embla-carousel

  Use :class on items to control basis for breakpoints."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/carousel {:opts {:align "start"}
                                                    :class "w-full max-w-sm"}
                                      [sut/carousel-content {}
                                       (for [idx (range 1 6)]
                                         ^{:key idx}
                                         [sut/carousel-item {:class "md:basis-1/2 lg:basis-1/3"}
                                          [slide-card idx]])]
                                      [sut/carousel-previous {}]
                                      [sut/carousel-next {}]]]))

(defscene
 carousel-orientation
 "Vertical carousel orientation.

  Based on shadcn/ui Carousel — https://ui.shadcn.com/docs/components/carousel
  Library: embla-carousel

  Use :orientation :vertical for stacked slides."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/carousel {:opts {:align "start"}
                                                    :orientation :vertical
                                                    :class "w-full max-w-xs"}
                                      [sut/carousel-content {:class "-mt-1 h-[200px]"}
                                       (for [idx (range 1 6)]
                                         ^{:key idx}
                                         [sut/carousel-item {:class "pt-1 md:basis-1/2"}
                                          [slide-card idx]])]
                                      [sut/carousel-previous {}]
                                      [sut/carousel-next {}]]]))

(defscene
 carousel-spacing
 "Carousel with custom spacing.

  Based on shadcn/ui Carousel — https://ui.shadcn.com/docs/components/carousel
  Library: embla-carousel

  Adjust padding and negative margins to tune spacing."
 []
 (mm-portfolio-utils/wrap-component [:div {:class "p-6"}
                                     [sut/carousel {:class "w-full max-w-sm"}
                                      [sut/carousel-content {:class "-ml-1"}
                                       (for [idx (range 1 6)]
                                         ^{:key idx}
                                         [sut/carousel-item {:class
                                                             "pl-1 md:basis-1/2 lg:basis-1/3"}
                                          [slide-card idx]])]
                                      [sut/carousel-previous {}]
                                      [sut/carousel-next {}]]]))

(defscene
 carousel-api
 "Carousel API events and counters.

  Based on shadcn/ui Carousel — https://ui.shadcn.com/docs/components/carousel
  Library: embla-carousel

  Use :set-api to track current slide and total count."
 []
 (let [api (r/atom nil)
       current (r/atom 0)
       total (r/atom 0)]
   (fn []
     (mm-portfolio-utils/wrap-component
      [:div {:class "p-6"}
       [sut/carousel {:set-api
                      (fn [carousel-api]
                        (reset! api carousel-api)
                        (when carousel-api
                          (reset! total (.-length (.scrollSnapList carousel-api)))
                          (reset! current (inc (.selectedScrollSnap carousel-api)))
                          (.on carousel-api
                               "select"
                               (fn [] (reset! current (inc (.selectedScrollSnap carousel-api)))))))
                      :class "w-full max-w-xs"}
        [sut/carousel-content {}
         (for [idx (range 1 6)]
           ^{:key idx}
           [sut/carousel-item {}
            [slide-card idx]])]
        [sut/carousel-previous {}]
        [sut/carousel-next {}]]
       [:div {:class "text-muted-foreground py-2 text-center text-sm"}
        (str "Slide " @current " of " @total)]]))))