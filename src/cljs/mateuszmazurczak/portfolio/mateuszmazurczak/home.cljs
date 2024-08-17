(ns mateuszmazurczak.portfolio.mateuszmazurczak.home
  (:require
   [automaton-web.portfolio.proxy :as web-proxy]
   [mateuszmazurczak.ui.home      :as sut]
   [portfolio.reagent-18          :as           portfolio
                                  :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :mateuszmazurczak
                   :title "Mateuszmazurczak page"})

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defscene home
          (web-proxy/wrap-component [sut/mateuszmazurczak-page
                                     {:document (web-proxy/iframe-document)}]))
