(ns mateuszmazurczak.application.home.page-data
  "Home page data preparation - application layer orchestration."
  (:require
   [mateuszmazurczak.adapters.navigation.routes   :as-alias mm-routes]
   [mateuszmazurczak.application.home.page-schema :as page-schema]
   [mateuszmazurczak.frontend-i18n                :as fi18n]
   [mateuszmazurczak.ports.events                 :as events]))

;; =============================================================================
;; Data Builders
;; =============================================================================

(defn- build-raw-home-data
  "Build raw home page data structure."
  [articles-data]
  {:about-me-section {:welcome-text [:i18n :hi-mati]
                      :description [:i18n :i-like-simplicity]
                      :contact-info [:i18n :contact-me]}
   :navigation {:text [:i18n :articles]
                :dark-mode true}
   :articles articles-data})

(defn- add-navigation-click-handlers
  "Add navigation click handlers to articles."
  [articles-data]
  (mapv (fn [article]
          (assoc article
                 :on-click
                 [:dispatch
                  [:nav/navigate ::mm-routes/article {:article-id (name (:id article))} nil]]))
        articles-data))

(defn build-home-page-data
  "Build complete home page data with navigation and handlers."
  [articles-data navigation-href]
  (let [raw-data (build-raw-home-data articles-data)]
    (-> raw-data
        (assoc-in [:navigation :href] navigation-href)
        (update :articles add-navigation-click-handlers))))

;; =============================================================================
;; UI Data Preparation
;; =============================================================================

(defn prepare-ui-data
  "Prepare home page data for UI display.
   
   Orchestrates marker transformations and validation to convert
   raw app-db data into component-ready UI data."
  [raw-data]
  (let [processed-data (-> raw-data
                           fi18n/i18n-markers->translation
                           events/dispatch-markers->handlers)
        valid? (page-schema/valid-home-page-data? processed-data)]
    (if valid?
      {:data processed-data
       :valid? true}
      {:data processed-data
       :valid? false
       :error {:id ::home-translation-failed
               :data (page-schema/explain-home-page-data processed-data)
               :actual-data processed-data}})))
