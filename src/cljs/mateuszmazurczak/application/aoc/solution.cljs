(ns mateuszmazurczak.application.aoc.solution
  "Application-layer solution enrichment and UI preparation.
   
   Contains web-specific logic for preparing solutions for display.")

;; =============================================================================
;; UI Enrichment
;; =============================================================================

(defn enrich-solution-with-vote-handlers
  "Add vote handler dispatch markers to solution."
  [solution]
  (let [solution-id (:id solution)]
    (assoc solution
           :on-vote-best-practices [:dispatch [:aoc/vote solution-id :best-practices]]
           :on-vote-clever [:dispatch [:aoc/vote solution-id :clever]])))

(defn enrich-solution-with-ui-context
  "Enrich solution with UI-specific context (theme, text)."
  [solution theme text]
  (assoc solution :theme theme :text text))

(defn denormalize-and-enrich-solutions
  "Denormalize solution IDs and enrich with UI handlers."
  [solution-ids entities denormalize-fn]
  (mapv enrich-solution-with-vote-handlers (denormalize-fn solution-ids entities)))
