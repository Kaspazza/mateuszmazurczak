(ns mateuszmazurczak.portfolio.mateuszmazurczak.home
  (:require
   [mateuszmazurczak.adapters.navigation.routes :as mm-routes]
   [mateuszmazurczak.domain.articles.core       :as articles]
   [mateuszmazurczak.domain.pages.home          :as home-domain]
   [mateuszmazurczak.portfolio.utils            :as mm-portfolio-utils]
   [mateuszmazurczak.ports.navigation           :as navigation]
   [mateuszmazurczak.ui.pages.home              :as sut]
   [portfolio.reagent-18                        :as           portfolio
                                                :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :mateuszmazurczak
                   :title "Mateuszmazurczak page"})

(def home-data
  (let [page-data (home-domain/build-home-page-data articles/articles "js.alert('hello')")]
    page-data))

(defscene home [] (mm-portfolio-utils/wrap-component [sut/home nil]))
