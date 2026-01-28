(ns mateuszmazurczak.domain.aoc.selection "Year and challenge selection logic.")

;; =============================================================================
;; Constants
;; =============================================================================

(def years (vec (range 2015 2026)))

(defn challenges-for-year
  "Returns available challenges for given year.
   2025 only has 12 challenges (current year), others have 24."
  [year]
  (let [max-challenge (if (= year 2025) 12 24)] (vec (range 1 (inc max-challenge)))))

;; =============================================================================
;; Validation
;; =============================================================================

(defn valid-year? [year] (some #{year} years))

(defn valid-challenge-for-year?
  "Check if challenge is valid for given year."
  [year challenge]
  (some #{challenge} (challenges-for-year year)))

;; =============================================================================
;; Options Builders
;; =============================================================================

(defn build-years-options
  "Build year selector options from available years."
  []
  (mapv (fn [y]
          {:value y
           :label (str y)})
        (reverse years)))

(defn build-challenges-options
  "Build challenge selector options for given year."
  [year]
  (mapv (fn [c]
          {:value c
           :label (str "Day " c)})
        (challenges-for-year year)))

;; =============================================================================
;; Selection Resolution
;; =============================================================================

(defn resolve-year-challenge
  "Resolve and validate year/challenge values."
  [url-year url-challenge default-year]
  (let [valid-year? (valid-year? url-year)
        year (if valid-year? url-year default-year)
        valid-challenge? (and valid-year? (valid-challenge-for-year? url-year url-challenge))
        challenge (if valid-challenge? url-challenge (first (challenges-for-year year)))]
    {:year year
     :challenge challenge}))

(defn resolve-year-selection
  "Resolve state after year selection change."
  [year]
  (let [first-challenge (first (challenges-for-year year))
        challenges-options (build-challenges-options year)]
    {:year year
     :challenge first-challenge
     :challenges-options challenges-options}))
