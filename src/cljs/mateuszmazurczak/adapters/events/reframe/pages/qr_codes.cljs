(ns mateuszmazurczak.adapters.events.reframe.pages.qr-codes
  "Re-frame adapter for QR codes page events."
  (:require
   [mateuszmazurczak.application.qr-codes.page-data   :as page-data]
   [mateuszmazurczak.application.qr-codes.page-schema :as page-schema]
   [mateuszmazurczak.domain.qr-codes.generator        :as gen]
   [mateuszmazurczak.domain.state.registry            :as state-registry]
   [mateuszmazurczak.ports.export                     :as export]
   [mateuszmazurczak.ports.logging                    :as log]
   [re-frame.core                                     :as rf]))

;; =============================================================================
;; Internal Effects (re-frame specific)
;; =============================================================================

(defonce ^:private worker-state
  (atom {:worker nil
         :requests {}}))

(defn- handle-worker-message
  [event]
  (let [data (.-data event)
        type (aget data "type")
        request-id (aget data "request-id")
        {:keys [on-ready on-progress on-finalizing on-done on-failure]}
        (get-in @worker-state [:requests request-id])
        message-type (if (keyword? type) (name type) type)]
    (case message-type
      "qr-codes/ready"
      (when on-ready
        (let [total-batches (aget data "total-batches")]
          (swap! worker-state assoc-in [:requests request-id :total-batches] total-batches)
          (rf/dispatch (conj on-ready
                             {:request-id request-id
                              :total-batches total-batches}))))
      "qr-codes/progress" (when on-progress
                            (rf/dispatch (conj on-progress
                                               {:request-id request-id
                                                :batch-index (aget data "batch-index")
                                                :total-batches (aget data "total-batches")
                                                :current (aget data "current")
                                                :total (aget data "total")})))
      "qr-codes/finalizing" (when on-finalizing
                              (rf/dispatch (conj on-finalizing
                                                 {:request-id request-id
                                                  :format (keyword (aget data "format"))})))
      "qr-codes/done" (do (swap! worker-state update :requests dissoc request-id)
                          (when on-done
                            (rf/dispatch (conj on-done
                                               {:request-id request-id
                                                :buffer (aget data "buffer")}))))
      "qr-codes/error" (do (swap! worker-state update :requests dissoc request-id)
                           (when on-failure
                             (rf/dispatch (conj on-failure (js->clj (aget data "errors"))))))
      nil)))

(defn- ensure-worker
  []
  (let [{:keys [worker]} @worker-state]
    (if worker
      worker
      (let [worker (js/Worker. "/js/compiled/qr-codes-worker.js")]
        (.addEventListener worker "message" handle-worker-message)
        (swap! worker-state assoc :worker worker)
        worker))))

(rf/reg-fx ::init-qr-worker
           (fn [{:keys [request-id
                        input
                        size
                        format
                        show-label?
                        pdf-layout-config
                        on-ready
                        on-progress
                        on-finalizing
                        on-done
                        on-failure]}]
             (let [worker (ensure-worker)]
               (swap! worker-state assoc-in
                 [:requests request-id]
                 {:on-ready on-ready
                  :on-progress on-progress
                  :on-finalizing on-finalizing
                  :on-done on-done
                  :on-failure on-failure})
               (.postMessage worker
                             (clj->js {:type "qr-codes/init"
                                       :request-id request-id
                                       :input input
                                       :size size
                                       :format format
                                       :show-label? show-label?
                                       :pdf-layout-config pdf-layout-config})))))

(rf/reg-fx ::request-qr-batch
           (fn [{:keys [request-id]}]
             (let [worker (ensure-worker)]
               (.postMessage worker
                             (clj->js {:type "qr-codes/next"
                                       :request-id request-id})))))

(rf/reg-fx ::cancel-qr-worker
           (fn [{:keys [request-id]}]
             (let [worker (ensure-worker)]
               (swap! worker-state update :requests dissoc request-id)
               (.postMessage worker
                             (clj->js {:type "qr-codes/cancel"
                                       :request-id request-id})))))

