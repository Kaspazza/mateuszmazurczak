(ns mateuszmazurczak.navigation.routes
  "Defines the routes for the mateuszmazurczak frontend
  All leaves are expected to be named (i.e. with a `:name` keyword ) as routes are searched by their keywords"
  (:require
   [mateuszmazurczak.events :as events]))

(def routes
  [[""
    {:name ::root
     :panel-id :panels/home
     :controllers [{:start (fn [_]
                             (events/dispatch! [:home/on-route-enter]))}]}]
   ["/"
    {:name ::home ;; Important for history as browser adds systematically that `/`
     :panel-id :panels/home
     :controllers [{:start (fn [_]
                             (events/dispatch! [:home/on-route-enter]))}]}]
   ["/articles"
    {:name ::articles
     :panel-id :panels/articles}]
   ["/article/:article-id"
    {:name ::article
     :panel-id :panels/article}]])
