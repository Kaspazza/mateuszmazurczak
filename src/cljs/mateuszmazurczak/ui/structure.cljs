(ns mateuszmazurczak.ui.structure
  (:require
   [mateuszmazurczak.ui.footer :as mm-ui-footer]
   [mateuszmazurczak.ui.header :as mm-ui-headers]))

(defn structure
  [{:keys [header footer class]} & components]
  [:div {:class (vec (concat class
                             ["h-fit min-h-screen flex flex-col relative"]))}
   header
   (into [:div {:class ["grow"]}]
         (for [comp components] comp))
   footer])

(defn mateuszmazurczak-page-structure
  [& components]
  (apply structure
         {:header [mm-ui-headers/header {:size :full
                                         :sticky? true
                                         :border? true}]
          :footer [mm-ui-footer/footer]}
         components))
