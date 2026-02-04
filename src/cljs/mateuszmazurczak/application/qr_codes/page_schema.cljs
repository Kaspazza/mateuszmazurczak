(ns mateuszmazurczak.application.qr-codes.page-schema
  "QR codes page data schemas and validation."
  (:require
   [malli.core                                 :as m]
   [malli.error                                :as me]
   [mateuszmazurczak.domain.qr-codes.generator :as gen]
   [mateuszmazurczak.domain.qr-codes.schema    :as qr-schema]))

;; =============================================================================
;; Page Data Schema (stored in app-db)
;; =============================================================================

(def QrCodesPageData
  "Schema for QR codes page data stored in app-db.
   
   Includes UI state (generating?, loading?) alongside domain data."
  [:map
   [:input :string]
   [:size qr-schema/QrSize]
   [:format qr-schema/OutputFormat]
   [:error-correction qr-schema/ErrorCorrectionLevel]
   [:show-label? :boolean]
   [:pdf-layout qr-schema/PdfLayout]
   [:pdf-custom-cols [:or :string [:int {:min 1}]]]
   [:pdf-custom-rows [:or :string [:int {:min 1}]]]
   [:pdf-custom-qr-size-cm [:or :string [:double {:min 0.5}]]]
   [:preview-codes [:vector qr-schema/GeneratedQrCode]]
   [:generating? :boolean]
   [:errors [:vector :string]]
   [:show-validation-errors? :boolean]
   [:loading? {:optional true}
    :boolean]
   [:download-progress {:optional true}
    [:maybe
     [:map
      [:current {:optional true}
       [:maybe :int]]
      [:total {:optional true}
       [:maybe :int]]
      [:status-key {:optional true}
       :keyword]]]]])

(defn valid-page-data?
  "Validate QR codes page data against schema."
  [data]
  (m/validate QrCodesPageData data))

(defn explain-page-data
  "Explain validation errors for QR codes page data."
  [data]
  (me/humanize (m/explain QrCodesPageData data)))

;; =============================================================================
;; UI Data Schema (denormalized for components)
;; =============================================================================

(def QrCodesPageUIData
  "Schema for QR codes page UI data after translation and handler transformation.
   
   This is fully denormalized for component consumption with computed properties,
   handlers, and translated text."
  [:map
   [:input :string]
   [:size qr-schema/QrSize]
   [:format qr-schema/OutputFormat]
   [:error-correction qr-schema/ErrorCorrectionLevel]
   [:show-label? :boolean]
   [:pdf-layout qr-schema/PdfLayout]
   [:pdf-custom-cols [:or :string [:int {:min 1}]]]
   [:pdf-custom-rows [:or :string [:int {:min 1}]]]
   [:pdf-custom-qr-size-cm [:or :string [:double {:min 0.5}]]]
   [:preview-codes [:vector qr-schema/GeneratedQrCode]]
   [:generating? :boolean]
   [:errors [:vector :string]]
   [:show-validation-errors? :boolean]
   [:loading? {:optional true}
    :boolean]
   [:download-progress {:optional true}
    [:maybe
     [:map
      [:current {:optional true}
       [:maybe :int]]
      [:total {:optional true}
       [:maybe :int]]
      [:status-key {:optional true}
       :keyword]]]]
   [:input-count :int]
   [:has-input? :boolean]
   [:can-download? :boolean]
   [:size-options [:vector :any]]
   [:format-options [:vector :any]]
   [:pdf-layout-options [:vector :any]]
   [:preview-count :int]
   [:preview-display-size :int]
   [:showing-preview? :boolean]
   [:more-codes-count :int]
   [:input-hint :string]
   [:handlers [:map-of :keyword fn?]]
   [:text [:map-of :keyword :string]]])

(defn valid-ui-data?
  "Validate QR codes page UI data against schema."
  [data]
  (m/validate QrCodesPageUIData data))

(defn explain-ui-data
  "Explain validation errors for QR codes page UI data."
  [data]
  (me/humanize (m/explain QrCodesPageUIData data)))

;; =============================================================================
;; Initial Data
;; =============================================================================

(defn initial-page-data
  "Initial state for QR codes page.
   
   Uses domain defaults for QR generation settings."
  []
  {:input ""
   :size gen/default-qr-pixel-size
   :format :zip
   :error-correction :medium
   :show-label? false
   :pdf-layout :per-page-6  ; 6 per page (2×3, 5cm) - large, scannable default
   :pdf-custom-cols "4"       ; Custom defaults distinct from all presets (stored as strings)
   :pdf-custom-rows "4"
   :pdf-custom-qr-size-cm "3.5"
   :preview-codes []
   :generating? false
   :errors []
   :show-validation-errors? false
   :loading? false
   :download-progress nil})
