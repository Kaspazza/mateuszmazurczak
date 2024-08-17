(ns mateuszmazurczak.ui.fe-navigation-test
  (:require
   [automaton-web.adapters.fe.url     :as fe-url]
   [mateuszmazurczak.routes           :as mateuszmazurczak-routes]
   [mateuszmazurczak.ui.fe-navigation :as sut]))

(comment
  (-> (sut/href-delta ::mateuszmazurczak-routes/privacy)
      fe-url/navigate!)
  ;
)
