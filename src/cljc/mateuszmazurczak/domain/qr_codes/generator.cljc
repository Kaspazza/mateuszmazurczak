(ns mateuszmazurczak.domain.qr-codes.generator
  "Pure domain logic for QR code generation.
   Cross-platform (CLJ/CLJS) using Nayuki's QR-Code-generator library.
   
   Design Pattern: Strategy Pattern - same interface, platform-specific impl.
   Algorithm: QR Code encoding (ISO/IEC 18004)"
  (:require
   [clojure.string :as str]
   #?@(:cljs [[goog.string :as gstring]
              [goog.string.format]
              ["nayuki-qr-code-generator" :as qrcodegen]]))
  #?(:clj (:import [io.nayuki.qrcodegen QrCode QrCode$Ecc])))

;; Constants
(def default-qr-size 300)
(def min-qr-size 50)
(def max-qr-size 1000)
(def max-codes-per-batch 1000)

;; Pure Domain Functions

(defn parse-input
  "Parse input text into individual QR code values.
   Splits by newlines, trims whitespace, removes empty lines."
  [input]
  (when (string? input)
    (->> (str/split-lines input)
         (map str/trim)
         (remove str/blank?)
         vec)))

(defn valid-size?
  "Check if QR code size is within acceptable bounds."
  [size]
  (and (number? size) (>= size min-qr-size) (<= size max-qr-size)))

(defn sanitize-filename
  "Create safe filename from content string."
  [content idx]
  (let [safe-content (-> content
                         (str/replace #"[^a-zA-Z0-9\-_]" "_")
                         (str/replace #"_{2,}" "_"))
        truncated (subs safe-content 0 (min (count safe-content) 40))
        padded-idx (#?(:clj format
                       :cljs gstring/format)
                    "%03d"
                    (inc idx))]
    (str "qr_" padded-idx "_" truncated ".png")))

(defn validate-request
  "Validate QR code generation request."
  [{:keys [contents size format]}]
  (let [errors (cond-> []
                 (or (nil? contents) (empty? contents)) (conj "No QR code values provided")
                 (and (some? size) (not (valid-size? size)))
                 (conj (str "Size must be between " min-qr-size " and " max-qr-size))
                 (and (some? format) (not (#{:zip :pdf} format))) (conj
                                                                   "Format must be :zip or :pdf")
                 (and (some? contents) (> (count contents) max-codes-per-batch))
                 (conj (str "Maximum " max-codes-per-batch " QR codes per request")))]
    {:valid? (empty? errors)
     :errors errors}))

;; Platform-Specific QR Generation

#?(:clj (defn- get-ecc-level
          [level]
          (case level
            :low QrCode$Ecc/LOW
            :medium QrCode$Ecc/MEDIUM
            :quartile QrCode$Ecc/QUARTILE
            :high QrCode$Ecc/HIGH
            QrCode$Ecc/MEDIUM)))

#?(:clj
     (defn generate-qr-matrix
       "Generate QR code matrix from content string.
      Returns {:size int :matrix [[bool]]} where true = black module."
       [content
        &
        {:keys [error-correction]
         :or {error-correction :medium}}]
       (let [ecc (get-ecc-level error-correction)
             qr (QrCode/encodeText content ecc)
             size (.size qr)]
         {:size size
          :matrix (vec (for [y (range size)] (vec (for [x (range size)] (.getModule qr x y)))))})))

#?(:cljs
     (defn generate-qr-matrix
       "Generate QR code matrix from content string.
      Returns {:size int :matrix [[bool]]} where true = black module."
       [content
        &
        {:keys [error-correction]
         :or {error-correction :medium}}]
       ;; Access pattern: qrcodegen.default.QrCode and qrcodegen.default.QrCode.Ecc
       (let [QrCode (-> qrcodegen .-default .-QrCode)
             Ecc (.-Ecc QrCode)
             ecc (case error-correction
                   :low (.-LOW Ecc)
                   :medium (.-MEDIUM Ecc)
                   :quartile (.-QUARTILE Ecc)
                   :high (.-HIGH Ecc)
                   (.-MEDIUM Ecc))
             qr (.encodeText QrCode content ecc)
             size (.-size qr)]
         {:size size
          :matrix (vec (for [y (range size)] (vec (for [x (range size)] (.getModule qr x y)))))})))

;; SVG Rendering (platform-independent)

(defn- split-long-word
  "Split a word that exceeds max length into chunks.
   Used for URLs and other long strings without spaces."
  [word max-len]
  (if (<= (count word) max-len)
    [word]
    (loop [remaining word
           result []]
      (if (empty? remaining)
        result
        (let [chunk (subs remaining 0 (min max-len (count remaining)))
              rest-str (subs remaining (min max-len (count remaining)))]
          (recur rest-str (conj result chunk)))))))

