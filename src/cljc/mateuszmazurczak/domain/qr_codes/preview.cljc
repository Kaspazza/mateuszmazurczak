(ns mateuszmazurczak.domain.qr-codes.preview
  "QR code preview generation and state calculations."
  (:require
   [mateuszmazurczak.domain.qr-codes.generator :as gen]))

;; =============================================================================
;; Constants
;; =============================================================================

(def preview-display-size
  "Size for QR code preview display (actual export uses configured size)."
  150)

(def valid-sizes "Valid QR code sizes in pixels." [100 200 300 400 500])

(def valid-formats "Valid output formats." #{:zip :pdf})

;; =============================================================================
;; Preview Generation
;; =============================================================================

(defn parse-input-count
  "Count how many QR codes will be generated from input."
  [input]
  (count (gen/parse-input input)))

(defn generate-preview-codes
  "Generate preview QR codes from input. Limits to first 10 for performance."
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


