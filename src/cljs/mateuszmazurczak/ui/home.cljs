(ns mateuszmazurczak.ui.home
  "Customer app home page assembly"
  (:require
   [automaton-web.components.navigation :as web-navigation]
   [mateuszmazurczak.navigation.utils   :as mm-nav]
   [mateuszmazurczak.routes             :as mm-routes]))

(defn mateuszmazurczak-page
  []
  [:div {:class ["flex flex-col mt-12"]}
   [:span "hello world"]
   (web-navigation/navigation
    {:href (str (mm-nav/href-delta ::mm-routes/articles) "#clue")
     :text "Go to articles"
     :dark? true})
   [:div (repeat 1000 "I'm repeated over and over... ")]
   (web-navigation/navigation {:href (mm-nav/href-delta ::mm-routes/articles)
                               :text "Go to articles"
                               :dark? true})])

(defn home
  "Functional component for displaying mateuszmazurczak page sections."
  []
  [mateuszmazurczak-page])
