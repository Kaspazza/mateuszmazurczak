(ns mateuszmazurczak.application.home.page-data
  "Home page data preparation - application layer orchestration."
  (:require
   [mateuszmazurczak.domain.pages.home :as home-domain]
   [mateuszmazurczak.frontend-i18n     :as fi18n]
   [mateuszmazurczak.ports.events      :as events]))

(defn prepare-ui-data
  "Prepare home page data for UI display.
   
   Orchestrates marker transformations and validation to convert
   raw app-db data into component-ready UI data."
  [raw-data]
  (let [processed-data (-> raw-data
                           fi18n/i18n-markers->translation
                           events/dispatch-markers->handlers)
        valid? (home-domain/valid-home-page-data? processed-data)]
    (if valid?
      {:data processed-data
       :valid? true}
      {:data processed-data
       :valid? false
       :error {:id ::home-translation-failed
               :data (home-domain/explain-home-page-data processed-data)
               :actual-data processed-data}})))
