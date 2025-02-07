(ns mateuszmazurczak.navigation.router
  "Mateuszmazurczak cust-app front end router"
  (:require
   [mateuszmazurczak.navigation.router.protocol :as mm-router]
   [mateuszmazurczak.navigation.router.reitit   :as mm-router-reitit]
   [mateuszmazurczak.navigation.routes          :as mm-fe-routes]
   [mount.core                                  :refer [defstate]]))

(defn start-router
  []
  (mm-router-reitit/make-reitit-router mm-fe-routes/routes {}))

(defstate router
          :start
          (try (start-router)
               (catch :default e (ex-info "Impossible to start router" e))))

(defn match-from-url
  "Match the `url`
  For instance match `#legal/disclaimer` into reitit matcher to `:disclaimer`, query params, ...
  Params:
  * `router` (Optional, default to this namespace router )
  * `url` to analyse"
  ([url] (mm-router/match-from-url @router url))
  ([router url] (mm-router/match-from-url router url)))

(defn panel-id
  "Return the name of the panel to retrieve"
  [match]
  (mm-router/panel-id @router match))

(defn url-params
  "Return the url parameters of the matched route
Params:
  * `match` match"
  [match]
  (mm-router/url-params @router match))
