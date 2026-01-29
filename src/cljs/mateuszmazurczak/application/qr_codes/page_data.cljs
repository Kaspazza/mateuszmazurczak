(ns mateuszmazurczak.application.qr-codes.page-data
  "QR codes page data preparation - application layer orchestration."
  (:require
   [mateuszmazurczak.application.qr-codes.page-schema :as page-schema]
   [mateuszmazurczak.domain.qr-codes.preview          :as qr-preview]
   [mateuszmazurczak.frontend-i18n                    :as fi18n]
   [mateuszmazurczak.ports.events                     :as events]))

;; =============================================================================
;; Page State Updates
;; =============================================================================

(defn update-page-input
  "Update input and clear preview."
  [page-data input]
  (assoc page-data :input input :preview-codes [] :errors []))

(defn update-page-size
  "Update size and regenerate preview if input exists."
  [page-data size]
  (let [updated (assoc page-data :size size)]
    (if (seq (:input updated))
      (let [{:keys [codes errors]}
            (qr-preview/generate-preview-codes (:input updated) size (:show-label? updated))]
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
      (let [{:keys [codes errors]}
            (qr-preview/generate-preview-codes (:input updated) (:size updated) show-label?)]
        (assoc updated :preview-codes codes :errors errors))
      updated)))

(defn generate-page-preview
  "Generate preview codes for current input."
  [page-data]
  (let [{:keys [input size show-label?]} page-data
        {:keys [codes errors]} (qr-preview/generate-preview-codes input size show-label?)]
    (assoc page-data :preview-codes codes :errors errors :generating? false)))

;; =============================================================================
;; Derived State
;; =============================================================================

(defn calculate-derived-state
  "Calculate derived UI state from page data."
  [page-data]
  (let [input-count (qr-preview/parse-input-count (:input page-data))
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
;; UI Data Builders
;; =============================================================================

(defn- build-size-options
  "Build size options for UI selector."
  []
  (mapv (fn [size]
          {:value size
           :label (str size "px")})
        qr-preview/valid-sizes))

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
   :showing-preview-count [:i18n
                           :showing-preview-count
                           {:1 preview-count
                            :2 input-count}]
   :more-codes-hidden [:i18n :more-codes-hidden {:1 (- input-count preview-count)}]})

(defn- enrich-with-ui-data
  "Enrich domain data with UI-specific concerns."
  [page-data]
  (let [derived (calculate-derived-state page-data)
        {:keys [input-count preview-count]} derived]
    (merge page-data
           derived
           {:size-options (build-size-options)
            :format-options (build-format-options)
            :preview-display-size qr-preview/preview-display-size
            :input-hint (build-input-hint input-count)
            :handlers (build-handlers)
            :text (build-text-markers preview-count input-count)})))



;; =============================================================================
;; Public API
;; =============================================================================

(defn prepare-ui-data
  "Prepare QR codes page data for UI display.
   
   Orchestrates domain calculations, UI enrichment, marker transformations,
   and validation to convert raw app-db data into component-ready UI data."
  [raw-data]
  (let [data (or raw-data (page-schema/initial-page-data))
        ui-data (-> data
                    enrich-with-ui-data
                    fi18n/i18n-markers->translation
                    events/dispatch-markers->handlers)
        valid? (page-schema/valid-ui-data? ui-data)]
    (if valid?
      {:data ui-data
       :valid? true}
      {:data ui-data
       :valid? false
       :error {:id ::qr-codes-ui-data-invalid
               :explanation (page-schema/explain-ui-data ui-data)
               :actual-data ui-data}})))
