(ns mateuszmazurczak.application.aoc.solution
  "Application-layer orchestration for AOC solutions (frontend).
   
   Contains form preparation logic and UI enrichment (web-specific concerns).
   Imports domain/ and ports/ only."
  (:require
   [clojure.string                           :as str]
   [mateuszmazurczak.application.aoc.playground :as playground]
   [mateuszmazurczak.domain.aoc.playground      :as playground-domain]
   [mateuszmazurczak.domain.aoc.solution        :as solution]
   [mateuszmazurczak.domain.aoc.validation      :as validation]))

;; =============================================================================
;; Solution Form Validation
;; =============================================================================

(defn validate-solution-form
  "Validate solution form and return field-level errors with i18n markers.
   
   Returns nil if valid, or a map of field -> i18n marker if invalid.
   
   This is application-layer concern because it returns web-specific i18n markers."
  [form]
  (when-not (validation/valid-solution? form)
    (let [errors {}
          author-name (:author-name form)
          content (:content form)
          content-type (:content-type form)
          is-repo-link? (= content-type :repo-link)]
      (cond-> errors
        (or (nil? author-name) (str/blank? author-name)) (assoc :author-name [:i18n :name-required])
        (or (nil? content) (str/blank? content)) (assoc :content [:i18n :content-required])
        (and is-repo-link? (not (validation/url? content))) (assoc :content [:i18n :invalid-url])))))

;; =============================================================================
;; Solution Form Preparation
;; =============================================================================

(defn prepare-solution-payload
  "Prepare form data for submission by normalizing GitHub username."
  [form]
  (-> form
      (update :github-username solution/parse-github-username)))

;; =============================================================================
;; UI Enrichment
;; =============================================================================

(defn- long-code-threshold
  "Line count threshold to consider code as long and collapsible."
  []
  20)

(defn- is-long-code?
  "Check if content is long enough to warrant collapsing."
  [content]
  (when content
    (let [lines (str/split-lines content)]
      (> (count lines) (long-code-threshold)))))

(defn- is-long-url?
  "Check if URL is long enough to warrant collapsing."
  [url]
  (when url
    (> (count url) 100)))

(defn enrich-solution-with-vote-handlers
  "Add vote handler dispatch markers to solution."
  [solution]
  (let [solution-id (:id solution)]
    (assoc solution
           :on-vote-best-practices [:dispatch [:aoc/vote solution-id :best-practices]]
           :on-vote-clever [:dispatch [:aoc/vote solution-id :clever]])))

(defn enrich-solution-with-ui-context
  "Enrich solution with UI-specific context (theme, text).
   
   Also adds computed display properties:
   - :collapsible? - whether content should be collapsible
   - :playground-urls - map of {:squint url :cherry url} if applicable"
  [solution theme text]
  (let [content-type (:content-type solution)
        content (:content solution)
        collapsible? (case content-type
                       :code-snippet (is-long-code? content)
                       :repo-link (is-long-url? content)
                       false)
        playground-urls (when (playground-domain/should-show-playground? content-type)
                          {:squint (playground/squint-url content)
                           :cherry (playground/cherry-url content)})]
    (cond-> solution
      true (assoc :theme theme :text text)
      true (assoc :collapsible? collapsible?)
      playground-urls (assoc :playground-urls playground-urls))))

(defn denormalize-and-enrich-solutions
  "Denormalize solution IDs and enrich with UI handlers."
  [solution-ids entities denormalize-fn]
  (mapv enrich-solution-with-vote-handlers (denormalize-fn solution-ids entities)))

(defn sort-solutions-for-display
  "Sort solutions for display: user solutions first, then by votes."
  [solutions user-solution-ids]
  (let [sorted-by-votes (solution/sort-solutions-by-votes solutions)]
    (sort-by (fn [sol]
               (if (contains? user-solution-ids (:id sol))
                 0  ;; User solutions first
                 1)) ;; Others after
             sorted-by-votes)))
