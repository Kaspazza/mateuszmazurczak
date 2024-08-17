(ns mateuszmazurczak.portfolio.mateuszmazurczak.downloadable
  (:require
   [automaton-web.portfolio.proxy     :as web-proxy]
   [mateuszmazurczak.ui.downloadable-materials :as sut]
   [portfolio.reagent-18              :as           portfolio
                                      :refer-macros [defscene
                                                     configure-scenes]]))

(configure-scenes {:collection :mateuszmazurczak
                   :title "Downloadable materials section"})

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defscene downloadable-materials
          (web-proxy/wrap-component [sut/downloadable-info
                                     {:document (web-proxy/iframe-document)}]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defscene downloadable-materials-dark
          (web-proxy/wrap-component [sut/downloadable-info
                                     {:document (web-proxy/iframe-document)
                                      :dark? true}]))
