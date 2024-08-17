(ns mateuszmazurczak.routes
  "Defines the routes for the mateuszmazurczak frontend
  All leaves are expected to be named (i.e. with a `:name` keyword ) as routes are searched by their keywords")

(def routes
  "Data structure defining all possible routes for mateuszmazurczak
  Not found should not be there, as it introduce conflicts, use nil value instead

  * `:be` is storing backend router data, as described in [reitit.ring/router](https://cljdoc.org/d/fi.metosin/reitit/0.7.0-alpha6/doc/ring/ring-router#reititringrouter)
  * `:fe` is storing frontend router data
  "
  [[""
    {:name ::root
     :be {:get :html-page/index
          :middleware []}
     :fe {:panel-id :panels/home}}]
   ["/"
    {:name ::home ;; Important for history as browser adds systematically that `/`
     :be {:get :html-page/index}
     :fe {:panel-id :panels/home}}]])
