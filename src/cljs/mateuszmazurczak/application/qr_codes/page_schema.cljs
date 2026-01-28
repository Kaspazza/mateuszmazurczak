(ns mateuszmazurczak.application.qr-codes.page-schema
  "QR codes page data schemas and validation."
  (:require
   [malli.core  :as m]
   [malli.error :as me]))

;; =============================================================================
;; Page Data Schema (stored in app-db)
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
   [:loading? {:optional true}
    :boolean]])

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
  "Schema for QR codes page UI data after translation and handler transformation."
  [:map
   [:input :string]
   [:size :int]
   [:format [:enum :zip :pdf]]
   [:show-label? :boolean]
   [:preview-codes [:vector :any]]
   [:generating? :boolean]
   [:errors [:vector :string]]
   [:loading? {:optional true}
    :boolean]
   [:input-count :int]
   [:has-input? :boolean]
   [:can-download? :boolean]
   [:size-options [:vector :any]]
   [:format-options [:vector :any]]
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
  "Initial state for QR codes page."
  []
  {:input ""
   :size 300
   :format :zip
   :show-label? false
   :preview-codes []
   :generating? false
   :errors []
   :loading? false})
