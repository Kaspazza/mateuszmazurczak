(ns mateuszmazurczak.ports.export
  "Port for file export operations.
   
   Hybrid port pattern - directly uses the file-export adapter
   since we don't expect multiple implementations."
  (:require
   [mateuszmazurczak.adapters.export.file-export :as file-export]))

(defn download-qr-codes!
  "Download QR codes in specified format.
   
   Arguments:
   - codes: Vector of {:content :svg :filename} maps (from generate-batch)
   - opts: {:size int :filename string :format (:zip|:jpg|:pdf) :as-svg? bool}
   
   Returns: Promise that resolves when download starts"
  [codes opts]
  (file-export/download! codes opts))

(defn download-qr-codes-zip!
  "Download QR codes as a ZIP file.
   
   Arguments:
   - codes: Vector of {:content :svg :filename} maps (from generate-batch)
   - opts: {:size int :filename string :as-svg? bool}
   
   Returns: Promise that resolves when download starts"
  [codes opts]
  (file-export/download-zip! codes opts))

(defn download-qr-codes-pdf!
  "Download QR codes as a PDF file.
   Each QR code gets its own page with label.
   
   Arguments:
   - codes: Vector of {:content :svg :filename} maps (from generate-batch)
   - opts: {:size int :filename string}
   
   Returns: Promise that resolves when download starts"
  [codes opts]
  (file-export/download-pdf! codes opts))

(defn save-blob!
  "Trigger browser download for any Blob.
   
   Arguments:
   - blob: Blob to download
   - filename: Suggested filename"
  [blob filename]
  (file-export/save-blob! blob filename))

(defn save-array-buffer!
  "Trigger browser download for an ArrayBuffer.
   
   Arguments:
   - array-buffer: ArrayBuffer to download
   - opts: {:filename string :format (:zip|:jpg|:pdf)}"
  [array-buffer opts]
  (file-export/save-array-buffer! array-buffer opts))
