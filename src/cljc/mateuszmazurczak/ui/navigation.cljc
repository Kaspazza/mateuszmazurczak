(ns mateuszmazurczak.ui.navigation
  "Navigation component, instantited with mateuszmazurczak router"
  (:require
   [automaton-web.components.navigation :as web-navigation]
   #?@(:cljs [[mateuszmazurczak.ui.fe-navigation :as mateuszmazurczak-fe-nav]])))

(defn back-navigation
  "Print a back navigation button
  * The linked could be set with `href` or `on-click` and are higher priority"
  [nav-opts]
  (web-navigation/back-navigation (assoc nav-opts
                                         :href
                                         #?(:cljs (mateuszmazurczak-fe-nav/href-delta
                                                   :mateuszmazurczak.routes/home)
                                            :clj "/"))))
