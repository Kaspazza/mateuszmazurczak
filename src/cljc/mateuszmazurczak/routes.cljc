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
     :fe {:panel-id :panels/home}}]
   ["/simulation-interactive"
    {:name ::simulation-interactive
     :be {:get :html-page/index}
     :fe {:panel-id :panels/simulation-interactive}}]
   ["/articles"
    ["/simulation"
     {:name ::simulation
      :be {:get :html-page/simulation}
      :fe {:panel-id :panels/simulation}}]
    ["/simulation-pitch"
     {:name ::simulation-pitch
      :be {:get :html-page/simulation-pitch}
      :fe {:panel-id :panels/simulation-pitch}}]
    ["/about-us"
     {:name ::about-us
      :be {:get :html-page/about-us}
      :fe {:panel-id :panels/about-us}}]
    ["/our-brand"
     {:name ::our-brand
      :be {:get :html-page/our-brand}
      :fe {:panel-id :panels/our-brand}}]
    ["/network"
     {:name ::network
      :be {:get :html-page/network}
      :fe {:panel-id :panels/network}}]
    ["/commitment"
     {:name ::commitment
      :be {:get :html-page/commitment}
      :fe {:panel-id :panels/commitment}}]
    ["/additional-outcomes"
     {:name ::additional-outcomes
      :be {:get :html-page/additional-outcomes}
      :fe {:panel-id :panels/additional-outcomes}}]
    ["/our-values"
     {:name ::our-values
      :be {:get :html-page/our-values}
      :fe {:panel-id :panels/our-values}}]]
   ["/legal/"
    ["disclaimer"
     {:name ::disclaimer
      :be {:get :html-page/index}
      :fe {:panel-id :panels/disclaimer}}]
    ["privacy"
     {:name ::privacy
      :be {:get :html-page/index}
      :fe {:panel-id :panels/privacy}}]]])
