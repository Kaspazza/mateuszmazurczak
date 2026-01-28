(ns mateuszmazurczak.domain.pages.qr-codes
  "Domain logic for QR codes page.
   Pure functions for data transformation and state management."
  (:require
   [mateuszmazurczak.domain.qr-codes.generator :as gen]))

;; =============================================================================
;; Initial State
;; =============================================================================

(def initial-page-data
  "Initial state for QR codes page."
  {:input ""
   :size 300
   :format :zip
   :show-label? false
   :preview-codes []
   :generating? false
   :errors []
   :loading? false})

;; =============================================================================
;; Size Options
;; =============================================================================

(def size-options
  "Available QR code size options."
  [{:value 100
    :label "100px (Small)"}
   {:value 200
    :label "200px"}
   {:value 300
    :label "300px (Default)"}
   {:value 400
    :label "400px"}
   {:value 500
    :label "500px (Large)"}])

;; =============================================================================
;; Format Options
;; =============================================================================

(def format-options
  "Available output format options."
  [{:value :zip
    :label "ZIP (PNG images)"}
   {:value :pdf
    :label "PDF (one QR per page)"}])

;; =============================================================================
;; Pure Domain Functions
;; =============================================================================

(defn parse-input-count
  "Count how many QR codes will be generated from input."
  [input]
  (count (gen/parse-input input)))

(defn generate-preview-codes
  "Generate preview QR codes from input.
   Limits to first 10 for performance."
  [input size show-label?]
  (let [contents (gen/parse-input input)
        limited (take 10 contents)
        result (gen/generate-batch (vec limited) :size size :show-label? show-label?)]
    (if (:success result)
      {:codes (:codes result)
       :total (count contents)
       :errors []}
      {:codes []
       :total 0
       :errors (:errors result)})))

(defn update-page-input
  "Update input and clear preview."
  [page-data input]
  (assoc page-data :input input :preview-codes [] :errors []))

(defn update-page-size
  "Update size and regenerate preview if input exists."
  [page-data size]
  (let [updated (assoc page-data :size size)]
    (if (seq (:input updated))
      (let [{:keys [codes errors]} (generate-preview-codes (:input updated) size (:show-label? updated))]
        (assoc updated :preview-codes codes :errors errors))
      updated)))

(defn update-page-format
  "Update output format."
  [page-data format]
  (assoc page-data :format format))

(defn update-page-show-label
  "Toggle whether to show QR code value as label below QR code."
  [page-data show-label?]
  (let [updated (assoc page-data :show-label? show-label?)]
    (if (seq (:preview-codes updated))
      ;; Regenerate preview if codes already exist to show updated labels
      (let [{:keys [codes errors]} (generate-preview-codes (:input updated) (:size updated) show-label?)]
        (assoc updated :preview-codes codes :errors errors))
      updated)))

(defn generate-page-preview
  "Generate preview codes for current input."
  [page-data]
  (let [{:keys [input size show-label?]} page-data
        {:keys [codes errors]} (generate-preview-codes input size show-label?)]
    (assoc page-data :preview-codes codes :errors errors :generating? false)))

;; =============================================================================
;; UI Data Preparation
;; =============================================================================

(defn prepare-ui-data
  "Prepare page data for UI consumption.
   Adds computed values and validation."
  [raw-data]
  (let [data (or raw-data initial-page-data)
        input-count (parse-input-count (:input data))
        has-input? (pos? input-count)
        can-download? (and has-input? (empty? (:errors data)))]
    {:data (assoc data
                  :input-count input-count
                  :has-input? has-input?
                  :can-download? can-download?
                  :size-options size-options
                  :format-options format-options
                  :preview-count (count (:preview-codes data))
                  :showing-preview? (pos? (count (:preview-codes data))))
     :valid? true
     :error nil}))
