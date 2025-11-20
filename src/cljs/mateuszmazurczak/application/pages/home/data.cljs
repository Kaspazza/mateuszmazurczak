(ns mateuszmazurczak.application.pages.home.data
  "Home page application-level data builders and transformations.
   
   This layer orchestrates domain data (articles) with infrastructure (navigation)
   to build the final page data structure."
  (:require
   [mateuszmazurczak.adapters.navigation.routes :as-alias mm-routes]
   [mateuszmazurczak.domain.articles.core       :as articles]
   [mateuszmazurczak.ports.navigation           :as navigation]))

(defn- transform-articles
  "Transform articles data for home page display"
  [articles-data]
  (mapv (fn [{:keys [id]
              :as article}]
          (-> article
              (select-keys [:id :title :description :date :tags :img])
              (assoc :on-click
                     [:dispatch [:nav/navigate ::mm-routes/article {:article-id (name id)}]])))
        articles-data))

(defn build-home-page-data
  "Build raw home page data with i18n markers.
   
   Uses [:i18n :key] markers for translatable content.
   Translations are computed in subscriptions via translate-tree for reactivity.
   
   Returns a map with raw content (no loading state - that's managed by events)."
  []
  {:navigation {:href (navigation/href ::mm-routes/articles)}
   :articles (transform-articles articles/articles)})
