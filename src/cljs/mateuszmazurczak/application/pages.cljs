(ns mateuszmazurczak.application.pages
  "Describes the link between page names and contents"
  (:require
   [mateuszmazurczak.domain.articles.core :as articles]
   [mateuszmazurczak.frontend-i18n        :as fi18n]
   [mateuszmazurczak.ui.errors            :as mm-ui-errors]
   [mateuszmazurczak.ui.pages.articles    :as pages-articles]
   [mateuszmazurczak.ui.pages.home        :as mm-home]
   [mateuszmazurczak.ui.spinner           :as mm-ui-spinner]
   [mateuszmazurczak.ui.structure         :as mm-ui-structure]))

(defmulti pages :page-id)

(defmethod pages :default
  [_]
  [mm-ui-errors/not-found {:title (fi18n/tr :not-found-page)
                           :description (fi18n/tr :not-found-description)
                           :back-home-text (fi18n/tr :back-home)}])

(defmethod pages :pages/pending [_] [:div [mm-ui-spinner/spinner]])

(defmethod pages :pages/system-error
  [_]
  [mm-ui-errors/internal-error
   {:title "System Initialization Failed"
    :description
    "We encountered an error while starting the application. Please refresh the page or contact support if the problem persists."
    :back-home-text "Refresh Page"}])

(defmethod pages :pages/home
  [_
   {:keys [loading?]
    :as data}]
  (if (false? loading?)
    [mm-ui-structure/mateuszmazurczak-page-structure [mm-home/home data]]
    [mm-ui-spinner/spinner]))

(defmethod pages :pages/articles
  [_]
  [mm-ui-structure/mateuszmazurczak-page-structure [pages-articles/articles-page]])

(defmethod pages :pages/article
  [route-data]
  (let [article-id (keyword (get-in route-data [:path-parameters :article-id]))
        article (articles/article article-id)]
    (if article
      [mm-ui-structure/mateuszmazurczak-page-structure [pages-articles/article-page article]]
      [mm-ui-errors/not-found {:title (fi18n/tr :not-found-page)
                               :description (fi18n/tr :not-found-description)
                               :back-home-text (fi18n/tr :back-home)}])))
