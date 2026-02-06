(ns mateuszmazurczak.ports.background-processing
  "Port for offloading CPU-intensive work to background threads.
   
   Hybrid port pattern - directly uses the worker adapter
   since we don't expect multiple implementations."
  (:require
   [mateuszmazurczak.adapters.workers.qr-codes :as qr-worker]))

(defn init-qr-worker!
  "Initialize QR code generation in background worker.
   
   Arguments:
   - request-id: Unique identifier for this request
   - config: {:input :size :format :show-label? :png-config :pdf-layout-config}
   - callbacks: {:on-ready :on-progress :on-finalizing :on-done :on-failure}
                Each callback receives parsed message data
   
   Returns: nil (side effect only)"
  [request-id config callbacks]
  (qr-worker/init-request! request-id config callbacks))

(defn request-next-batch!
  "Request next batch of QR codes from worker.
   
   Arguments:
   - request-id: The request identifier
   
   Returns: nil (side effect only)"
  [request-id]
  (qr-worker/request-next-batch! request-id))

(defn cancel-request!
  "Cancel ongoing worker request.
   
   Arguments:
   - request-id: The request identifier to cancel
   
   Returns: nil (side effect only)"
  [request-id]
  (qr-worker/cancel-request! request-id))
