(ns mateuszmazurczak.ui.pages.home
  "Customer app home page assembly"
  (:require
   [mateuszmazurczak.ui.articles              :as ui-articles]
   [mateuszmazurczak.ui.components.navigation :as mm-ui-navigation]))

(defn about-me
  [{:keys [welcome-text description contact-info]}]
  [:div {:class "hero-content flex-col lg:flex-row max-w-screen justify-evenly"
         :style {:background "linear-gradient(85deg, rgb(170 137 173) 41%, #fff 100%)"}}
   [:div {:style {:height "100%"
                  :position "relative"
                  :background-color "#bea5c2 "}}]
   [:div {:class "block  overflow-hidden w-80"}
    [:img {:src "img/mateusz_mazurczak.png"}]]
   [:div
    [:h1 {:class "text-2xl md:text-3xl lg:text-5xl font-bold"}
     welcome-text]
    [:p {:class "py-6 text-md md:text-xl lg:text-3xl"}
     description
     [:br]]
    [:p {:class "py-6 text-md md:text-xl lg:text-3xl"}
     contact-info]]])

(defn home
  [{:keys [about-me-section navigation articles]}]
  [:div
   [about-me about-me-section]
   [:div {:class ["flex flex-col pt-8 p-12 lg:p-36 lg:pt-14 gap-14"]}
    [:span {:class ["text-4xl/7 font-bold ml-4"]}
     [mm-ui-navigation/navigation {:href (:href navigation)
                                   :text (:text navigation)
                                   :dark? (:dark-mode navigation)}]]
    [:div {:class ["grid justify-items-stretch gap-6 mx-auto w-full"]}
     (doall (for [{:keys [title]
                   :as article}
                  articles]
              ^{:key title} [ui-articles/article-card article]))]]])