(rf/reg-fx ::save-qr-batch
           (fn [{:keys [buffer opts on-success on-failure]}]
             (try (export/save-array-buffer! buffer opts)
                  (when on-success (rf/dispatch on-success))
                  (catch :default error (when on-failure (rf/dispatch (conj on-failure error)))))))

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
                              request-id (str (random-uuid))]
                          {:db (assoc-in db
                                state-registry/*qr-codes-page-path*
                                (assoc page-data :loading? true :errors [] :download-progress nil))
                           ::init-qr-worker {:request-id request-id
                                             :input (:input page-data)
                                             :size (:size page-data)
                                             :format (:format page-data)
                                             :show-label? (:show-label? page-data)
                                             :pdf-layout-config (page-data/resolve-pdf-layout-config
                                                                 page-data)
                                             :on-ready [:qr-codes/worker-ready]
                                             :on-progress [:qr-codes/worker-progress]
                                             :on-finalizing [:qr-codes/worker-finalizing]
                                             :on-done [:qr-codes/worker-done]
                                             :on-failure [:qr-codes/worker-failure]}}))
   :qr-codes/worker-ready (fn [{:keys [db]} [_ {:keys [request-id]}]]
                            (let [page-data (get-in db state-registry/*qr-codes-page-path*)
                                  ;; Calculate total items from input
                                  contents (gen/parse-input (:input page-data))
                                  total-items (count contents)]
                              {:db (assoc-in db
                                    state-registry/*qr-codes-page-path*
                                    (assoc page-data
                                           :download-progress
                                           {:current 0
                                            :total total-items}))
                               ::request-qr-batch {:request-id request-id}}))
   :qr-codes/worker-progress (fn [{:keys [db]} [_ {:keys [request-id current total]}]]
                               {:db (update-in db
                                               state-registry/*qr-codes-page-path*
                                               assoc
                                               :download-progress
                                               {:current current
                                                :total total})
                                ::request-qr-batch {:request-id request-id}})
   :qr-codes/worker-finalizing (fn [db [_ {:keys [format]}]]
                                 (let [status-key (case format
                                                    :pdf :download-progress-pdf
                                                    :zip :download-progress-zip
                                                    :download-progress-finalizing)]
                                   (update-in db
                                              state-registry/*qr-codes-page-path*
                                              assoc
                                              :download-progress
                                              {:current nil
                                               :total nil
                                               :status-key status-key})))
   :qr-codes/worker-done (fn [{:keys [db]} [_ {:keys [buffer]}]]
                           (let [page-data (get-in db state-registry/*qr-codes-page-path*)
                                 ;; Build filename for archive
                                 ;; For PDF format, worker now returns a ZIP containing multiple PDFs
                                 extension (case (:format page-data)
                                             :pdf "zip"  ; Changed: PDF downloads are now ZIPs of PDFs
                                             :zip "zip")
                                 filename (str "qr-codes." extension)]
                             {:db (update-in db
                                             state-registry/*qr-codes-page-path*
                                             assoc
                                             :loading? false
                                             :download-progress nil)
                              ::save-qr-batch {:buffer buffer
                                               :opts {:filename filename}
                                               :on-success nil
                                               :on-failure [:qr-codes/download-failure]}}))
   :qr-codes/worker-failure (fn [{:keys [db]} [_ errors]]
                              (when-let [logger (get-in db state-registry/*logger-path*)]
                                (log/error! logger
                                            {:error (ex-info "QR code worker failed"
                                                             {:type ::worker-failed
                                                              :errors errors})}))
                              {:db (update-in db
                                              state-registry/*qr-codes-page-path*
                                              assoc
                                              :errors errors
                                              :loading? false
                                              :download-progress nil)})
   :qr-codes/download-failure (fn [{:keys [db]} [_ error]]
                                (let [logger (get-in db state-registry/*logger-path*)]
                                  (log/error! logger
                                              {:error (ex-info "Failed to download QR codes"
                                                               {:type ::download-failed
                                                                :error error})})
                                  {:db (update-in db
                                                  state-registry/*qr-codes-page-path*
                                                  assoc
                                                  :loading? false
                                                  :download-progress nil)}))})
