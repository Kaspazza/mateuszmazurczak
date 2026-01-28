(ns mateuszmazurczak.application.admin.page-data
  "Admin page data preparation - application layer orchestration."
  (:require
   [mateuszmazurczak.application.admin.page-schema :as page-schema]
   [mateuszmazurczak.frontend-i18n                 :as fi18n]
   [mateuszmazurczak.ports.events                  :as events]
   [mateuszmazurczak.ports.logging                 :as log]))

(defn prepare-ui-data
  "Prepare admin page data for UI display.
   
   Orchestrates marker transformations and validation to convert
   raw app-db data into component-ready UI data."
  [raw-data logged-in? logger]
  (let [ui-data (-> raw-data
                    (assoc :logged-in? logged-in?)
                    events/dispatch-markers->handlers
                    fi18n/i18n-markers->translation)
        valid? (page-schema/valid-admin-page-ui-data? ui-data)
        explanation (when-not valid? (page-schema/explain-admin-page-ui-data ui-data))]
    (when-not valid?
      (log/error! logger
                  {:error (ex-info "Admin page validation failed"
                                   {:type ::admin-validation-failed
                                    :explanation explanation
                                    :raw-data raw-data})}))
    (if valid?
      {:data ui-data
       :valid? true}
      {:data ui-data
       :valid? false
       :error {:id ::admin-validation-failed
               :actual-data raw-data
               :explanation explanation}})))
