(ns mateuszmazurczak.main
  "Entry point to mateuszmazurczak app"
  (:require
   [mateuszmazurczak.navigation.panels :as mm-nav-panels]
   [re-frame.core                      :as rf]))

(defn router-component
  "Component to route to the `:mateuszmazurczak-subs/route-match`"
  []
  (let [current-route @(rf/subscribe [:nav/current-route])]
    [mm-nav-panels/panels current-route]))


(defn main-component "Main component replacing app" [] [router-component])
