(ns mateuszmazurczak.ui.fe-navigation-test
  (:require
   [automaton-web.adapters.fe.url     :as fe-url]
   [mateuszmazurczak.navigation.utils :as sut]
   [mateuszmazurczak.routes           :as mateuszmazurczak-routes]))

(comment
  (-> (sut/href-delta ::mateuszmazurczak-routes/privacy)
      fe-url/navigate!)
  ;
)
