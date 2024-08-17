(ns mateuszmazurczak.fe.routes
  "Defines the routes for the mateuszmazurczak frontend
  All leaves are expected to be named (i.e. with a `:name` keyword ) as routes are searched by their keywords"
  (:require
   [automaton-web.routes :as web-routes]
   [mateuszmazurczak.routes       :as mateuszmazurczak-routes]))

(def routes (web-routes/parse-routes :fe mateuszmazurczak-routes/routes))
