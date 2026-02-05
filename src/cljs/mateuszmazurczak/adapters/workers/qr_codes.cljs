(ns mateuszmazurczak.adapters.workers.qr-codes
  "Web Worker adapter for QR code generation in background thread.
   
   Manages worker lifecycle, message routing, and request tracking.
   Framework-agnostic - can be used outside re-frame."
  (:require
   [mateuszmazurczak.application.qr-codes.processing :as qr-processing]))

;; =============================================================================
;; Worker State Management
;; =============================================================================

(defonce ^:private worker-state
  (atom {:worker nil
         :requests {}}))

;; =============================================================================
;; Worker Message Protocol
;; =============================================================================

(defn- parse-worker-message
  "Parse JavaScript worker message event into Clojure data.
   
   Arguments:
   - event: JavaScript MessageEvent from worker
   
   Returns: {:type :request-id :payload ...}"
  [^js event]
  (let [data (.-data event)
        type (aget data "type")
        request-id (aget data "request-id")
        message-type (if (keyword? type) (name type) type)]
    (case message-type
      "qr-codes/ready" {:type :ready
                        :request-id request-id
                        :total-batches (aget data "total-batches")}
      "qr-codes/progress" {:type :progress
                           :request-id request-id
                           :batch-index (aget data "batch-index")
                           :total-batches (aget data "total-batches")
                           :current (aget data "current")
                           :total (aget data "total")}
      "qr-codes/finalizing" {:type :finalizing
                             :request-id request-id
                             :format (keyword (aget data "format"))}
      "qr-codes/done" {:type :done
                       :request-id request-id
                       :buffer (aget data "buffer")}
      "qr-codes/error" {:type :error
                        :request-id request-id
                        :errors (js->clj (aget data "errors"))}
      {:type :unknown
       :request-id request-id})))

(defn- handle-worker-message
  "Handle incoming worker message and route to callbacks.
   
   Arguments:
   - event: JavaScript MessageEvent from worker
   
   Side effects: Calls registered callbacks, updates worker-state"
  [^js event]
  (let [parsed (parse-worker-message event)
        {:keys [request-id]} parsed
        callbacks (get-in @worker-state [:requests request-id])]
    (when callbacks
      (when-let [dispatch (qr-processing/route-worker-message parsed callbacks)]
        ;; Return the dispatch vector to be handled by caller
        ;; This is the framework coupling point - caller can dispatch to re-frame
        (when-let [on-dispatch (:on-dispatch callbacks)] (on-dispatch dispatch)))
      ;; Update worker state based on message type
      (case (:type parsed)
        :ready
        (swap! worker-state assoc-in [:requests request-id :total-batches] (:total-batches parsed))
        (:done :error) (swap! worker-state update :requests dissoc request-id)
        nil))))

(defn- ensure-worker!
  "Ensure worker exists and is initialized.
   
   Returns: Worker instance"
  []
  (if-let [worker (:worker @worker-state)]
    worker
    (let [worker (js/Worker. "/js/compiled/qr-codes-worker.js")]
      (.addEventListener worker "message" handle-worker-message)
      (swap! worker-state assoc :worker worker)
      worker)))

(defn- send-message!
  "Send message to worker.
   
   Arguments:
   - message: Clojure map to send (will be converted to JS)
   
   Returns: nil"
  [message]
  (let [worker (ensure-worker!)] (.postMessage worker (clj->js message))))

;; =============================================================================
;; Public API
;; =============================================================================

(defn init-request!
  "Initialize worker request with configuration and callbacks.
   
   Arguments:
   - request-id: Unique request identifier
   - config: {:input :size :format :show-label? :pdf-layout-config}
   - callbacks: {:on-ready :on-progress :on-finalizing :on-done :on-failure :on-dispatch}
                on-dispatch is called with dispatch vectors for framework integration
   
   Returns: nil (side effect only)"
  [request-id {:keys [input size format show-label? pdf-layout-config]} callbacks]
  (swap! worker-state assoc-in [:requests request-id] callbacks)
  (send-message! {:type "qr-codes/init"
                  :request-id request-id
                  :input input
                  :size size
                  :format format
                  :show-label? show-label?
                  :pdf-layout-config pdf-layout-config}))

(defn request-next-batch!
  "Request next batch from worker.
   
   Arguments:
   - request-id: Request identifier
   
   Returns: nil (side effect only)"
  [request-id]
  (send-message! {:type "qr-codes/next"
                  :request-id request-id}))

(defn cancel-request!
  "Cancel worker request and clean up.
   
   Arguments:
   - request-id: Request identifier to cancel
   
   Returns: nil (side effect only)"
  [request-id]
  (swap! worker-state update :requests dissoc request-id)
  (send-message! {:type "qr-codes/cancel"
                  :request-id request-id}))
