(ns mateuszmazurczak.portfolio.mateuszmazurczak.hero
  (:require
   [automaton-web.portfolio.proxy :as web-proxy]
   [mateuszmazurczak.i18n.fe.translate     :as mateuszmazurczak-fe-translate]
   [mateuszmazurczak.ui.hero               :as sut]
   [portfolio.reagent-18          :as           portfolio
                                  :refer-macros [defscene configure-scenes]]))

(configure-scenes {:collection :mateuszmazurczak
                   :title "Hero section"})

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defscene hero (web-proxy/wrap-component [sut/hero-section]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defscene main-text (web-proxy/wrap-component [sut/main-section]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defscene icons
          (web-proxy/wrap-component
           [sut/social-proof {:text (mateuszmazurczak-fe-translate/tr
                                     :commercial-experience)
                              :icons (take 6 sut/icons)}
            {:text (mateuszmazurczak-fe-translate/tr :theory-experience)
             :icons (drop 6 sut/icons)}]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defscene icons-container
          (web-proxy/wrap-component [apply
                                     sut/social-proof-container
                                     {:text "This is custom container"}
                                     (take 6 sut/icons)]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defscene single-icon
          (web-proxy/wrap-component [sut/social-proof-icon (first sut/icons)]))
