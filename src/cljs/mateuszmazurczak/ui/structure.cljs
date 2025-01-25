(ns mateuszmazurczak.ui.structure
  (:require
   [automaton-web.components.structure :as web-structure]
   [mateuszmazurczak.ui.footer         :as mm-ui-footer]
   [mateuszmazurczak.ui.header         :as mm-ui-headers]))

(defn mateuszmazurczak-page-structure
  [& components]
  [apply
   web-structure/structure
   {:header [mm-ui-headers/transparent-header {}]
    :footer [mm-ui-footer/footer]}
   components])

(defn structure
  [& components]
  [apply
   web-structure/structure
   {:header [mm-ui-headers/header {:size :full
                                   :sticky? true
                                   :border? true}]
    :footer [mm-ui-footer/footer]}
   components])
