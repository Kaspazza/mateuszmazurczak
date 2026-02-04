(ns mateuszmazurczak.application.qr-codes.batch
  "Batch generation for QR codes in the application layer."
  (:require
   [mateuszmazurczak.application.qr-codes.export :as qr-export]
   [mateuszmazurczak.application.qr-codes.svg :as qr-svg]
   [mateuszmazurczak.domain.qr-codes.generator :as gen]))

(defn generate-batch
  "Generate multiple QR codes with both SVG string and render data.
   Returns {:success bool :codes [{:content :svg :svg-data :filename}] :errors []}
   - :svg - SVG string for file export (ZIP/PDF)
   - :svg-data - data map for direct UI rendering"
  [contents
   &
   {:keys [size error-correction show-label? start-index]
    :or {size gen/default-qr-pixel-size
         error-correction :medium
         show-label? false
         start-index 0}}]
  (let [validation (gen/validate-request {:contents contents
                                          :size size})]
    (if-not (:valid? validation)
      {:success false
       :errors (:errors validation)}
      {:success true
       :codes (vec (map-indexed
                    (fn [idx content]
                      (let [qr-matrix (gen/generate-qr-matrix content :error-correction error-correction)
                            label (when show-label? content)
                            filename-idx (+ start-index idx)]
                        {:content content
                         :qr-matrix qr-matrix
                         :svg (qr-svg/matrix->svg qr-matrix :output-size size :label label)
                         :svg-data (qr-svg/matrix->svg-data qr-matrix :output-size size :label label)
                         :filename (qr-export/sanitize-filename content filename-idx)}))
                    contents))})))

(defn generate-batches
  "Generate QR codes in chunks based on max-codes-per-batch.
   Returns {:success bool :batches [{:index int :codes []}] :errors []}."
  [contents
   &
   {:keys [size error-correction show-label? max-batch-size]
    :or {size gen/default-qr-pixel-size
         error-correction :medium
         show-label? false
         max-batch-size gen/max-codes-per-batch}}]
  (let [validation (gen/validate-request {:contents contents
                                          :size size})]
    (if-not (:valid? validation)
      {:success false
       :errors (:errors validation)}
      (loop [remaining (partition-all max-batch-size contents)
             index 0
             start-index 0
             batches []]
        (if (empty? remaining)
          {:success true
           :batches batches}
          (let [batch (first remaining)
                result (generate-batch (vec batch)
                                       :size size
                                       :error-correction error-correction
                                       :show-label? show-label?
                                       :start-index start-index)]
            (if-not (:success result)
              {:success false
               :errors (:errors result)}
              (recur (rest remaining)
                     (inc index)
                     (+ start-index (count batch))
                     (conj batches
                           {:index index
                            :codes (:codes result)})))))))))
