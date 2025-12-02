(ns mateuszmazurczak.adapters.http.routes
  (:require
   [mateuszmazurczak.adapters.http.api   :as api]
   [mateuszmazurczak.adapters.http.pages :refer [article-page mateuszmazurczak-page]]
   [mateuszmazurczak.env                 :as mm-env]))

(def routes
  "Storing backend router data, as described in [reitit.ring/router](https://cljdoc.org/d/fi.metosin/reitit/0.7.0-alpha6/doc/ring/ring-router#reititringrouter)
   Not found should not be here, as it introduce conflicts, use nil value instead."
  (conj
   [[""
     {:name ::root
      :get mateuszmazurczak-page
      :middleware []}]
    ["/"
     {:name ::home ;; Important for history as browser adds systematically that `/`
      :get mateuszmazurczak-page}]
    ["/articles"
     {:name ::articles
      :get mateuszmazurczak-page}]
    ["/article/:article-name"
     {:name ::article
      :get article-page}]
    ["/aoc"
     [""
      {:name ::aoc
       :get mateuszmazurczak-page}]
     ["/:year/:challenge"
      {:name ::aoc-specific
       :get mateuszmazurczak-page}]]
    ["/secret-admin-panel-xyz"
     {:name ::admin
      :get mateuszmazurczak-page}]
    ["/api/aoc/solutions"
     [""
      {:name ::api-aoc-solutions
       :get api/get-solutions
       :post api/post-solution}]
     ["/vote"
      {:name ::api-aoc-solutions-vote
       :post api/post-vote
       :conflicting true}]
     ["/:solution-id"
      {:name ::api-aoc-solution-delete
       :delete api/delete-solution
       :conflicting true
       :constraints
       {:solution-id
        #"[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"}}]]]
   (mm-env/route)))
