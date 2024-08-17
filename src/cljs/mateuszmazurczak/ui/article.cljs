(ns mateuszmazurczak.ui.article
  (:require
   [automaton-web.components.section :as web-section]
   [mateuszmazurczak.i18n.fe.translate        :as mateuszmazurczak-fe-translate]
   [mateuszmazurczak.routes                   :as mateuszmazurczak-routes]
   [mateuszmazurczak.ui.fe-navigation         :as mateuszmazurczak-fe-nav]
   [mateuszmazurczak.ui.navigation            :as mateuszmazurczak-navigation]))

(defn article-page
  [article-page]
  [web-section/section-text-container {}
   [mateuszmazurczak-navigation/back-navigation
    {:href (mateuszmazurczak-fe-nav/href-delta ::mateuszmazurczak-routes/home)
     :text (mateuszmazurczak-fe-translate/tr :back-home)}]
   [:div {:class ["notion-page"]}
    (mateuszmazurczak-fe-translate/tr article-page)]])
