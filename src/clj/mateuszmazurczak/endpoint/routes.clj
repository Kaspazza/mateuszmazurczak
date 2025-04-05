(ns mateuszmazurczak.endpoint.routes
  (:require
   [mateuszmazurczak.endpoint.handler :refer [article-page
                                              mateuszmazurczak-page]]
   [mateuszmazurczak.env              :as mm-env]))

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
         ["/article"
          ["/routing/big-picture"
           {:name ::big-picture
            :get (partial article-page
                          {:title :routing-big-picture
                           :description :routing-big-picture-desc
                           :image :routing-big-picture-preview
                           :url "articles/routing/big-picture"})}]]]
        (mm-env/route)))
