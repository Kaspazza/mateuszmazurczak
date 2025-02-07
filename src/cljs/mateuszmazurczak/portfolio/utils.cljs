(ns mateuszmazurczak.portfolio.utils)

(defn wrap-component
  "A simple wrapper to enable additional code to be added for each scene.
   Right now it's empty"
  [& cmps]
  [:span (doall (for [cmp cmps] ^{:key (str (random-uuid))} cmp))])
