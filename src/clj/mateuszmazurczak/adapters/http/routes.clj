(ns mateuszmazurczak.adapters.http.routes
  (:require
   [mateuszmazurczak.adapters.http.pages :refer [article-page mateuszmazurczak-page]]
   [mateuszmazurczak.env                 :as mm-env]))

(def routes
  "Storing backend router data, as described in [reitit.ring/router](https://cljdoc.org/d/fi.metosin/reitit/0.7.0-alpha6/doc/ring/ring-router#reititringrouter)
   Not found should not be here, as it introduce conflicts, use nil value instead."
  (conj [[""
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
           :get article-page}]]
        (mm-env/route)))
