(ns mateuszmazurczak.ui.fe-navigation
  "Navigation components based on mateuszmazurczak router and history"
  (:require
   [automaton-web.events-proxy  :as web-events-proxy]
   [mateuszmazurczak.fe.history :as mateuszmazurczak-fe-history]))

(defn href-delta
  "Search for an hyperlink relatively to the current
  So all nil values set below will be replaced with the value of the current match
  Params:
  * `route-name` name of the route to navigate, replaced with current route name if nil
  * `path-params` add the parameters in that map to the current parameter map
  * `query-params` add the parameters in that map to the current query parameters"
  ([route-name path-params query-params]
   (mateuszmazurczak-fe-history/href-delta
    (web-events-proxy/subscribe-value
     [:mateuszmazurczak.events.subs/route-match])
    route-name
    path-params
    query-params))
  ([route-name path-params]
   (mateuszmazurczak-fe-history/href-delta
    (web-events-proxy/subscribe-value
     [:mateuszmazurczak.events.subs/route-match])
    route-name
    path-params
    {}))
  ([route-name]
   (mateuszmazurczak-fe-history/href-delta
    (web-events-proxy/subscribe-value
     [:mateuszmazurczak.events.subs/route-match])
    route-name
    {}
    {})))
