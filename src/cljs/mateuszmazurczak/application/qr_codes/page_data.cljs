(ns mateuszmazurczak.application.qr-codes.page-data
  "QR codes page data preparation - application layer orchestration."
  (:require
   [malli.core                             :as m]
   [malli.error                            :as me]
   [mateuszmazurczak.domain.pages.qr-codes :as qr-domain]
   [mateuszmazurczak.frontend-i18n         :as fi18n]
   [mateuszmazurczak.ports.events          :as events]))

;; =============================================================================
;; UI Data Builders
;; =============================================================================

(defn- build-size-options
  "Build size options for UI selector."
  []
  (mapv (fn [size]
          {:value size
           :label (str size "px")})
        qr-domain/valid-sizes))

(defn- build-format-options
  "Build format options for UI selector."
  []
  [{:value :zip
    :label [:i18n :format-zip]}
   {:value :pdf
    :label [:i18n :format-pdf]}])

(defn- build-handlers
  "Build handler dispatch markers for UI interactions."
  []
  {:on-update-input [:dispatch [:qr-codes/update-input]]
   :on-update-size [:dispatch [:qr-codes/update-size]]
   :on-update-format [:dispatch [:qr-codes/update-format]]
   :on-update-show-label [:dispatch [:qr-codes/update-show-label]]
   :on-generate-preview [:dispatch [:qr-codes/generate-preview]]
   :on-download [:dispatch [:qr-codes/download]]})

(defn- build-input-hint
  "Build input hint i18n marker based on input count."
  [input-count]
  (if (pos? input-count)
    [:i18n :qr-codes-will-be-generated {:1 input-count}]
    [:i18n :enter-values-hint]))

(defn- build-text-markers
  "Build i18n markers for all text in the UI."
  [preview-count input-count]
  {:title [:i18n :qr-code-generator]
   :description [:i18n :generate-multiple-qr-codes]
   :qr-code-values [:i18n :qr-code-values]
   :enter-values-placeholder [:i18n :enter-values-placeholder]
   :qr-code-size [:i18n :qr-code-size]
   :select-size [:i18n :select-size]
   :output-format [:i18n :output-format]
   :select-format [:i18n :select-format]
   :show-label [:i18n :show-label]
   :show-label-description [:i18n :show-label-description]
   :generate-preview [:i18n :generate-preview]
   :download [:i18n :download]
   :preview [:i18n :preview]
   :errors [:i18n :errors]
   :showing-preview-count [:i18n :showing-preview-count {:1 preview-count :2 input-count}]
   :more-codes-hidden [:i18n :more-codes-hidden {:1 (- input-count preview-count)}]})

(defn- enrich-with-ui-data
  "Enrich domain data with UI-specific concerns."
  [page-data]
  (let [derived (qr-domain/calculate-derived-state page-data)
        {:keys [input-count preview-count]} derived]
    (merge page-data
           derived
           {:size-options (build-size-options)
            :format-options (build-format-options)
            :preview-display-size qr-domain/preview-display-size
            :input-hint (build-input-hint input-count)
            :handlers (build-handlers)
            :text (build-text-markers preview-count input-count)})))

;; =============================================================================
;; UI Data Schema & Validation
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
   [:loading? {:optional true} :boolean]
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

(defn- valid-ui-data?
  "Validate QR codes page UI data against schema."
  [data]
  (m/validate QrCodesPageUIData data))

(defn- explain-ui-data
  "Explain validation errors for QR codes page UI data."
  [data]
  (me/humanize (m/explain QrCodesPageUIData data)))

;; =============================================================================
;; Public API
;; =============================================================================

(defn prepare-ui-data
  "Prepare QR codes page data for UI display.
   
   Orchestrates domain calculations, UI enrichment, marker transformations,
   and validation to convert raw app-db data into component-ready UI data."
  [raw-data]
  (let [data (or raw-data qr-domain/initial-page-data)
        ui-data (-> data
                    enrich-with-ui-data
                    fi18n/i18n-markers->translation
                    events/dispatch-markers->handlers)
        valid? (valid-ui-data? ui-data)]
    (if valid?
      {:data ui-data
       :valid? true}
      {:data ui-data
       :valid? false
       :error {:id ::qr-codes-ui-data-invalid
               :explanation (explain-ui-data ui-data)
               :actual-data ui-data}})))
