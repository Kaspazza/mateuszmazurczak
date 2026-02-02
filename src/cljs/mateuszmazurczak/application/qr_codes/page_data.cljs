(ns mateuszmazurczak.application.qr-codes.page-data
  "QR codes page data preparation - application layer orchestration."
  (:require
   [mateuszmazurczak.application.qr-codes.page-schema :as page-schema]
   [mateuszmazurczak.domain.qr-codes.generator        :as gen]
   [mateuszmazurczak.domain.qr-codes.preview          :as qr-preview]
   [mateuszmazurczak.frontend-i18n                    :as fi18n]
   [mateuszmazurczak.ports.events                     :as events]))

;; =============================================================================
;; Page State Updates
;; =============================================================================

(defn update-page-input
  "Update input and clear preview."
  [page-data input]
  (let [contents (gen/parse-input input)
        validation (when (seq contents)
                     (gen/validate-request {:contents contents
                                            :size (:size page-data)
                                            :format (:format page-data)}))
        errors (if (and validation (not (:valid? validation)))
                 (:errors validation)
                 [])]
    (assoc page-data :input input :preview-codes [] :errors errors)))

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

(defn- parse-int-with-fallback
  [value fallback]
  (let [parsed (js/parseInt value 10)]
    (if (js/isNaN parsed) fallback parsed)))

(defn- parse-float-with-fallback
  [value fallback]
  (let [parsed (js/parseFloat value)]
    (if (js/isNaN parsed) fallback parsed)))

(defn update-page-show-label
  "Toggle whether to show QR code value as label below QR code."
  [page-data show-label?]
  (let [updated (assoc page-data :show-label? show-label?)]
    (if (seq (:preview-codes updated))
      (let [{:keys [codes errors]}
            (qr-preview/generate-preview-codes (:input updated) (:size updated) show-label?)]
        (assoc updated :preview-codes codes :errors errors))
      updated)))

(defn update-page-pdf-layout
  "Update PDF layout preset selection."
  [page-data layout]
  (assoc page-data :pdf-layout layout))

(defn update-page-pdf-custom
  "Update custom PDF layout settings."
  [page-data field value]
  (case field
    :cols (let [current (:pdf-custom-cols page-data)
                parsed (parse-int-with-fallback value current)
                next-value (max 1 parsed)]
            (assoc page-data :pdf-custom-cols next-value))
    :rows (let [current (:pdf-custom-rows page-data)
                parsed (parse-int-with-fallback value current)
                next-value (max 1 parsed)]
            (assoc page-data :pdf-custom-rows next-value))
    :qr-size-cm (let [current (:pdf-custom-qr-size-cm page-data)
                      parsed (parse-float-with-fallback value current)
                      next-value (max 0.5 parsed)]
                  (assoc page-data :pdf-custom-qr-size-cm next-value))
    page-data))

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
        loading? (:loading? page-data)
        can-download? (and has-input? (empty? (:errors page-data)) (not loading?))
        showing-preview? (pos? preview-count)
        more-codes-count (- input-count preview-count)]
    {:input-count input-count
     :preview-count preview-count
     :has-input? has-input?
     :can-download? can-download?
     :showing-preview? showing-preview?
     :more-codes-count more-codes-count}))

;; =============================================================================
;; Download Preparation
;; =============================================================================

(defn build-download-batch
  "Prepare a single download batch for QR code export."
  [{:keys [batch-index total-batches format size]}]
  (let [base-filename "qr-codes"
        extension (if (= format :pdf) ".pdf" ".zip")
        filename (if (= total-batches 1)
                   (str base-filename extension)
                   (str base-filename
                        "-part-"
                        (format "%03d" (inc batch-index))
                        extension))]
    {:opts {:size size
            :format format
            :filename filename}}))

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

(def ^:private pdf-layout-presets
  {:avery-5160 {:cols 3
                :rows 10
                :qr-size-cm 2.0}
   :avery-5163 {:cols 2
                :rows 5
                :qr-size-cm 4.0}
   :grid-6 {:cols 2
            :rows 3
            :qr-size-cm 5.0}})

