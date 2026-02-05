(ns mateuszmazurczak.application.qr-codes.svg
  "SVG serialization for QR code matrices."
  (:require
   [clojure.string                             :as str]
   [mateuszmazurczak.domain.qr-codes.generator :as gen]
   [mateuszmazurczak.utils.text                :as text]
   [mateuszmazurczak.utils.xml                 :as xml]))

(defn matrix->svg-path
  "Convert QR matrix to SVG path data string. Pure function.
   Returns the 'd' attribute value for an SVG path element."
  [{:keys [size matrix]} margin]
  (str/join ""
            (for [y (range size)
                  x (range size)
                  :when (get-in matrix [y x])]
              (str "M" (+ margin x) "," (+ margin y) "h1v1h-1z"))))

(defn matrix->svg-data
  "Convert QR matrix to SVG data structure for rendering.
   Returns a map that can be used to render SVG in any format."
  [{:keys [size]
    :as qr-data}
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
  [{:keys [size]
    :as qr-data}
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
        text-lines (when has-label? (text/wrap-text label chars-per-line))
        line-count (if has-label? (count text-lines) 0)
        ;; Font size relative to QR modules - smaller for more text
        font-size (* total-modules 0.055)
        line-height (* font-size 1.3)
        ;; Calculate label height: padding + lines
        label-padding (* total-modules 0.08)
        label-text-height (* line-count line-height)
        label-height (if has-label? (+ label-padding label-text-height (* total-modules 0.05)) 0)
        total-height-modules (+ total-modules label-height)
        ;; Starting Y position for first line of text (after QR + padding)
        text-start-y (+ total-modules label-padding)]
    (str "<svg xmlns=\"http://www.w3.org/2000/svg\" "
         "viewBox=\"0 0 "
         total-modules
         " "
         total-height-modules
         "\" "
         "width=\""
         output-size
         "\" height=\""
         (int (* output-size (/ total-height-modules total-modules)))
         "\">"
         "<rect width=\"100%\" height=\"100%\" fill=\""
         background
         "\"/>"
         "<path d=\""
         path-d
         "\" fill=\""
         foreground
         "\"/>"
         (when has-label?
           (str "<text x=\""
                (/ total-modules 2)
                "\" "
                "text-anchor=\"middle\" "
                "font-family=\"monospace, Courier New, Courier\" "
                "font-size=\""
                font-size
                "\" "
                "fill=\""
                foreground
                "\">"
                (str/join ""
                          (map-indexed (fn [idx line]
                                         (let [escaped-line (xml/escape-xml line)]
                                           (str "<tspan x=\""
                                                (/ total-modules 2)
                                                "\" "
                                                "y=\""
                                                (+ text-start-y (* idx line-height))
                                                "\">"
                                                escaped-line
                                                "</tspan>")))
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
  (let [qr-data (gen/generate-qr-matrix content :error-correction error-correction)]
    (matrix->svg qr-data
                 :output-size output-size
                 :margin margin
                 :foreground foreground
                 :background background
                 :label label)))
