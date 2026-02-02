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

(defonce ^:private worker-state (atom {:worker nil :requests {}}))

(defn- handle-worker-message
  [event]
  (let [{:keys [type request-id batches errors]}
        (js->clj (.-data event) :keywordize-keys true)
        {:keys [on-success on-failure format size]}
        (get-in @worker-state [:requests request-id])
        message-type (keyword type)]
    (swap! worker-state update :requests dissoc request-id)
    (case message-type
      :worker-success
      (when on-success
        (rf/dispatch (conj on-success {:batches batches
                                       :format format
                                       :size size})))
      :worker-failure
      (when on-failure (rf/dispatch (conj on-failure errors)))
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

(rf/reg-fx ::generate-qr-batches
           (fn [{:keys [input size show-label? format on-success on-failure]}]
             (let [worker (ensure-worker)
                   request-id (str (random-uuid))]
               (swap! worker-state assoc-in
                      [:requests request-id]
                      {:on-success on-success
                       :on-failure on-failure
                       :format format
                       :size size})
               (.postMessage worker (clj->js {:request-id request-id
                                              :input input
                                              :size size
                                              :show-label? show-label?})))))

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
     (let [page-data (get-in db state-registry/*qr-codes-page-path*)]
       {:db (assoc-in db state-registry/*qr-codes-page-path* (assoc page-data :loading? true))
        ::generate-qr-batches {:input (:input page-data)
                               :size (:size page-data)
                               :show-label? (:show-label? page-data)
                               :format (:format page-data)
                               :on-success [:qr-codes/worker-success]
                               :on-failure [:qr-codes/worker-failure]}}))
   :qr-codes/worker-success
   (fn [{:keys [db]} [_ {:keys [batches format size]}]]
     (let [page-data (get-in db state-registry/*qr-codes-page-path*)
           download-batches (page-data/build-download-batches {:batches batches
                                                               :format format
                                                               :size size})]
       {:db (assoc-in db state-registry/*qr-codes-page-path* (assoc page-data :errors []))
        ::download-qr-codes {:batches download-batches
                             :on-success [:qr-codes/download-success]
                             :on-failure [:qr-codes/download-failure]}}))
   :qr-codes/worker-failure
   (fn [db [_ errors]]
     (update-in db state-registry/*qr-codes-page-path* assoc :errors errors :loading? false))
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
