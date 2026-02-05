(ns mateuszmazurczak.portfolio.ui-components.carousel
  (:require
   [mateuszmazurczak.portfolio.utils        :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.carousel :as sut]
   [portfolio.reagent-18                    :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Carousel"})

(defscene
 basic-carousel
 "Carousel with navigation buttons."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6 max-w-lg"}
   [sut/carousel {:opts {:loop true}}
    [sut/carousel-content {}
     (for [idx (range 1 4)]
       ^{:key idx}
       [sut/carousel-item {}
        [:div {:class "flex h-40 items-center justify-center rounded-lg border bg-muted"}
         (str "Slide " idx)]])]
    [sut/carousel-previous {}]
    [sut/carousel-next {}]]]))