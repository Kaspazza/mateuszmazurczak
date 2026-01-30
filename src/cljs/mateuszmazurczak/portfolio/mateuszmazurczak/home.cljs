(ns mateuszmazurczak.portfolio.mateuszmazurczak.home
  (:require
   [mateuszmazurczak.application.home.page-data :as page-data]
   [mateuszmazurczak.domain.articles.core       :as articles]
   [mateuszmazurczak.portfolio.utils            :as mm-portfolio-utils]
   [mateuszmazurczak.ui.pages.home              :as sut]
   [portfolio.reagent-18                        :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :mateuszmazurczak
                   :title "Mateuszmazurczak page"})

(def home-data
  (let [page-data (page-data/build-home-page-data articles/articles "js.alert('hello')")]
    page-data))

(defscene home [] (mm-portfolio-utils/wrap-component [sut/home nil]))
