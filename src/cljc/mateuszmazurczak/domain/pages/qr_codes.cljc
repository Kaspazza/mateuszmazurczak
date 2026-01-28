(ns mateuszmazurczak.domain.pages.qr-codes
  "Domain logic for QR codes page.
   Business rules, data transformations, and state management."
  (:require
   [malli.core                                  :as m]
   [malli.error                                 :as me]
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
;; Constants
;; =============================================================================

(def preview-display-size
  "Size for QR code preview display (actual export uses configured size)."
  150)

;; =============================================================================
;; Business Constants
;; =============================================================================

(def valid-sizes
  "Valid QR code sizes in pixels."
  [100 200 300 400 500])

(def valid-formats
  "Valid output formats."
  #{:zip :pdf})

;; =============================================================================
;; Domain Functions
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
;; Derived State Calculations
;; =============================================================================

(defn calculate-derived-state
  "Calculate derived state from page data."
  [page-data]
  (let [input-count (parse-input-count (:input page-data))
        preview-count (count (:preview-codes page-data))
        has-input? (pos? input-count)
        can-download? (and has-input? (empty? (:errors page-data)))
        showing-preview? (pos? preview-count)
        more-codes-count (- input-count preview-count)]
    {:input-count input-count
     :preview-count preview-count
     :has-input? has-input?
     :can-download? can-download?
     :showing-preview? showing-preview?
     :more-codes-count more-codes-count}))

;; =============================================================================
;; Schema & Validation
;; =============================================================================

(def QrCodesPageData
  "Schema for QR codes page data."
  [:map
   [:input :string]
   [:size :int]
   [:format [:enum :zip :pdf]]
   [:show-label? :boolean]
   [:preview-codes [:vector :any]]
   [:generating? :boolean]
   [:errors [:vector :string]]
   [:loading? {:optional true} :boolean]])

(defn valid-page-data?
  "Validate QR codes page data against schema."
  [data]
  (m/validate QrCodesPageData data))

(defn explain-page-data
  "Explain validation errors for QR codes page data."
  [data]
  (me/humanize (m/explain QrCodesPageData data)))
