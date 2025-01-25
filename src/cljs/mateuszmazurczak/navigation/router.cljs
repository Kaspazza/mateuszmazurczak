(ns mateuszmazurczak.navigation.router
  "Mateuszmazurczak cust-app front end router"
  (:require
   [automaton-core.log                 :as core-log]
   [automaton-web.fe.router            :as web-fe-router]
   [automaton-web.fe.router.reitit     :as fe-reitit-router]
   [mateuszmazurczak.i18n.translate    :as mm-fe-translate]
   [mateuszmazurczak.navigation.routes :as mm-fe-routes]
   [mount.core                         :refer [defstate]]))

(defn- gather-route-params-fn
  "Gather parameters necessary to build the route,
  Note that it is useless in mateuszmazurczak as no parameters encoded in path are used"
  []
  {:lang (mm-fe-translate/lang)})

(defn start-router
  []
  (fe-reitit-router/make-reitit-router mm-fe-routes/routes
                                       gather-route-params-fn))

(defstate router
          :start
          (try (start-router)
               (catch :default e
                 (core-log/error (ex-info "Impossible to start router" e)))))

(defn match-from-url
  "Match the `url`
  For instance match `#legal/disclaimer` into reitit matcher to `:disclaimer`, query params, ...
  Params:
  * `router` (Optional, default to this namespace router )
  * `url` to analyse"
  ([url] (web-fe-router/match-from-url @router url))
  ([router url] (web-fe-router/match-from-url router url)))

(defn panel-id
  "Return the name of the panel to retrieve"
  [match]
  (web-fe-router/panel-id @router match))

(defn url-params
  "Return the url parameters of the matched route
Params:
  * `match` match"
  [match]
  (web-fe-router/url-params @router match))
