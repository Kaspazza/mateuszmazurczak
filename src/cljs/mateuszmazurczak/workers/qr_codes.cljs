(ns mateuszmazurczak.workers.qr-codes
  "Web worker for QR code batch generation."
  (:require
   [clojure.string :as str]
   [mateuszmazurczak.domain.qr-codes.generator :as gen]
   ["jszip" :as JSZip]
   ["pdf-lib" :as pdf-lib]))

(defonce ^:private worker-state (atom nil))

(defn- post!
  ([payload]
   (js/postMessage (clj->js payload)))
  ([payload transfer]
   (js/postMessage (clj->js payload) transfer)))

(defn- total-batches
  [total-items max-batch-size]
  (if (pos? total-items)
    (int (js/Math.ceil (/ total-items max-batch-size)))
    0))

(defn- draw-qr-matrix-to-canvas
  "Render QR code matrix directly to OffscreenCanvas as PNG.
   No SVG conversion needed - fast and reliable.
   
   Algorithm: Direct pixel rendering with margin and optional label."
  [qr-matrix output-size label]
  (let [{:keys [size matrix]} qr-matrix
        margin 4
        total-modules (+ size (* 2 margin))
        ;; Calculate label height if needed
        has-label? (and label (not (str/blank? label)))
        chars-per-line (max 20 (int (* total-modules 0.9)))
        text-lines (when has-label? (gen/wrap-text label chars-per-line))
        line-count (if has-label? (count text-lines) 0)
        font-size (* total-modules 0.055)
        line-height (* font-size 1.3)
        label-padding (* total-modules 0.08)
        label-text-height (* line-count line-height)
        label-height (if has-label? (+ label-padding label-text-height (* total-modules 0.05)) 0)
        total-height-modules (+ total-modules label-height)
        ;; Create canvas with proper dimensions
        module-scale (/ output-size total-modules)
        canvas-width output-size
        canvas-height (int (* output-size (/ total-height-modules total-modules)))
        canvas (js/OffscreenCanvas. canvas-width canvas-height)
        ctx (.getContext canvas "2d")]
    
    ;; Fill white background
    (set! (.-fillStyle ctx) "#FFFFFF")
    (.fillRect ctx 0 0 canvas-width canvas-height)
    
    ;; Draw black QR modules
    (set! (.-fillStyle ctx) "#000000")
    (doseq [y (range size)
            x (range size)
            :when (get-in matrix [y x])]
      (.fillRect ctx
                 (* (+ x margin) module-scale)
                 (* (+ y margin) module-scale)
                 module-scale
                 module-scale))
    
    ;; Draw label if present
    (when has-label?
      (set! (.-fillStyle ctx) "#000000")
      (set! (.-font ctx) (str (* font-size module-scale) "px monospace, Courier New, Courier"))
      (set! (.-textAlign ctx) "center")
      (let [text-start-y (* (+ total-modules label-padding) module-scale)]
        (doseq [[idx line] (map-indexed vector text-lines)]
          (.fillText ctx
                     line
                     (/ canvas-width 2)
                     (+ text-start-y (* idx line-height module-scale))))))
    
    ;; Return promise that resolves to PNG ArrayBuffer
    (-> (.convertToBlob canvas #js {:type "image/png"})
        (.then (fn [blob] (.arrayBuffer blob))))))

(def ^:private a4-width-pt 595)
(def ^:private a4-height-pt 842)
(def ^:private points-per-inch 72)

(defn- cm->pt
  "Convert centimeters to PDF points."
  [cm]
  (* cm (/ points-per-inch 2.54)))

(defn- calculate-qr-size-pt
  "Calculate QR code size in points based on desired physical size and DPI.
   Default: 2cm at 300 DPI for print quality."
  [desired-cm]
  (cm->pt desired-cm))

(defn- calculate-grid-layout
  "Calculate grid layout for multiple QR codes per page.
   Returns {:codes-per-page :qr-size-pt :cols :rows :spacing-pt :margin-pt}"
  []
  (let [qr-size-cm 2.0 ; 2cm per code (good for handheld scanning)
        qr-size-pt (calculate-qr-size-pt qr-size-cm)
        margin-pt 36 ; 0.5 inch margin
        spacing-pt 18 ; 0.25 inch between codes
        available-width (- a4-width-pt (* 2 margin-pt))
        available-height (- a4-height-pt (* 2 margin-pt))
        ;; Calculate how many codes fit horizontally and vertically
        cols (int (/ (+ available-width spacing-pt) (+ qr-size-pt spacing-pt)))
        rows (int (/ (+ available-height spacing-pt) (+ qr-size-pt spacing-pt)))
        codes-per-page (* cols rows)]
    {:codes-per-page codes-per-page
     :qr-size-pt qr-size-pt
     :cols cols
     :rows rows
     :spacing-pt spacing-pt
     :margin-pt margin-pt}))

(defn- uint8array->array-buffer
  [bytes]
  (let [buffer (.-buffer bytes)
        offset (.-byteOffset bytes)
        length (.-byteLength bytes)]
    (if (zero? offset)
      (if (= length (.-byteLength buffer))
        buffer
        (.slice buffer 0 length))
      (.slice buffer offset (+ offset length)))))

(defn- draw-qr-vector-on-pdf-page
  "Draw a single QR code as vector rectangles on a PDF page.
   
   Algorithm: Direct vector rendering - each black module is a filled rectangle.
   This produces resolution-independent, infinitely scalable QR codes."
  [page qr-matrix x y qr-size-pt content show-label?]
  (let [{:keys [size matrix]} qr-matrix
        rgb (.-rgb (.-default pdf-lib))
        margin 4 ; Quiet zone (4 modules)
        total-modules (+ size (* 2 margin))
        module-size-pt (/ qr-size-pt total-modules)]
    
    ;; Draw white background (with quiet zone)
    (.drawRectangle page
                    #js {:x x
                         :y y
                         :width qr-size-pt
                         :height qr-size-pt
                         :color (rgb 1 1 1)})
    
    ;; Draw black modules as vector rectangles
    (doseq [row (range size)
            col (range size)
            :when (get-in matrix [row col])]
      (let [module-x (+ x (* (+ col margin) module-size-pt))
            ;; PDF Y-axis is bottom-up, so invert
            module-y (+ y (- qr-size-pt (* (+ row margin 1) module-size-pt)))]
        (.drawRectangle page
                        #js {:x module-x
                             :y module-y
                             :width module-size-pt
                             :height module-size-pt
                             :color (rgb 0 0 0)})))
    
    ;; Draw label if requested
    (when (and show-label? content)
      (let [font-size 8
            label-y (- y 10)] ; Position label below QR code
        (.drawText page
                   content
                   #js {:x x
                        :y label-y
                        :size font-size
                        :color (rgb 0 0 0)
                        :maxWidth qr-size-pt})))))

