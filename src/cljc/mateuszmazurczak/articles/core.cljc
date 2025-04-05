(ns mateuszmazurczak.articles.core
  (:require
   [mateuszmazurczak.navigation.routes :as-alias mm-routes]))

(def articles
  (repeat
   10
   {:route ::mm-routes/routing-big-picture
    :title "Routing big picture"
    :description
    "This article aims to provide a general, shallow understanding of how client requests reach your application and what occurs during that process."
    :img "article/routing/image.png"}))
