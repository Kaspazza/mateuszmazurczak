(ns mateuszmazurczak.domain.qr-codes.generator
  "Pure domain logic for QR code generation.
   Cross-platform (CLJ/CLJS) using Nayuki's QR-Code-generator library.

   Design Pattern: Strategy Pattern - same interface, platform-specific impl.
   Algorithm: QR Code encoding (ISO/IEC 18004)"
  (:require
   [clojure.string :as str]
   #?@(:cljs [["nayuki-qr-code-generator" :as qrcodegen]]))
  #?(:clj (:import [io.nayuki.qrcodegen QrCode QrCode$Ecc])))

;; Constants
(def default-qr-pixel-size 300)
(def min-qr-pixel-size 50)
(def max-qr-pixel-size 2000)
(def max-codes-per-batch 100)

;; Pure Domain Functions

(defn parse-input
  "Parse newline-separated input into a vector of trimmed, non-empty strings.
   Returns nil for nil input, empty vector for empty input."
  [input]
  (when input
    (if (str/blank? input)
      []
      (->> (str/split-lines input)
           (map str/trim)
           (remove str/blank?)
           vec))))

(defn- pad-number
  "Pad a number with leading zeros to 3 digits."
  [n]
  (let [s (str n)]
    (cond
      (< n 10) (str "00" s)
      (< n 100) (str "0" s)
      :else s)))

(defn sanitize-filename
  "Create a safe filename from QR content and index.
   Replaces special characters with underscores, truncates long content.
   Format: qr_<padded-index>_<sanitized-content>.png"
  [content index]
  (let [sanitized (-> content
                      (str/replace #"[^a-zA-Z0-9]+" "_")
                      (str/replace #"^_+|_+$" ""))
        truncated (if (> (count sanitized) 40) (subs sanitized 0 40) sanitized)
        padded-index (pad-number (inc index))]
    (str "qr_" padded-index "_" truncated ".png")))

(defn valid-size?
  "Check if QR code pixel size is within acceptable bounds."
  [size]
  (and (number? size) (>= size min-qr-pixel-size) (<= size max-qr-pixel-size)))

(defn validate-request
  "Validate QR code generation request (contents + size only)."
  [{:keys [contents size]}]
  (let [errors (cond-> []
                 (or (nil? contents) (empty? contents)) (conj "No QR code values provided")
                 (and (some? size) (not (valid-size? size)))
                 (conj (str "Size must be between " min-qr-pixel-size " and " max-qr-pixel-size)))]
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
       (let [QrCode (-> qrcodegen
                        .-default
                        .-QrCode)
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
