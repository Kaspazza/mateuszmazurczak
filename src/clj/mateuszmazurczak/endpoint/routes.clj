(ns mateuszmazurczak.endpoint.routes
  (:require
   [automaton-web.routes    :as web-routes]
   [mateuszmazurczak.env    :as mm-env]
   [mateuszmazurczak.routes :as mm-routes]))

(defn web-routes
  "Routes for mateuszmazurczak,
  `mateuszmazurczak.routes` contains both backa and frontend routes,
  Here, the routes are parsed and the specific routes for local environment are added
  Params:
  * `registry` "
  [registry]
  (conj (web-routes/parse-routes :be mm-routes/routes registry) (mm-env/route)))
