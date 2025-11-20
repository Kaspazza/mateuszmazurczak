(ns mateuszmazurczak.adapters.navigation.reitit-router
  "Adapter for `` based on [reitit](https://cljdoc.org/d/fi.metosin/reitit/0.7.0-alpha6/doc/introduction)

  That namespace is written to always return a page when something unexpected occur, so a page is displayed, even if a message is logged

  Create an instance preferably with `make-reitit-routes` function"
  (:require
   [reitit.core       :as reitit]
   [reitit.dev.pretty :as pretty]
   [reitit.frontend   :as reitit-frontend]
   [reitit.spec       :as rs]))

(defn create-router
  "Make reitit router
  Params:
  * `routes` is the data structure describing the routes
  * `gather-route-params-fn` function with no argument returning a map with all data used in the routes"
  ([routes] (create-router routes {}))
  ([routes router-params]
   (reitit/router routes
                  (merge {:conflicts (fn [conflicts]
                                       {:conflicts conflicts
                                        :routes routes})
                          :validate rs/validate
                          :exception pretty/exception}
                         router-params))))

(defn match-by-path
  "Find match for a given path"
  [router path]
  (reitit-frontend/match-by-path router path))

(defn match-by-name
  "Find match for a given route name"
  [router route-name & [path-params]]
  (reitit/match-by-name router route-name path-params))

;; Extraction from match functions
(defn page-id-from-match [match] (get-in match [:data :page-id] :pages/not-found))

(defn route-name-from-match [match] (get-in match [:data :name]))

(defn path-params-from-match [match] (:path-params match))

(defn query-params-from-match [match] (:query-params match))

;; Route name based functions
(defn page-id
  "Get page ID for a route"
  [router route-name & [path-params]]
  (let [match (match-by-name router route-name path-params)] (page-id-from-match match)))

(defn path-params
  "Get default path parameters for a route"
  [router route-name]
  (let [match (match-by-name router route-name)] (path-params-from-match match)))

(defn all-routes
  "Get all route names in the router"
  [router]
  (map route-name-from-match (reitit/routes router)))
