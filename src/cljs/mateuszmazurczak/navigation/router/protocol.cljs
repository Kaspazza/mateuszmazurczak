(ns mateuszmazurczak.navigation.router.protocol)

(defprotocol Router
  (match-from-url [this url]
   "Find a match object from url. A match should contain what is needed to build a path again (what spa page, its parameters, the url encoded parameters...)")
  (route-name [this match]
   "Return a route name from a match")
  (panel-id [this match]
   "Return the panel id that will be displayed for that match")
  (url-params [this match]
   "Return the parameters encoded in the url"))
