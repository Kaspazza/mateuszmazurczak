(ns mateuszmazurczak.portfolio.mateuszmazurczak.us
  (:require
   [automaton-web.portfolio.proxy :as web-proxy]
   [mateuszmazurczak.ui.us                 :as sut]
   [portfolio.reagent-18          :as           portfolio
                                  :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :mateuszmazurczak
                   :title "About us section"})

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defscene about-us (web-proxy/wrap-component [sut/about-us-section]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defscene about-us-dark
          (web-proxy/wrap-component [sut/about-us-section {:dark? true}]))
