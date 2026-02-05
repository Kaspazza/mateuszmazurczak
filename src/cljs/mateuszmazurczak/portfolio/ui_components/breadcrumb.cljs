(ns mateuszmazurczak.portfolio.ui-components.breadcrumb
  (:require
   [mateuszmazurczak.portfolio.utils           :as mm-portfolio-utils]
   [mateuszmazurczak.ui.components.breadcrumb  :as sut]
   [portfolio.reagent-18                       :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :ui-components
                   :title "Breadcrumb"})

(defscene
 basic-breadcrumb
 "Breadcrumb navigation with current page."
 []
 (mm-portfolio-utils/wrap-component
  [:div {:class "p-6"}
   [sut/breadcrumb {}
    [sut/breadcrumb-list {}
     [sut/breadcrumb-item {}
      [sut/breadcrumb-link {:href "#"} "Home"]]
     [sut/breadcrumb-separator {}]
     [sut/breadcrumb-item {}
      [sut/breadcrumb-link {:href "#"} "Library"]]
     [sut/breadcrumb-separator {}]
     [sut/breadcrumb-item {}
      [sut/breadcrumb-page {} "Data"]]]]]))