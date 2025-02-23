(ns mateuszmazurczak.main
  "Entry point to mateuszmazurczak app"
  (:require
   [mateuszmazurczak.navigation.panels :as mm-nav-panels]
   [mateuszmazurczak.navigation.router :as ev-routing]
   [re-frame.core                      :as rf]))

(defn router-component
  "Component to route to the `:mateuszmazurczak-subs/route-match`"
  []
  (let [current-panel (rf/subscribe [::ev-routing/route-match])]
    (fn [] [mm-nav-panels/panels @current-panel])))


(defn main-component "Main component replacing app" [] [router-component])