(defn wrap-text
  "Wrap text into multiple lines based on character limit.
   Handles both spaced text and long strings without spaces (like URLs).
   Returns vector of text lines."
  [text max-chars-per-line]
  (if (<= (count text) max-chars-per-line)
    [text]
    (let [;; Split by whitespace, keeping track of segments
          segments (str/split text #"\s+")
          ;; Process each segment, splitting long ones
          process-segment (fn [segment]
                            (if (<= (count segment) max-chars-per-line)
                              [segment]
                              (split-long-word segment max-chars-per-line)))
          ;; Flatten all segments into words/chunks
          all-chunks (mapcat process-segment segments)
          ;; Build lines by combining chunks
          lines (reduce (fn [acc chunk]
                          (let [current-line (or (last acc) "")
                                separator (if (empty? current-line) "" " ")
                                new-line (str current-line separator chunk)]
                            (if (<= (count new-line) max-chars-per-line)
                              (if (empty? acc)
                                [new-line]
                                (conj (vec (butlast acc)) new-line))
                              (conj acc chunk))))
                        []
                        all-chunks)]
      (vec (remove str/blank? lines)))))

(defn matrix->svg-path
  "Convert QR matrix to SVG path data string. Pure function.
   Returns the 'd' attribute value for an SVG path element."
  [{:keys [size matrix]} margin]
  (str/join "" (for [y (range size)
                     x (range size)
                     :when (get-in matrix [y x])]
                 (str "M" (+ margin x) "," (+ margin y) "h1v1h-1z"))))

(defn matrix->svg-data
  "Convert QR matrix to SVG data structure for rendering.
   Returns a map that can be used to render SVG in any format."
  [{:keys [size matrix] :as qr-data}
   &
   {:keys [output-size margin foreground background label]
    :or {output-size 300
         margin 4
         foreground "#000000"
         background "#FFFFFF"
         label nil}}]
  (let [total-modules (+ size (* 2 margin))]
    {:viewbox (str "0 0 " total-modules " " total-modules)
     :width output-size
     :height output-size
     :background background
     :foreground foreground
     :path (matrix->svg-path qr-data margin)
     :label label}))

(defn matrix->svg
  "Convert QR matrix to SVG string. Pure function.
   Used for file export (ZIP/PDF). For UI rendering, use matrix->svg-data."
  [{:keys [size matrix] :as qr-data}
   &
   {:keys [output-size margin foreground background label]
    :or {output-size 300
         margin 4
         foreground "#000000"
         background "#FFFFFF"
         label nil}}]
  (let [total-modules (+ size (* 2 margin))
        path-d (matrix->svg-path qr-data margin)
        ;; Add space for label if present
        has-label? (and label (not (str/blank? label)))
        ;; Calculate characters per line based on QR size
        ;; Smaller font = more chars per line, using ~0.6 of module width per char
        chars-per-line (max 20 (int (* total-modules 0.9)))
        text-lines (when has-label? (wrap-text label chars-per-line))
        line-count (if has-label? (count text-lines) 0)
        ;; Font size relative to QR modules - smaller for more text
        font-size (* total-modules 0.055)
        line-height (* font-size 1.3)
        ;; Calculate label height: padding + lines
        label-padding (* total-modules 0.08)
        label-text-height (* line-count line-height)
        label-height (if has-label?
                       (+ label-padding label-text-height (* total-modules 0.05))
                       0)
        total-height-modules (+ total-modules label-height)
        ;; Starting Y position for first line of text (after QR + padding)
        text-start-y (+ total-modules label-padding)]
    (str "<svg xmlns=\"http://www.w3.org/2000/svg\" "
         "viewBox=\"0 0 " total-modules " " total-height-modules "\" "
         "width=\"" output-size "\" height=\"" (int (* output-size (/ total-height-modules total-modules))) "\">"
         "<rect width=\"100%\" height=\"100%\" fill=\"" background "\"/>"
         "<path d=\"" path-d "\" fill=\"" foreground "\"/>"
         (when has-label?
           (str "<text x=\"" (/ total-modules 2) "\" "
                "text-anchor=\"middle\" "
                "font-family=\"monospace, Courier New, Courier\" "
                "font-size=\"" font-size "\" "
                "fill=\"" foreground "\">"
                (str/join ""
                          (map-indexed
                           (fn [idx line]
                             (str "<tspan x=\"" (/ total-modules 2) "\" "
                                  "y=\"" (+ text-start-y (* idx line-height)) "\">"
                                  line
                                  "</tspan>"))
                           text-lines))
                "</text>"))
         "</svg>")))

(defn generate-qr-svg
  "Generate QR code as SVG string."
  [content
   &
   {:keys [output-size error-correction margin foreground background label]
    :or {output-size 300
         error-correction :medium
         margin 4
         foreground "#000000"
         background "#FFFFFF"
         label nil}}]
  (let [qr-data (generate-qr-matrix content :error-correction error-correction)]
    (matrix->svg qr-data
                 :output-size output-size
                 :margin margin
                 :foreground foreground
                 :background background
                 :label label)))

;; Batch Processing

(defn generate-batch
  "Generate multiple QR codes with both SVG string and render data.
   Returns {:success bool :codes [{:content :svg :svg-data :filename}] :errors []}
   - :svg - SVG string for file export (ZIP/PDF)
   - :svg-data - data map for direct UI rendering (no innerHTML needed)"
  [contents
   &
   {:keys [size error-correction show-label?]
    :or {size default-qr-size
         error-correction :medium
         show-label? false}}]
  (let [validation (validate-request {:contents contents
                                      :size size
                                      :format :zip})]
    (if-not (:valid? validation)
      {:success false
       :errors (:errors validation)}
      {:success true
       :codes (vec (map-indexed (fn [idx content]
                                  (let [qr-matrix (generate-qr-matrix content
                                                                      :error-correction
                                                                      error-correction)
                                        label (when show-label? content)]
                                    {:content content
                                     :svg (matrix->svg qr-matrix :output-size size :label label)
                                     :svg-data (matrix->svg-data qr-matrix :output-size size :label label)
                                     :filename (sanitize-filename content idx)}))
                                contents))})))
