(ns mateuszmazurczak.navigation.panels
  "Describes the link between panel names and contents"
  (:require
   [mateuszmazurczak.articles.core     :as articles]
   [mateuszmazurczak.i18n.translate    :as mm-i18n-translate]
   [mateuszmazurczak.ui.errors         :as mm-ui-errors]
   [mateuszmazurczak.ui.pages.articles :as pages-articles]
   [mateuszmazurczak.ui.pages.home     :as mm-home]
   [mateuszmazurczak.ui.spinner        :as mm-ui-spinner]
   [mateuszmazurczak.ui.structure      :as mm-ui-structure]))

(defmulti panels :panel-id)

(defmethod panels :default
  [_]
  [mm-ui-errors/not-found {:title (mm-i18n-translate/tr :not-found-page)
                           :description (mm-i18n-translate/tr
                                         :not-found-description)
                           :back-home-text (mm-i18n-translate/tr :back-home)}])

(defmethod panels :panels/pending [_] [:div [mm-ui-spinner/spinner]])

(defmethod panels :panels/home
  [_]
  [mm-ui-structure/mateuszmazurczak-page-structure [mm-home/home]])

(defmethod panels :panels/articles
  [_]
  [mm-ui-structure/mateuszmazurczak-page-structure
   [pages-articles/articles-page]])

(defmethod panels :panels/article
  [route-data]
  (let [article-id (keyword (get-in route-data [:path-parameters :article-id]))
        article (articles/article article-id)]
    (if article
      [mm-ui-structure/mateuszmazurczak-page-structure
       [pages-articles/article-page article]]
      [mm-ui-errors/not-found
       {:title (mm-i18n-translate/tr :not-found-page)
        :description (mm-i18n-translate/tr :not-found-description)
        :back-home-text (mm-i18n-translate/tr :back-home)}])))