(def ^:private pdf-layout-defaults
  {:margin-cm 1.0
   :spacing-cm 0.3})

(defn- build-format-options
  "Build format options for UI selector."
  []
  [{:value :zip
    :label [:i18n :format-zip]}
   {:value :pdf
    :label [:i18n :format-pdf]}])

(defn- build-pdf-layout-options
  "Build PDF layout options for UI selector."
  []
  [{:value :avery-5160
    :label [:i18n :pdf-layout-avery-5160]}
   {:value :avery-5163
    :label [:i18n :pdf-layout-avery-5163]}
   {:value :grid-6
    :label [:i18n :pdf-layout-grid-6]}
   {:value :custom
    :label [:i18n :pdf-layout-custom]}])

(defn resolve-pdf-layout-config
  "Resolve PDF layout configuration for worker export."
  [page-data]
  (let [layout (:pdf-layout page-data)
        custom-config {:cols (:pdf-custom-cols page-data)
                       :rows (:pdf-custom-rows page-data)
                       :qr-size-cm (:pdf-custom-qr-size-cm page-data)}
        preset-config (get pdf-layout-presets layout custom-config)
        layout-config (if (= layout :custom) custom-config preset-config)]
    (merge pdf-layout-defaults layout-config)))

(defn- build-handlers
  "Build handler dispatch markers for UI interactions."
  []
  {:on-update-input [:dispatch [:qr-codes/update-input]]
   :on-update-size [:dispatch [:qr-codes/update-size]]
   :on-update-format [:dispatch [:qr-codes/update-format]]
   :on-update-show-label [:dispatch [:qr-codes/update-show-label]]
   :on-update-pdf-layout [:dispatch [:qr-codes/update-pdf-layout]]
   :on-update-pdf-custom [:dispatch [:qr-codes/update-pdf-custom]]
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
  [preview-count input-count download-progress]
  {:title [:i18n :qr-code-generator]
   :description [:i18n :generate-multiple-qr-codes]
   :qr-code-values [:i18n :qr-code-values]
   :enter-values-placeholder [:i18n :enter-values-placeholder]
   :qr-code-size [:i18n :qr-code-size]
   :select-size [:i18n :select-size]
   :output-format [:i18n :output-format]
   :select-format [:i18n :select-format]
   :format-pdf-description [:i18n :format-pdf-description]
   :pdf-layout [:i18n :pdf-layout]
   :select-pdf-layout [:i18n :select-pdf-layout]
   :pdf-layout-description [:i18n :pdf-layout-description]
   :pdf-custom-layout [:i18n :pdf-custom-layout]
   :pdf-custom-cols [:i18n :pdf-custom-cols]
   :pdf-custom-rows [:i18n :pdf-custom-rows]
   :pdf-custom-size-cm [:i18n :pdf-custom-size-cm]
   :show-label [:i18n :show-label]
   :show-label-description [:i18n :show-label-description]
   :generate-preview [:i18n :generate-preview]
   :download [:i18n :download]
   :preview [:i18n :preview]
   :errors [:i18n :errors]
   :download-progress (cond
                        ;; Finalizing status (no current/total, just status message)
                        (and download-progress (:status-key download-progress))
                        (case (:status-key download-progress)
                          :download-progress-pdf [:i18n :download-progress-pdf]
                          :download-progress-zip [:i18n :download-progress-zip]
                          :download-progress-finalizing [:i18n :download-progress-finalizing]
                          "")

                        ;; Normal progress with current/total
                        (and download-progress (pos? (:total download-progress)))
                        [:i18n :download-progress
                         {:1 (:current download-progress)
                          :2 (:total download-progress)}]

                        ;; No progress
                        :else
                        "")
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
            :pdf-layout-options (build-pdf-layout-options)
            :preview-display-size qr-preview/preview-display-size
            :input-hint (build-input-hint input-count)
            :handlers (build-handlers)
            :text (build-text-markers preview-count input-count (:download-progress page-data))})))



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
