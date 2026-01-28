(ns mateuszmazurczak.domain.aoc.validation
  "AoC solution form validation."
  (:require
   [clojure.string :as str]
   [malli.core     :as m]))

;; =============================================================================
;; Schemas
;; =============================================================================

(def Year [:and :int [:>= 2015] [:<= 2025]])

(def Challenge [:and :int [:>= 1] [:<= 24]])

(def ContentType [:enum :code-snippet :repo-link])

(def SolutionForm
  "Schema for the upload form data (UI form state).
   
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

(def valid-solution-form? (m/validator SolutionForm))

;; =============================================================================
;; URL Validation
;; =============================================================================

(defn url?
  "Check if string is a valid URL (basic validation)."
  [s]
  (when (string? s) (or (str/starts-with? s "http://") (str/starts-with? s "https://"))))

;; =============================================================================
;; Form Validation
;; =============================================================================

(defn validate-solution-form
  "Validate solution form and return field-level errors.
   
   Returns nil if valid, or a map of field -> i18n marker if invalid."
  [form]
  (when-not (valid-solution-form? form)
    (let [errors {}
          author-name (:author-name form)
          content (:content form)
          content-type (:content-type form)
          is-repo-link? (= content-type :repo-link)]
      (cond-> errors
        (or (nil? author-name) (str/blank? author-name)) (assoc :author-name [:i18n :name-required])
        (or (nil? content) (str/blank? content)) (assoc :content [:i18n :content-required])
        (and is-repo-link? (not (url? content))) (assoc :content [:i18n :invalid-url])))))
