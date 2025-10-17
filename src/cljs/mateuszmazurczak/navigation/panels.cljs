(ns mateuszmazurczak.navigation.panels
  "Describes the link between panel names and contents"
  (:require
   [mateuszmazurczak.articles.core     :as articles]
   [mateuszmazurczak.frontend-i18n     :as fi18n]
   [mateuszmazurczak.ui.errors         :as mm-ui-errors]
   [mateuszmazurczak.ui.pages.articles :as pages-articles]
   [mateuszmazurczak.ui.pages.home     :as mm-home]
   [mateuszmazurczak.ui.spinner        :as mm-ui-spinner]
   [mateuszmazurczak.ui.structure      :as mm-ui-structure]))

(defmulti panels :panel-id)

(defmethod panels :default
  [_]
  [mm-ui-errors/not-found {:title (fi18n/tr :not-found-page)
                           :description (fi18n/tr :not-found-description)
                           :back-home-text (fi18n/tr :back-home)}])

(defmethod panels :panels/pending [_] [:div [mm-ui-spinner/spinner]])

(defmethod panels :panels/system-error
  [_]
  [mm-ui-errors/internal-error
   {:title "System Initialization Failed"
    :description
    "We encountered an error while starting the application. Please refresh the page or contact support if the problem persists."
    :back-home-text "Refresh Page"}])

(defmethod panels :panels/home
  [_
   {:keys [loading?]
    :as data}]
  (if (false? loading?)
    [mm-ui-structure/mateuszmazurczak-page-structure [mm-home/home data]]
    [mm-ui-spinner/spinner]))

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
      [mm-ui-errors/not-found {:title (fi18n/tr :not-found-page)
                               :description (fi18n/tr :not-found-description)
                               :back-home-text (fi18n/tr :back-home)}])))
