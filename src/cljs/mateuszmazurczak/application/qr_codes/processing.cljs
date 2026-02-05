(ns mateuszmazurczak.application.qr-codes.processing
  "QR codes processing workflows - pure orchestration.
   
   Contains download flow coordination without framework specifics.
   All functions are pure - no atoms, no side effects, no JS interop."
  (:require
   [mateuszmazurczak.application.qr-codes.input     :as qr-input]
   [mateuszmazurczak.application.qr-codes.page-data :as page-data]))

;; =============================================================================
;; Worker Message Routing (Pure)
;; =============================================================================

(defn route-worker-message
  "Route parsed worker message to appropriate callback.
   
   Pure function - takes parsed message and callbacks, returns dispatch vector.
   
   Arguments:
   - parsed-message: {:type :request-id ...} from worker adapter
   - callbacks: {:on-ready :on-progress :on-finalizing :on-done :on-failure}
   
   Returns: Dispatch vector to be handled by framework, or nil"
  [parsed-message callbacks]
  (let [{:keys [type request-id]} parsed-message
        {:keys [on-ready on-progress on-finalizing on-done on-failure]} callbacks]
    (case type
      :ready
      (when on-ready
        (conj on-ready
              {:request-id request-id
               :total-batches (:total-batches parsed-message)}))
      
      :progress
      (when on-progress
        (conj on-progress
              {:request-id request-id
               :batch-index (:batch-index parsed-message)
               :total-batches (:total-batches parsed-message)
               :current (:current parsed-message)
               :total (:total parsed-message)}))
      
      :finalizing
      (when on-finalizing
        (conj on-finalizing
              {:request-id request-id
               :format (:format parsed-message)}))
      
      :done
      (when on-done
        (conj on-done
              {:request-id request-id
               :buffer (:buffer parsed-message)}))
      
      :error
      (when on-failure
        (conj on-failure (:errors parsed-message)))
      
      nil)))

;; =============================================================================
;; Download Workflow (Pure State Transformations)
;; =============================================================================

(defn start-download
  "Prepare state updates and worker init data for download start.
   
   Pure function - takes page data, returns updates.
   
   Arguments:
   - page-data: Current QR codes page state
   
   Returns: {:request-id ... :page-data ... :worker-config ...}"
  [page-data]
  (let [request-id (str (random-uuid))]
    {:request-id request-id
     :page-data (assoc page-data
                       :loading? true
                       :errors []
                       :download-progress nil
                       :show-validation-errors? true)
     :worker-config {:input (:input page-data)
                     :size (:size page-data)
                     :format (:format page-data)
                     :show-label? (:show-label? page-data)
                     :pdf-layout-config (page-data/resolve-pdf-layout-config page-data)}}))

(defn handle-worker-ready
  "Handle worker ready message - calculate derived state.
   
   Pure function - takes page data, returns updates.
   
   Arguments:
   - page-data: Current page state
   - request-id: Worker request identifier
   
   Returns: {:page-data ... :request-id ...}"
  [page-data request-id]
  (let [contents (qr-input/parse-input (:input page-data))
        total-items (count contents)]
    {:page-data (assoc page-data
                       :download-progress
                       {:current 0
                        :total total-items})
     :request-id request-id}))

(defn handle-worker-progress
  "Handle worker progress message - update progress state.
   
   Pure function - takes page data and progress info, returns updates.
   
   Arguments:
   - page-data: Current page state
   - request-id: Worker request identifier
   - current: Current item count
   - total: Total item count
   
   Returns: {:page-data ... :request-id ...}"
  [page-data request-id current total]
  {:page-data (assoc page-data
                     :download-progress
                     {:current current
                      :total total})
   :request-id request-id})

(defn handle-worker-finalizing
  "Handle worker finalizing message - show finalization status.
   
   Pure function - takes page data and format, returns updates.
   
   Arguments:
   - page-data: Current page state
   - format: Output format (:pdf or :zip)
   
   Returns: {:page-data ...}"
  [page-data format]
  (let [status-key (case format
                     :pdf :creating-pdf-document
                     :zip :packaging-files
                     :preparing-download)]
    {:page-data (assoc page-data
                       :download-progress
                       {:current nil
                        :total nil
                        :status-key status-key})}))

(defn handle-worker-done
  "Handle worker done message - prepare download metadata.
   
   Pure function - takes page data and buffer, returns updates.
   
   Arguments:
   - page-data: Current page state
   - buffer: ArrayBuffer from worker
   
   Returns: {:page-data ... :save-batch {:buffer ... :opts ...}}"
  [page-data buffer]
  (let [extension (case (:format page-data)
                    :pdf "zip"
                    :zip "zip")
        filename (str "qr-codes." extension)]
    {:page-data (assoc page-data
                       :loading? false
                       :download-progress nil)
     :save-batch {:buffer buffer
                  :opts {:filename filename}}}))

(defn handle-worker-failure
  "Handle worker failure message - prepare error state.
   
   Pure function - takes page data and errors, returns updates.
   
   Arguments:
   - page-data: Current page state
   - errors: Vector of error messages
   
   Returns: {:page-data ... :log-error ex-info}"
  [page-data errors]
  {:page-data (assoc page-data
                     :errors errors
                     :loading? false
                     :download-progress nil)
   :log-error (ex-info "QR code worker failed"
                       {:type ::worker-failed
                        :errors errors})})

(defn handle-download-failure
  "Handle failed export save - prepare error state.
   
   Pure function - takes page data and error, returns updates.
   
   Arguments:
   - page-data: Current page state
   - error: Error object from export failure
   
   Returns: {:page-data ... :log-error ex-info}"
  [page-data error]
  {:page-data (assoc page-data
                     :loading? false
                     :download-progress nil)
   :log-error (ex-info "Failed to download QR codes"
                       {:type ::download-failed
                        :error error})})
