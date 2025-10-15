(ns mateuszmazurczak.portfolio.mateuszmazurczak.home
  (:require
   [mateuszmazurczak.portfolio.utils :as mm-portfolio-utils]
   [mateuszmazurczak.ui.pages.home   :as sut]
   [portfolio.reagent-18             :as           portfolio
                                     :refer-macros [defscene
                                                    configure-scenes]]))

(configure-scenes {:collection :mateuszmazurczak
                   :title "Mateuszmazurczak page"})

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defscene home (mm-portfolio-utils/wrap-component [sut/home]))
