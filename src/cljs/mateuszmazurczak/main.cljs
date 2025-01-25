(ns mateuszmazurczak.main
  "Entry point to mateuszmazurczak app"
  (:require
   [automaton-web.events-proxy         :as web-events-proxy]
   [mateuszmazurczak.events.routing    :as ev-routing]
   [mateuszmazurczak.navigation.panels :as mm-nav-panels]))

(defn router-component
  "Component to route to the `:mateuszmazurczak-subs/route-match`"
  []
  (let [current-panel (web-events-proxy/subscribe [::ev-routing/route-match])]
    (fn [] [mm-nav-panels/panels @current-panel])))


(defn main-component "Main component replacing app" [] [router-component])
