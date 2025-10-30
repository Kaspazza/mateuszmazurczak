(ns mateuszmazurczak.application.pages.home.data
  "Home page data builders and transformations"
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
              (assoc
               :on-click
               [:dispatch
                [:nav/navigate ::mm-routes/article {:article-id (name id)}]])))
        articles-data))

(defn build-home-page-data
  "Build raw home page data with i18n markers.
   
   Uses [:i18n :key] markers for translatable content.
   Translations are computed in subscriptions via translate-tree for reactivity.
   
   Returns a map with raw content (no loading state - that's managed by events)."
  []
  {:navigation {:href (navigation/href ::mm-routes/articles)}
   :articles (transform-articles articles/articles)})

(defn initial-home-data
  "Returns initial home page data structure for app-db initialization.
   
   Starts with loading state. Actual data is loaded via :home/on-route-enter event.
   This avoids dependency on router during system initialization."
  []
  {:loading? true
   :about-me-section {:welcome-text [:i18n :hi-mati]
                      :description [:i18n :i-like-simplicity]
                      :contact-info [:i18n :contact-me]}
   :navigation {:text [:i18n :articles]
                :dark-mode true}
   :articles []})
