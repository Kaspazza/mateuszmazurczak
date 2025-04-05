(ns mateuszmazurczak.navigation.routes
  "Defines the routes for the mateuszmazurczak frontend
  All leaves are expected to be named (i.e. with a `:name` keyword ) as routes are searched by their keywords")

(def routes
  [[""
    {:name ::root
     :panel-id :panels/home}]
   ["/"
    {:name ::home ;; Important for history as browser adds systematically that `/`
     :panel-id :panels/home}]
   ["/articles"
    {:name ::articles
     :panel-id :panels/articles}]
   ["/article"
    ["/routing/big-picture"
     {:name ::routing-big-picture
      :panel-id :panels/routing-big-picture}]]])
