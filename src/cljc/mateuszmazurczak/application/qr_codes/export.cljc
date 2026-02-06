(ns mateuszmazurczak.application.qr-codes.export
  "Export utilities for QR codes.

   Includes filename sanitization and export-format validation."
  (:require
   [clojure.string                             :as str]
   [mateuszmazurczak.domain.qr-codes.generator :as gen]
   #?@(:cljs [[goog.string :as gstring] [goog.string.format]])))

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

(defn validate-export-request
  "Validate QR code export request (contents, size, and format)."
  [{:keys [contents size format]}]
  (let [{:keys [errors]} (gen/validate-request {:contents contents
                                                :size size})
        format-errors (cond-> []
                        (and (some? format) (not (#{:zip :jpg :pdf} format)))
                        (conj "Format must be :zip, :jpg, or :pdf"))
        all-errors (into (vec errors) format-errors)]
    {:valid? (empty? all-errors)
     :errors all-errors}))
