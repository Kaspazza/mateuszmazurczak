(ns mateuszmazurczak.domain.qr-codes.schema
  "Malli schemas for QR code generation domain."
  (:require
   [mateuszmazurczak.domain.qr-codes.generator :as gen]))

;; Error correction levels
(def ErrorCorrectionLevel [:enum :low :medium :quartile :high])

;; Output format
(def OutputFormat [:enum :zip :pdf])

;; PDF layout options
(def PdfLayout [:enum :per-page-30 :per-page-10 :per-page-6 :per-page-1 :custom])

;; QR code size (pixels)
(def QrSize
  [:int {:min gen/min-qr-pixel-size
         :max gen/max-qr-pixel-size}])

;; Single QR code content
(def QrContent
  [:string {:min 1
            :max 4296}]) ;; QR code max capacity

;; Batch of QR code contents
(def QrContents
  [:vector {:min 1
            :max gen/max-codes-per-batch}
   QrContent])

;; Generation request
(def GenerateRequest
  [:map
   [:contents QrContents]
   [:size {:optional true
           :default gen/default-qr-pixel-size}
    QrSize]
   [:format {:optional true
             :default :zip}
    OutputFormat]
   [:error-correction {:optional true
                       :default :medium}
    ErrorCorrectionLevel]])

;; Generated QR code result
(def GeneratedQrCode [:map [:content :string] [:svg :string] [:filename :string]])

;; Batch generation result
(def GenerateBatchResult
  [:or
   [:map [:success [:= true]] [:codes [:vector GeneratedQrCode]]]
   [:map [:success [:= false]] [:errors [:vector :string]]]])
