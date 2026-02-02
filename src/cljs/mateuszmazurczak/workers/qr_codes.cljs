(ns mateuszmazurczak.workers.qr-codes
  "Web worker for QR code batch generation."
  (:require
   [mateuszmazurczak.domain.qr-codes.generator :as gen]))

(defn- handle-message
  [^js event]
  (let [{:keys [request-id input size show-label?]}
        (js->clj (.-data event) :keywordize-keys true)
        contents (gen/parse-input input)
        result (gen/generate-batches contents :size size :show-label? show-label?)]
    (if (:success result)
      (js/postMessage (clj->js {:type :worker-success
                                :request-id request-id
                                :batches (:batches result)}))
      (js/postMessage (clj->js {:type :worker-failure
                                :request-id request-id
                                :errors (:errors result)})))))

(defn init
  "Initialize QR codes worker listener."
  []
  (js/self.addEventListener "message" handle-message))
