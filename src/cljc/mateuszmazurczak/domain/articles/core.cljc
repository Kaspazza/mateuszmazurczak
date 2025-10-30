(ns mateuszmazurczak.domain.articles.core
  (:require
   [mateuszmazurczak.domain.articles.files               :as art-files]
   [mateuszmazurczak.domain.articles.routing-big-picture :as big-picture]
   [mateuszmazurczak.navigation.routes                   :as-alias mm-routes]))

(def hiccup-content {:routing-big-picture big-picture/article-content})

(def articles
  (mapv (fn [art]
          (if-let [article-content (get hiccup-content (:id art))]
            (assoc art :content article-content)
            art))
        (art-files/read-file "articles.edn")))

(defn article
  [article-id]
  (some (fn [{:keys [id]
              :as article}]
          (when (= article-id id) article))
        articles))
