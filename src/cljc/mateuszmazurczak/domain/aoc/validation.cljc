(ns mateuszmazurczak.domain.aoc.validation
  "AoC solution domain validation."
  (:require
   [clojure.string :as str]
   [malli.core     :as m]))

;; =============================================================================
;; Schemas
;; =============================================================================

(def Year [:and :int [:>= 2015] [:<= 2025]])

(def Challenge [:and :int [:>= 1] [:<= 24]])

(def ContentType [:enum :code-snippet :repo-link])

(def Solution
  "Schema for AoC solution entity.
   
   Note: :github-username can be entered in various formats (username, @username, full URL).
   Use prepare-solution-payload to normalize before submission."
  [:map
   [:author-name [:string {:min 1}]]
   [:github-username {:optional true}
    [:maybe :string]]
   [:content-type ContentType]
   [:content [:string {:min 1}]]
   [:year Year]
   [:challenge Challenge]])

(def valid-solution? (m/validator Solution))

;; =============================================================================
;; URL Validation
;; =============================================================================

(defn url?
  "Check if string is a valid URL (basic validation)."
  [s]
  (when (string? s) (or (str/starts-with? s "http://") (str/starts-with? s "https://"))))
