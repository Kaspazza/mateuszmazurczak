(ns mateuszmazurczak.ui.pages.home
  "Customer app home page assembly"
  (:require
   [mateuszmazurczak.articles.core     :as articles]
   [mateuszmazurczak.i18n.translate    :as mm-i18n-translate]
   [mateuszmazurczak.navigation.core   :as navigation]
   [mateuszmazurczak.navigation.routes :as mm-routes]
   [mateuszmazurczak.ui.articles       :as ui-articles]
   [mateuszmazurczak.ui.navigation     :as mm-ui-navigation]
   [re-frame.core                      :as rf]))




(defn about-me
  []
  [:div {:class "hero-content flex-col lg:flex-row max-w-screen justify-evenly"
         :style {:background
                 "linear-gradient(85deg, rgb(170 137 173) 41%, #fff 100%)"}}
   [:div {:style {:height "100%"
                  :position "relative"
                  :background-color "#bea5c2 "}}]
   [:div {:class "block  overflow-hidden w-80"}
    [:img {:src "img/mateusz_mazurczak.png"}]]
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
  [:div
   [about-me]
   [:div {:class ["flex flex-col pt-8 p-12 lg:p-36 lg:pt-14 gap-14"]}
    [:span {:class ["text-4xl/7 font-bold ml-4"]}
     [mm-ui-navigation/navigation {:href (navigation/href ::mm-routes/articles)
                                   :text (mm-i18n-translate/tr :articles)
                                   :dark? true}]]
    [:div {:class ["grid justify-items-stretch gap-6 mx-auto w-full"]}
     (doall (for [{:keys [title id]
                   :as article}
                  articles/articles]
              ^{:key title}
              [ui-articles/article-card
               (merge article
                      {:on-click #(rf/dispatch [:nav/navigate
                                                ::mm-routes/article
                                                {:article-id (name
                                                              id)}])})]))]]])

(defn home
  "Functional component for displaying mateuszmazurczak page sections."
  []
  [mateuszmazurczak-page])
