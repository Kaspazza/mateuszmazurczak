(ns mateuszmazurczak.adapters.events.reframe.pages.qr-codes
  "Re-frame adapter for QR codes page events."
  (:require
   [mateuszmazurczak.application.qr-codes.page-data   :as page-data]
   [mateuszmazurczak.application.qr-codes.page-schema :as page-schema]
   [mateuszmazurczak.domain.state.registry            :as state-registry]
   [mateuszmazurczak.ports.export                     :as export]
   [mateuszmazurczak.ports.logging                    :as log]
   [re-frame.core                                     :as rf]))

;; =============================================================================
;; Internal Effects (re-frame specific)
;; =============================================================================

(rf/reg-fx ::download-qr-codes
           (fn [{:keys [batches on-success on-failure]}]
             (let [download-chain (reduce (fn [promise {:keys [codes opts]}]
                                            (.then promise
                                                   (fn [] (export/download-qr-codes! codes opts))))
                                          (js/Promise.resolve)
                                          batches)]
               (-> download-chain
                   (.then (fn [_] (when on-success (rf/dispatch on-success))))
                   (.catch (fn [error]
                             (when on-failure (rf/dispatch (conj on-failure error)))))))))

(def handlers
  "QR codes page event handlers."
  {:qr-codes/on-route-enter
   (fn [db [_]] (assoc-in db state-registry/*qr-codes-page-path* (page-schema/initial-page-data)))
   :qr-codes/update-input
   (fn [db [_ input]]
     (update-in db state-registry/*qr-codes-page-path* page-data/update-page-input input))
   :qr-codes/update-size
   (fn [db [_ size]]
     (update-in db state-registry/*qr-codes-page-path* page-data/update-page-size size))
   :qr-codes/update-format
   (fn [db [_ format]]
     (update-in db state-registry/*qr-codes-page-path* page-data/update-page-format format))
   :qr-codes/update-show-label (fn [db [_ show-label?]]
                                 (update-in db
                                            state-registry/*qr-codes-page-path*
                                            page-data/update-page-show-label
                                            show-label?))
   :qr-codes/generate-preview
   (fn [db [_]] (update-in db state-registry/*qr-codes-page-path* page-data/generate-page-preview))
   :qr-codes/download
   (fn [{:keys [db]} [_]]
     (let [page-data (get-in db state-registry/*qr-codes-page-path*)
           {:keys [status batches errors]} (page-data/prepare-download-batches page-data)]
       (if (= status :success)
         {:db (assoc-in db state-registry/*qr-codes-page-path* (assoc page-data :loading? true))
          ::download-qr-codes {:batches batches
                               :on-success [:qr-codes/download-success]
                               :on-failure [:qr-codes/download-failure]}}
         {:db (assoc-in db
              state-registry/*qr-codes-page-path*
              (assoc page-data :errors errors :loading? false))})))
   :qr-codes/download-success
   (fn [db [_]]
     (update-in db state-registry/*qr-codes-page-path* assoc :loading? false))
   :qr-codes/download-failure
   (fn [{:keys [db]} [_ error]]
     (let [logger (get-in db state-registry/*logger-path*)]
       (log/error! logger
                   {:error (ex-info "Failed to download QR codes"
                                    {:type ::download-failed
                                     :error error})})
       {:db (update-in db state-registry/*qr-codes-page-path* assoc :loading? false)}))})
