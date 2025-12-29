(ns mateuszmazurczak.application.aoc.page-data
  "AoC page data preparation - application layer orchestration."
  (:require
   [mateuszmazurczak.domain.pages.aoc :as aoc-domain]
   [mateuszmazurczak.frontend-i18n    :as fi18n]
   [mateuszmazurczak.ports.events     :as events]
   [mateuszmazurczak.ports.logging    :as log]))

(defn prepare-ui-data
  "Prepare AoC page data for UI display.
   
   Orchestrates domain transformations and port operations to convert
   raw app-db data into component-ready UI data with validation."
  [raw-data solutions-entities theme admin-logged-in? logger]
  (let [solution-ids (get-in raw-data (aoc-domain/relative-path aoc-domain/*aoc-solution-ids-path*))
        denormalized-solutions (aoc-domain/denormalize-solutions solution-ids solutions-entities)
        solutions-text (get-in raw-data
                               (aoc-domain/relative-path aoc-domain/*aoc-solutions-text-path*))
        prepared-solutions
        (aoc-domain/prepare-solutions-for-ui denormalized-solutions theme solutions-text)
        ui-data
        (-> raw-data
            events/dispatch-markers->handlers
            fi18n/i18n-markers->translation
            (assoc-in (aoc-domain/relative-path aoc-domain/*aoc-solutions-path*) prepared-solutions)
            (update-in (aoc-domain/relative-path aoc-domain/*aoc-solutions-data-path*)
                       dissoc
                       :solution-ids)
            (assoc-in (aoc-domain/relative-path aoc-domain/*aoc-solutions-theme-path*) theme)
            (assoc-in (aoc-domain/relative-path aoc-domain/*aoc-solutions-admin-logged-in-path*)
                      admin-logged-in?)
            (update-in (aoc-domain/relative-path aoc-domain/*aoc-solutions-path*)
                       events/dispatch-markers->handlers))
        valid? (aoc-domain/valid-aoc-page-ui-data? ui-data)
        explanation (when-not valid? (aoc-domain/explain-aoc-page-ui-data ui-data))]
    (when-not valid?
      (log/error! logger
                  {:error (ex-info "AOC page validation failed"
                                   {:type ::aoc-validation-failed
                                    :explanation explanation
                                    :raw-data raw-data})}))
    (if valid?
      {:data ui-data
       :valid? true}
      {:data ui-data
       :valid? false
       :error {:id ::aoc-validation-failed
               :actual-data raw-data
               :explanation explanation}})))
