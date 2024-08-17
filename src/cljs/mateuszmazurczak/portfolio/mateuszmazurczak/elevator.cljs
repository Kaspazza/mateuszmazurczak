(ns mateuszmazurczak.portfolio.mateuszmazurczak.elevator
  (:require
   [automaton-web.portfolio.proxy :as web-proxy]
   [mateuszmazurczak.ui.elevator-pitch     :as sut]
   [portfolio.reagent-18          :as           portfolio
                                  :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :mateuszmazurczak
                   :title "Elevator pitch section"})

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defscene elevator-pitch
          (web-proxy/wrap-component [sut/elevator-pitch-section]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defscene elevator-pitch-dark
          (web-proxy/wrap-component [sut/elevator-pitch-section {:dark? true}]))
