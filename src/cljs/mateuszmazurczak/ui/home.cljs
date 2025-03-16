(ns mateuszmazurczak.ui.home
  "Customer app home page assembly"
  (:require
   [mateuszmazurczak.i18n.translate    :as mm-i18n-translate]
   [mateuszmazurczak.navigation.core   :as navigation]
   [mateuszmazurczak.navigation.routes :as mm-routes]
   [mateuszmazurczak.ui.navigation     :as mm-ui-navigation]))

(defn about-me
  []
  [:div {:class "hero-content flex-col lg:flex-row"}
   [:div {:style {:height "100%"
                  :position "relative"
                  :background-color "#bea5c2 "}}]
   [:div {:class "rounded-full block aspect-square overflow-hidden"}
    [:img {:src "img/mateuszmazurczak.png"}]]
   [:div
    [:h1 {:class "text-2xl md:text-3xl lg:text-5xl font-bold"}
     (mm-i18n-translate/tr :hi-mati)]
    [:p {:class "py-6 text-md md:text-xl lg:text-3xl"}
     (mm-i18n-translate/tr :i-like-simplicity)
     [:br]]
    [:p {:class "py-6 text-md md:text-xl lg:text-3xl"}
     (mm-i18n-translate/tr :contact-me)]]])

(defn mateuszmazurczak-page
  []
  [:div {:class ["flex flex-col p-12 lg:p-36 gap-16"]}
   [about-me]
   [:span {:class ["text-2xl/7 font-bold"]}
    [mm-ui-navigation/navigation {:href (navigation/href ::mm-routes/articles)
                                  :text (mm-i18n-translate/tr :articles)
                                  :dark? true}]]])

(defn home
  "Functional component for displaying mateuszmazurczak page sections."
  []
  [mateuszmazurczak-page])
