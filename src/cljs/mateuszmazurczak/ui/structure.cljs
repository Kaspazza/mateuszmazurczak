(ns mateuszmazurczak.ui.structure
  (:require
   [automaton-web.components.structure :as web-structure]
   [mateuszmazurczak.ui.footer         :as lcf]
   [mateuszmazurczak.ui.header         :as mateuszmazurczak-headers]))

(defn mateuszmazurczak-page-structure
  [& components]
  [apply
   web-structure/structure
   {:header [mateuszmazurczak-headers/transparent-header {}]
    :footer [lcf/footer]}
   components])

(defn structure
  [& components]
  [apply
   web-structure/structure
   {:header [mateuszmazurczak-headers/header {:size :full
                                              :sticky? true
                                              :border? true}]
    :footer [lcf/footer]}
   components])
