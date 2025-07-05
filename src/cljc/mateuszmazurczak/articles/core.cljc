(ns mateuszmazurczak.articles.core
  (:require
   [mateuszmazurczak.articles.routing.big-picture :as big-picture]
   [mateuszmazurczak.navigation.routes            :as-alias mm-routes]))

(def articles
  [{:id :routing-big-picture
    :title "Routing big picture"
    :date "2025/01/31"
    :description
    "This article aims to provide a general, shallow understanding of how client requests reach your application and what occurs during that process."
    :img "article/routing/image.png"
    :content big-picture/article-content}])

(defn article
  [article-id]
  (some (fn [{:keys [id]
              :as article}]
          (when (= article-id id) article))
        articles))
