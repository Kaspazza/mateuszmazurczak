(ns mateuszmazurczak.ui.simulation
  (:require
   [automaton-web.components.navigation :as web-navigation]
   [automaton-web.components.section    :as web-section]
   [mateuszmazurczak.i18n.fe.translate           :as mateuszmazurczak-fe-translate]
   [mateuszmazurczak.routes                      :as mateuszmazurczak-routes]
   [mateuszmazurczak.ui.article                  :as mateuszmazurczak-ui-article]
   [mateuszmazurczak.ui.fe-navigation            :as mateuszmazurczak-fe-nav]))

(defn simulation-section
  [{:keys [dark?]}]
  [:div {:class ["py-16" (if dark? "bg-theme-dark" "bg-theme-light")]}
   [web-section/text-section
    {:title (mateuszmazurczak-fe-translate/tr :simulation-offer)
     :description
     [:div {:class ["flex flex-col text-lg"]}
      [:span (mateuszmazurczak-fe-translate/tr :simulation-simplify-decision-making)]
      [:span (mateuszmazurczak-fe-translate/tr :simulation-answers-question)]
      [:ul {:class ["flex flex-col gap-2 mt-5 list-disc ml-4"]}
       [:li (mateuszmazurczak-fe-translate/tr :simulation-what-if-throughput-increased)]
       [:li (mateuszmazurczak-fe-translate/tr :simulation-what-if-more-work-in-progress)]
       [:li (mateuszmazurczak-fe-translate/tr :simulation-what-if-products-evolving)]
       [:li (mateuszmazurczak-fe-translate/tr :simulation-what-if-new-product)]
       [:li (mateuszmazurczak-fe-translate/tr :simulation-what-if-new-machine)]]]
     :btn-props {:text (mateuszmazurczak-fe-translate/tr :read-more)}
     :image-src "img/simulation/simulation.png"
     :dark? dark?}
    {:title [:div {:class ["ml-6 text-xl"]}
             (web-navigation/navigation
              {:href (mateuszmazurczak-fe-nav/href-delta ::mateuszmazurczak-routes/simulation)
               :text (mateuszmazurczak-fe-translate/tr :read-more)
               :dark? dark?})]}]])

(defn simulation-detailed [] [mateuszmazurczak-ui-article/article-page :simulation-page])

(defn simulation-pitch-detailed
  []
  [mateuszmazurczak-ui-article/article-page :simulation-pitch-page])
