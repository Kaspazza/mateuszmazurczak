(ns mateuszmazurczak.adapters.export.file-export
  "File export adapter for downloading generated files.
   Uses JSZip for ZIP creation, pdf-lib for PDF, and FileSaver for downloads.
   
   Design Pattern: Adapter Pattern
   - Adapts browser file APIs to our domain model
   - Handles async blob generation with promises"
  (:require
   ["file-saver"   :as FileSaver]
   ["jszip"        :as JSZip]
   ["pdf-lib"      :as pdf-lib]
   [clojure.string :as str]))

;; =============================================================================
;; SVG to PNG Conversion (via Canvas)
;; =============================================================================

(defn- extract-svg-dimensions
  "Extract width and height from SVG string.
   Returns [width height] or [size size] as fallback."
  [svg-string size]
  (let [width-match (re-find #"width=\"(\d+(?:\.\d+)?)\"" svg-string)
        height-match (re-find #"height=\"(\d+(?:\.\d+)?)\"" svg-string)]
    (if (and width-match height-match)
      [(js/parseFloat (second width-match)) (js/parseFloat (second height-match))]
      [size size])))

(defn- svg-to-image-blob
  "Convert SVG string to PNG or JPG Blob via Canvas.
   Automatically detects SVG dimensions to handle labels correctly.
   Returns a Promise that resolves to a Blob."
  [svg-string base-size image-format]
  (js/Promise.
   (fn [resolve reject]
     (let [[width height] (extract-svg-dimensions svg-string base-size)
           img (js/Image.)
           canvas (js/document.createElement "canvas")
           ctx (.getContext canvas "2d")
           blob (js/Blob. #js [svg-string] #js {:type "image/svg+xml"})
           url (js/URL.createObjectURL blob)
           mime-type (case image-format
                       :jpg "image/jpeg"
                       "image/png")]
       (set! (.-width canvas) width)
       (set! (.-height canvas) height)
       (set! (.-onload img)
             (fn []
               (js/URL.revokeObjectURL url)
               (.drawImage ctx img 0 0 width height)
               (.toBlob canvas
                        (fn [image-blob]
                          (if image-blob
                            (resolve image-blob)
                            (reject (js/Error. "Failed to create image blob"))))
                        mime-type)))
       (set! (.-onerror img) (fn [e] (js/URL.revokeObjectURL url) (reject e)))
       (set! (.-src img) url)))))

(defn- svg-to-png-bytes
  "Convert SVG string to PNG ArrayBuffer via Canvas.
   Returns a Promise that resolves to ArrayBuffer."
  [svg-string size]
  (-> (svg-to-image-blob svg-string size :png)
      (.then (fn [blob] (.arrayBuffer blob)))))

;; =============================================================================
;; ZIP Export
;; =============================================================================

(defn create-zip-from-codes
  "Create a ZIP file from generated QR codes as PNG or JPG files."
  ([codes size] (create-zip-from-codes codes size :png))
  ([codes size image-format]
   (let [zip (JSZip.)
         extension (case image-format
                     :jpg ".jpg"
                     ".png")]
     (-> (js/Promise.all (clj->js (map (fn [{:keys [svg filename]}]
                                         (-> (svg-to-image-blob svg size image-format)
                                             (.then (fn [image-blob]
                                                      (let [image-filename (str/replace filename
                                                                                        #"\.png$"
                                                                                        extension)]
                                                        (.file zip image-filename image-blob))))))
                                       codes)))
         (.then (fn [_] (.generateAsync zip #js {:type "blob"})))))))

(defn create-zip-from-svgs
  "Create a ZIP file with SVG files (no PNG conversion)."
  [codes]
  (let [zip (JSZip.)]
    (doseq [{:keys [svg filename]} codes]
      (let [svg-filename (str/replace filename #"\.png$" ".svg")] (.file zip svg-filename svg)))
    (.generateAsync zip #js {:type "blob"})))

;; =============================================================================
;; Download Triggers
;; =============================================================================

(defn save-blob!
  "Trigger browser download for a Blob."
  [blob filename]
  (FileSaver/saveAs blob filename))

(defn save-array-buffer!
  "Trigger browser download for an ArrayBuffer."
  [array-buffer
   {:keys [filename format]
    :or {format :zip}}]
  (let [mime-type (case format
                    :pdf "application/pdf"
                    :jpg "application/zip"
                    :zip "application/zip"
                    "application/octet-stream")
        blob (js/Blob. #js [array-buffer] #js {:type mime-type})]
    (save-blob! blob filename)))

(defn download-zip!
  "Generate ZIP from QR codes and trigger download.
   Supports PNG files (:zip) or JPG files (:jpg) inside the ZIP."
  [codes
   {:keys [size filename as-svg? format]
    :or {size 300
         filename "qr-codes.zip"
         as-svg? false
         format :zip}}]
  (let [image-format (if (= format :jpg) :jpg :png)]
    (-> (if as-svg? (create-zip-from-svgs codes) (create-zip-from-codes codes size image-format))
        (.then (fn [blob] (save-blob! blob filename)))
        (.catch (fn [err] (js/console.error "Failed to create ZIP:" err) (throw err))))))

;; =============================================================================
;; PDF Export (using pdf-lib)
;; =============================================================================

;; A4 dimensions in points (72 points per inch)
(def ^:private a4-width-pt 595)
(def ^:private a4-height-pt 842)

(defn- calculate-pdf-layout-pt
  "Calculate QR code layout for PDF page in points."
  [qr-size-px]
  (let [;; Convert px to points (assuming 96 DPI, 72 points per inch)
        px-to-pt (/ 72 96)
        qr-size-pt (* qr-size-px px-to-pt)
        ;; Cap at max size that fits on page with margins
        max-size (- (min a4-width-pt a4-height-pt) 100)
        final-size (min qr-size-pt max-size)
        ;; Center on page
        x (/ (- a4-width-pt final-size) 2)
        y (/ (- a4-height-pt final-size) 2)]
    {:qr-size final-size
     :x x
     :y y}))

(defn create-pdf-from-codes
  "Create a PDF file from generated QR codes using pdf-lib.
   Each QR code gets its own page.
   Note: Labels are already embedded in the SVG if show-label? was true,
   so we don't add any extra text here."
  [codes size]
  (let [PDFDocument (.-PDFDocument pdf-lib)]
    (-> (.create PDFDocument)
        (.then
         (fn [pdf-doc]
           ;; Convert all SVGs to PNG bytes first
           (-> (js/Promise.all (clj->js (map (fn [{:keys [svg]}] (svg-to-png-bytes svg size))
                                             codes)))
               (.then (fn [png-buffers]
                        ;; Embed all PNGs
                        (js/Promise.all (clj->js (map (fn [buf]
                                                        (.embedPng pdf-doc (js/Uint8Array. buf)))
                                                      png-buffers)))))
               (.then (fn [png-images]
                        (let [layout (calculate-pdf-layout-pt size)
                              images (js->clj png-images)]
                          ;; Add page for each QR code
                          (doseq [[png-image _code] (map vector images codes)]
                            (let [page (.addPage pdf-doc #js [a4-width-pt a4-height-pt])]
                              ;; Draw QR code image centered (label is already in the image if enabled)
                              (.drawImage page
                                          png-image
                                          #js {:x (:x layout)
                                               :y (:y layout)
                                               :width (:qr-size layout)
                                               :height (:qr-size layout)})))
                          ;; Save PDF
                          (.save pdf-doc))))))))))

(defn- array-buffer-to-blob
  "Convert ArrayBuffer to Blob with PDF mime type."
  [array-buffer]
  (js/Blob. #js [array-buffer] #js {:type "application/pdf"}))

(defn download-pdf!
  "Generate PDF from QR codes and trigger download."
  [codes
   {:keys [size filename]
    :or {size 300
         filename "qr-codes.pdf"}}]
  (-> (create-pdf-from-codes codes size)
      (.then (fn [pdf-bytes] (save-blob! (array-buffer-to-blob pdf-bytes) filename)))
      (.catch (fn [err] (js/console.error "Failed to create PDF:" err) (throw err)))))

;; =============================================================================
;; Unified Download Function
;; =============================================================================

(defn download!
  "Download QR codes in specified format."
  [codes
   {:keys [format]
    :or {format :zip}
    :as opts}]
  (case format
    :zip (download-zip! codes opts)
    :jpg (download-zip! codes opts)
    :pdf (download-pdf! codes opts)))

;; =============================================================================
;; Adapter Instance
;; =============================================================================

(def adapter
  "File export adapter implementation"
  {:download! download!
   :download-zip! download-zip!
   :download-pdf! download-pdf!
   :create-zip-from-codes create-zip-from-codes
   :create-pdf-from-codes create-pdf-from-codes
   :save-blob! save-blob!
   :save-array-buffer! save-array-buffer!})
