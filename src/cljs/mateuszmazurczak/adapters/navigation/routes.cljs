(ns mateuszmazurczak.adapters.navigation.routes
  "Defines the routes for the mateuszmazurczak frontend
  All leaves are expected to be named (i.e. with a `:name` keyword ) as routes are searched by their keywords"
  (:require
   [mateuszmazurczak.ports.events :as events]))

(def routes
  [[""
    {:name ::root
     :page-id :pages/home
     :controllers [{:start (fn [_] (events/dispatch! [:home/on-route-enter]))}]}]
   ["/"
    {:name ::home ;; Important for history as browser adds systematically that `/`
     :page-id :pages/home
     :controllers [{:start (fn [_] (events/dispatch! [:home/on-route-enter]))}]}]
   ["/articles"
    {:name ::articles
     :page-id :pages/articles}]
   ["/article/:article-id"
    {:name ::article
     :page-id :pages/article}]
   ["/aoc"
    {:name ::aoc
     :page-id :pages/aoc
     :controllers [{:start (fn [_] (events/dispatch! [:aoc/on-route-enter]))}]}]
   ["/secret-admin-panel-xyz"
    {:name ::admin
     :page-id :pages/admin
     :controllers [{:start (fn [_] (events/dispatch! [:admin/on-route-enter]))}]}]])
