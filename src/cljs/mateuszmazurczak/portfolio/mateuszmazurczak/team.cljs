(ns mateuszmazurczak.portfolio.mateuszmazurczak.team
  (:require
   [automaton-web.portfolio.proxy :as web-proxy]
   [mateuszmazurczak.ui.team               :as sut]
   [portfolio.reagent-18          :as           portfolio
                                  :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :mateuszmazurczak
                   :title "Our team section"})

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defscene our-team (web-proxy/wrap-component [sut/our-team-section]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defscene our-team-dark
          (web-proxy/wrap-component [sut/our-team-section {:dark? true}]))
