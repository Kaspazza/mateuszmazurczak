(ns mateuszmazurczak.adapters.events.reframe.pages.qr-codes
  "Re-frame adapter for QR codes page events."
  (:require
   [mateuszmazurczak.domain.pages.qr-codes     :as qr-domain]
   [mateuszmazurczak.domain.qr-codes.generator :as gen]
   [mateuszmazurczak.domain.state.registry     :as state-registry]
   [mateuszmazurczak.ports.export              :as export]))

(def handlers
  "QR codes page event handlers."
  {:qr-codes/on-route-enter
   (fn [db [_]] (assoc-in db state-registry/*qr-codes-page-path* qr-domain/initial-page-data))
   :qr-codes/update-input
   (fn [db [_ input]]
     (update-in db state-registry/*qr-codes-page-path* qr-domain/update-page-input input))
   :qr-codes/update-size
   (fn [db [_ size]]
     (update-in db state-registry/*qr-codes-page-path* qr-domain/update-page-size size))
   :qr-codes/update-format
   (fn [db [_ format]]
     (update-in db state-registry/*qr-codes-page-path* qr-domain/update-page-format format))
   :qr-codes/update-show-label
   (fn [db [_ show-label?]]
     (update-in db state-registry/*qr-codes-page-path* qr-domain/update-page-show-label show-label?))
   :qr-codes/generate-preview
   (fn [db [_]] (update-in db state-registry/*qr-codes-page-path* qr-domain/generate-page-preview))
   :qr-codes/download (fn [{:keys [db]} [_]]
                        (let [page-data (get-in db state-registry/*qr-codes-page-path*)
                              {:keys [input size format show-label?]} page-data
                              contents (gen/parse-input input)
                              result (gen/generate-batch contents :size size :show-label? show-label?)]
                          ;; Side effect must run here (no custom :fx key for downloads)
                          ;; In the future, this could be moved to a custom effect
                          (when (:success result)
                            (export/download-qr-codes!
                             (:codes result)
                             {:size size
                              :format format
                              :filename (if (= format :pdf) "qr-codes.pdf" "qr-codes.zip")}))
                          ;; Return unchanged db (download is pure side effect)
                          {:db db}))})
