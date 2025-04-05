(ns mateuszmazurczak.ui.pages.articles
  (:require
   [mateuszmazurczak.articles.core                :as articles]
   [mateuszmazurczak.articles.routing.big-picture :as articles-big-picture]
   [mateuszmazurczak.i18n.translate               :as mm-i18n-translate]
   [mateuszmazurczak.navigation.routes            :as-alias mm-routes]
   [mateuszmazurczak.ui.articles                  :as ui-articles]
   [re-frame.core                                 :as rf]))

(defn articles-page
  []
  [:div {:class ["mt-12"]}
   [:h1 {:class ["text-4xl/7 font-bold ml-4 mb-8"]}
    (mm-i18n-translate/tr :articles)]
   [:div {:class ["grid justify-items-stretch gap-6 mx-auto w-full"]}
    (doall (for [{:keys [title]
                  :as article}
                 articles/articles]
             ^{:key title}
             [ui-articles/article-card
              (merge article
                     {:on-click #(rf/dispatch [:nav/navigate
                                               (:route article)])})]))]])

(defn article-routing-big-picture
  []
  [ui-articles/article-wrap articles-big-picture/article])
