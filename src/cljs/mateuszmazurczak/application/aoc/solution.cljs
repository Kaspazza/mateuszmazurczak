(ns mateuszmazurczak.application.aoc.solution
  "Application-layer orchestration for AOC solutions (frontend).
   
   Contains form preparation logic (web-specific concerns).
   Imports domain/ and ports/ only."
  (:require
   [mateuszmazurczak.domain.aoc.solution :as solution]))

;; =============================================================================
;; Solution Form
;; =============================================================================

(defn prepare-solution-payload
  "Prepare form data for submission by normalizing GitHub username."
  [form]
  (-> form
      (update :github-username solution/parse-github-username)))
