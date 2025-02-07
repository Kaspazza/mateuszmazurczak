(ns mateuszmazurczak.ui.home
  "Customer app home page assembly"
  (:require
   [mateuszmazurczak.navigation.history :as mm-nav-hist]
   [mateuszmazurczak.routes             :as mm-routes]
   [mateuszmazurczak.ui.navigation      :as mm-ui-navigation]))



(defn about-me
  []
  [:div {:class "hero-content flex-col lg:flex-row"}
   [:div {:style {:height "100%"
                  :position "relative"
                  :background-color "#bea5c2 "}}]
   [:div {:class "rounded-full block aspect-square overflow-hidden"}
    [:img {:src "img/mateuszmazurczak.png"}]]
   [:div
    [:h1 {:class "text-5xl font-bold"}
     "Hi, I'm Mati!"]
    [:p {:class "py-6 text-3xl"}
     "I just like to write simple code in parenthesis or write about writing code."
     [:br]]
    [:p {:class "py-6 text-3xl"}
     "Feel free to message me for consulting or to share insights and ideas!"]]])

(defn mateuszmazurczak-page
  []
  [:div {:class ["flex flex-col p-36 gap-16"]}
   [about-me]
   [:span {:class ["text-2xl/7 font-bold"]}
    [mm-ui-navigation/navigation {:href (mm-nav-hist/href-delta
                                         ::mm-routes/articles)
                                  :text "Blog posts:"
                                  :dark? true}]]])

(defn home
  "Functional component for displaying mateuszmazurczak page sections."
  []
  [mateuszmazurczak-page])
