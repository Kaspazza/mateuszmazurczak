(ns mateuszmazurczak.main
  "Entry point to mateuszmazurczak app"
  (:require
   [automaton-web.events-proxy   :as web-events-proxy]
   [automaton-web.react-proxy    :as web-react]
   [clojure.string               :as str]
   [mateuszmazurczak.events.subs :as mateuszmazurczak-subs]
   [mateuszmazurczak.fe.panels   :as mateuszmazurczak-fe-panels]))

(defn- set-scroll-position!
  "Aligns scroll position of a page to Match fragment. If there is no specified fragment element or there is no component with that id on the page scroll position is set to the top of the window.
   In mateuszmazurczak page with each route change we want to start at the top of the page, unless specifiec otherwise with fragment value."
  []
  (let [fragment (:fragment (web-events-proxy/subscribe-value
                             [::mateuszmazurczak-subs/route-match]))
        element (.getElementById js/document fragment)]
    (if (and fragment (not (str/blank? fragment)) element)
      (.scrollIntoView element)
      (.scrollTo js/window 0 0))))

(defn router-component
  "Component to route to the `:mateuszmazurczak-subs/route-match`"
  []
  (web-react/create-class
   {:component-did-update (fn [_] (set-scroll-position!))
    :reagent-render (fn [_]
                      (let [route-match
                            (some-> (web-events-proxy/subscribe
                                     [::mateuszmazurczak-subs/route-match])
                                    deref)]
                        [mateuszmazurczak-fe-panels/panels route-match]))}))

(defn main-component "Main component replacing app" [] [router-component])