(defn- create-pdf-from-codes
  "Generate PDF with vector QR codes (resolution-independent).
   
   Uses grid layout to fit multiple codes per page efficiently.
   Each QR code is drawn as vector rectangles for infinite scalability."
  [codes _size show-label?]
  (let [PDFDocument (.-PDFDocument pdf-lib)
        layout (calculate-grid-layout)
        {:keys [codes-per-page qr-size-pt cols spacing-pt margin-pt]} layout]
    
    (-> (.create PDFDocument)
        (.then
         (fn [pdf-doc]
           ;; Partition codes into pages
           (doseq [page-codes (partition-all codes-per-page codes)]
             (let [page (.addPage pdf-doc #js [a4-width-pt a4-height-pt])]
               ;; Draw each code in grid layout
               (doseq [[idx {:keys [qr-matrix content]}] (map-indexed vector page-codes)]
                 (let [col (mod idx cols)
                       row (int (/ idx cols))
                       x (+ margin-pt (* col (+ qr-size-pt spacing-pt)))
                       ;; PDF Y-axis is bottom-up, calculate from bottom
                       y (- a4-height-pt
                            margin-pt
                            qr-size-pt
                            (* row (+ qr-size-pt spacing-pt)))]
                   (draw-qr-vector-on-pdf-page page qr-matrix x y qr-size-pt content show-label?)))))
           
           (.save pdf-doc)))
        (.then uint8array->array-buffer))))

(defn- error->message
  [error]
  (cond
    (string? error) error
    (and error (.-message error)) (.-message error)
    :else (str error)))

(defn- init-request
  [{:keys [request-id input size format show-label?]}]
  (let [contents (vec (gen/parse-input input))
        format-key (if (keyword? format) format (keyword format))
        validation (gen/validate-request {:contents contents
                                          :size size
                                          :format format-key})]
    (if-not (:valid? validation)
      (post! {:type "qr-codes/error"
              :request-id request-id
              :errors (:errors validation)})
      (let [batch-size gen/max-codes-per-batch
            batches-total (total-batches (count contents) batch-size)
            ;; Initialize accumulator based on format
            accumulator (case format-key
                          :zip (JSZip.)
                          :pdf nil)]  ; PDF accumulator created at end
        (reset! worker-state {:request-id request-id
                              :contents contents
                              :size size
                              :format format-key
                              :show-label? show-label?
                              :cursor 0
                              :batch-size batch-size
                              :total-batches batches-total
                              :accumulator accumulator
                              :all-codes []})  ; Store all codes for PDF
        (post! {:type "qr-codes/ready"
                :request-id request-id
                :total-batches batches-total})))))

(defn- next-batch
  [request-id]
  (let [{:keys [contents size format show-label? cursor batch-size total-batches accumulator all-codes]}
        @worker-state
        total-count (count contents)]
    (cond
      (or (nil? contents) (not= request-id (:request-id @worker-state)))
      (post! {:type "qr-codes/error"
              :request-id request-id
              :errors ["Worker is not initialized for this request"]})

      (>= cursor total-count)
      ;; All batches processed - finalize single archive
      (do
        ;; Notify UI that we're finalizing (this can take a while for PDF)
        (post! {:type "qr-codes/finalizing"
                :request-id request-id
                :format format})
        (let [;; Get fresh state for finalization
              final-state @worker-state
              finalize-promise
              (case format
                :zip (.generateAsync accumulator #js {:type "arraybuffer"})
                :pdf (create-pdf-from-codes (:all-codes final-state) size show-label?)
                (js/Promise.reject (js/Error. "Unsupported format")))]
          (-> finalize-promise
              (.then (fn [buffer]
                       (reset! worker-state nil)
                       (post! {:type "qr-codes/done"
                               :request-id request-id
                               :buffer buffer}
                              #js [buffer])))
              (.catch (fn [error]
                        (reset! worker-state nil)
                        (post! {:type "qr-codes/error"
                                :request-id request-id
                                :errors [(str "Failed to finalize archive: " (error->message error))]}))))))

      :else
      ;; Process next batch and add to accumulator
      (let [end (min (+ cursor batch-size) total-count)
            batch (subvec contents cursor end)
            batch-index (int (/ cursor batch-size))
            result (gen/generate-batch batch
                                       :size size
                                       :show-label? show-label?
                                       :start-index cursor)]
        (if (:success result)
          (let [codes (:codes result)
                ;; Add to accumulator based on format
                add-promise
                (case format
                  :zip
                  ;; Add files to ZIP progressively
                  (reduce (fn [promise {:keys [content qr-matrix filename]}]
                            (.then promise
                                   (fn []
                                     (let [label (when show-label? content)]
                                       (-> (draw-qr-matrix-to-canvas qr-matrix size label)
                                           (.then (fn [png-buffer]
                                                    (.file accumulator filename png-buffer))))))))
                          (js/Promise.resolve)
                          codes)
                  :pdf
                  ;; For PDF, accumulate codes (render all at end)
                  (do (swap! worker-state update :all-codes #(vec (concat % codes)))
                      (js/Promise.resolve)))]
            (-> add-promise
                (.then (fn [_]
                         (swap! worker-state assoc :cursor end)
                         (post! {:type "qr-codes/progress"
                                 :request-id request-id
                                 :batch-index batch-index
                                 :total-batches total-batches
                                 :current end
                                 :total total-count})))
                (.catch (fn [error]
                          (reset! worker-state nil)
                          (post! {:type "qr-codes/error"
                                  :request-id request-id
                                  :errors [(str "Batch processing failed: " (error->message error))]})))))
          (do (reset! worker-state nil)
              (post! {:type "qr-codes/error"
                      :request-id request-id
                      :errors (:errors result)})))))))

(defn- cancel-request
  [request-id]
  (when (= request-id (:request-id @worker-state))
    (reset! worker-state nil)))

(defn- handle-message
  [^js event]
  (let [{:keys [type request-id]
         :as payload}
        (js->clj (.-data event) :keywordize-keys true)
        message-type (if (keyword? type) (name type) type)]
    (case message-type
      "qr-codes/init" (init-request payload)
      "qr-codes/next" (next-batch request-id)
      "qr-codes/cancel" (cancel-request request-id)
      nil)))

(defn init
  "Initialize QR codes worker listener."
  []
  (js/self.addEventListener "message" handle-message))
