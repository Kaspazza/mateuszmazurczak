(ns mateuszmazurczak.adapters.events.reframe.pages.qr-codes
  "Re-frame adapter for QR codes page events."
  (:require
   [mateuszmazurczak.application.qr-codes.page-data   :as page-data]
   [mateuszmazurczak.application.qr-codes.page-schema :as page-schema]
   [mateuszmazurczak.application.qr-codes.processing  :as qr-processing]
   [mateuszmazurczak.domain.state.registry            :as state-registry]
   [mateuszmazurczak.ports.background-processing      :as bg-worker]
   [mateuszmazurczak.ports.export                     :as export]
   [mateuszmazurczak.ports.logging                    :as log]
   [re-frame.core                                     :as rf]))

;; =============================================================================
;; Re-frame Effects (Framework Integration)
;; =============================================================================

(rf/reg-fx ::init-qr-worker
           (fn [{:keys [request-id config on-ready on-progress on-finalizing on-done on-failure]}]
             (bg-worker/init-qr-worker! request-id
                                        config
                                        {:on-ready on-ready
                                         :on-progress on-progress
                                         :on-finalizing on-finalizing
                                         :on-done on-done
                                         :on-failure on-failure
                                         :on-dispatch (fn [dispatch-v] (rf/dispatch dispatch-v))})))

(rf/reg-fx ::request-qr-batch
           (fn [{:keys [request-id]}] (bg-worker/request-next-batch! request-id)))

(rf/reg-fx ::cancel-qr-worker (fn [{:keys [request-id]}] (bg-worker/cancel-request! request-id)))

(rf/reg-fx ::save-qr-batch
           (fn [{:keys [buffer opts on-success on-failure]}]
             (try (export/save-array-buffer! buffer opts)
                  (when on-success (rf/dispatch on-success))
                  (catch :default error (when on-failure (rf/dispatch (conj on-failure error)))))))

;; =============================================================================
;; Event Handlers
;; =============================================================================

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
   :qr-codes/update-png-background (fn [db [_ background]]
                                     (update-in db
                                                state-registry/*qr-codes-page-path*
                                                page-data/update-page-png-background
                                                background))
   :qr-codes/update-png-margin
   (fn [db [_ margin]]
     (update-in db state-registry/*qr-codes-page-path* page-data/update-page-png-margin margin))
   :qr-codes/update-pdf-layout
   (fn [db [_ layout]]
     (update-in db state-registry/*qr-codes-page-path* page-data/update-page-pdf-layout layout))
   :qr-codes/update-pdf-custom (fn [db [_ field value]]
                                 (update-in db
                                            state-registry/*qr-codes-page-path*
                                            page-data/update-page-pdf-custom
                                            field
                                            value))
   :qr-codes/generate-preview
   (fn [db [_]] (update-in db state-registry/*qr-codes-page-path* page-data/generate-page-preview))
   :qr-codes/download (fn [{:keys [db]} [_]]
                        (let [page-data (get-in db state-registry/*qr-codes-page-path*)
                              {:keys [request-id page-data worker-config]}
                              (qr-processing/start-download page-data)]
                          {:db (assoc-in db state-registry/*qr-codes-page-path* page-data)
                           ::init-qr-worker {:request-id request-id
                                             :config worker-config
                                             :on-ready [:qr-codes/worker-ready]
                                             :on-progress [:qr-codes/worker-progress]
                                             :on-finalizing [:qr-codes/worker-finalizing]
                                             :on-done [:qr-codes/worker-done]
                                             :on-failure [:qr-codes/worker-failure]}}))
   :qr-codes/worker-ready (fn [{:keys [db]} [_ {:keys [request-id]}]]
                            (let [page-data (get-in db state-registry/*qr-codes-page-path*)
                                  {:keys [page-data request-id]}
                                  (qr-processing/handle-worker-ready page-data request-id)]
                              {:db (assoc-in db state-registry/*qr-codes-page-path* page-data)
                               ::request-qr-batch {:request-id request-id}}))
   :qr-codes/worker-progress
   (fn [{:keys [db]} [_ {:keys [request-id current total]}]]
     (let [page-data (get-in db state-registry/*qr-codes-page-path*)
           {:keys [page-data request-id]}
           (qr-processing/handle-worker-progress page-data request-id current total)]
       {:db (assoc-in db state-registry/*qr-codes-page-path* page-data)
        ::request-qr-batch {:request-id request-id}}))
   :qr-codes/worker-finalizing (fn [db [_ {:keys [format]}]]
                                 (let [page-data (get-in db state-registry/*qr-codes-page-path*)
                                       {:keys [page-data]}
                                       (qr-processing/handle-worker-finalizing page-data format)]
                                   (assoc-in db state-registry/*qr-codes-page-path* page-data)))
   :qr-codes/worker-done
   (fn [{:keys [db]} [_ {:keys [buffer]}]]
     (let [page-data (get-in db state-registry/*qr-codes-page-path*)
           {:keys [page-data save-batch]} (qr-processing/handle-worker-done page-data buffer)]
       {:db (assoc-in db state-registry/*qr-codes-page-path* page-data)
        ::save-qr-batch
        (assoc save-batch :on-success nil :on-failure [:qr-codes/download-failure])}))
   :qr-codes/worker-failure (fn [{:keys [db]} [_ errors]]
                              (let [page-data (get-in db state-registry/*qr-codes-page-path*)
                                    {:keys [page-data log-error]}
                                    (qr-processing/handle-worker-failure page-data errors)]
                                (when-let [logger (get-in db state-registry/*logger-path*)]
                                  (log/error! logger {:error log-error}))
                                {:db (assoc-in db state-registry/*qr-codes-page-path* page-data)}))
   :qr-codes/download-failure
   (fn [{:keys [db]} [_ error]]
     (let [page-data (get-in db state-registry/*qr-codes-page-path*)
           {:keys [page-data log-error]} (qr-processing/handle-download-failure page-data error)
           logger (get-in db state-registry/*logger-path*)]
       (log/error! logger {:error log-error})
       {:db (assoc-in db state-registry/*qr-codes-page-path* page-data)}))})
