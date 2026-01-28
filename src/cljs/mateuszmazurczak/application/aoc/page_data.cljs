(ns mateuszmazurczak.application.aoc.page-data
  "AoC page data preparation - application layer orchestration."
  (:require
   [mateuszmazurczak.application.aoc.page-schema :as page-schema]
   [mateuszmazurczak.application.aoc.solution    :as app-solution]
   [mateuszmazurczak.domain.aoc.solution         :as solution]
   [mateuszmazurczak.frontend-i18n               :as fi18n]
   [mateuszmazurczak.ports.events                :as events]
   [mateuszmazurczak.ports.logging               :as log]))

(defn prepare-ui-data
  "Prepare AoC page data for UI display.
   
   Orchestrates domain transformations and port operations to convert
   raw app-db data into component-ready UI data with validation."
  [raw-data solutions-entities theme admin-logged-in? logger]
  (let [solution-ids (get-in raw-data
                             (page-schema/relative-path page-schema/*aoc-solution-ids-path*))
        denormalized-solutions (app-solution/denormalize-and-enrich-solutions
                                solution-ids
                                solutions-entities
                                solution/denormalize-solutions)
        solutions-text-raw
        (get-in raw-data (page-schema/relative-path page-schema/*aoc-solutions-text-path*))
        solutions-text (fi18n/i18n-markers->translation solutions-text-raw)
        prepared-solutions
        (->> denormalized-solutions
             (mapv #(app-solution/enrich-solution-with-ui-context % theme solutions-text))
             solution/sort-solutions-by-votes)
        ui-data
        (-> raw-data
            events/dispatch-markers->handlers
            fi18n/i18n-markers->translation
            (assoc-in (page-schema/relative-path page-schema/*aoc-solutions-path*)
                      prepared-solutions)
            (update-in (page-schema/relative-path page-schema/*aoc-solutions-data-path*)
                       dissoc
                       :solution-ids)
            (assoc-in (page-schema/relative-path page-schema/*aoc-solutions-theme-path*) theme)
            (assoc-in (page-schema/relative-path page-schema/*aoc-solutions-admin-logged-in-path*)
                      admin-logged-in?)
            (update-in (page-schema/relative-path page-schema/*aoc-solutions-path*)
                       events/dispatch-markers->handlers))
        valid? (page-schema/valid-aoc-page-ui-data? ui-data)
        explanation (when-not valid? (page-schema/explain-aoc-page-ui-data ui-data))]
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
