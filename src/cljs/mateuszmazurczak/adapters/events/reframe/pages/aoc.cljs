(ns mateuszmazurczak.adapters.events.reframe.pages.aoc
  "Re-frame adapter for AoC page events.
   
   Implements AoC page events from events/registry.cljc as a handler map.
   This is INTERNAL adapter code - UI components should dispatch via events/dispatch!"
  (:require
   [mateuszmazurczak.application.aoc.cache-service :as aoc-cache]
   [mateuszmazurczak.application.aoc.use-cases     :as aoc-use-cases]
   [mateuszmazurczak.domain.pages.aoc              :as aoc-domain]
   [mateuszmazurczak.domain.state.registry         :as state-registry]
   [mateuszmazurczak.ports.logging                 :as log]
   [mateuszmazurczak.ui.components.notification    :as notification]
   [mateuszmazurczak.utils.url                     :as url-utils]
   [re-frame.core                                  :as rf]))

;; =============================================================================
;; Internal Effects (not in registry - re-frame specific)
;; =============================================================================

(rf/reg-fx ::cache-consent (fn [[year challenge]] (aoc-cache/add-consent! year challenge)))

(rf/reg-fx ::cache-solution-id
           (fn [[year challenge solution-id]]
             (aoc-cache/add-solution-id! year challenge solution-id)))

(rf/reg-fx ::cache-vote (fn [[solution-id vote-type]] (aoc-cache/add-vote! solution-id vote-type)))

;; =============================================================================
;; Helpers
;; =============================================================================

(def handlers
  "AoC page event handlers exported as data.
   
   Each handler is a pure function that will be registered with re-frame
   by the wiring namespace. The registry metadata determines whether it's
   registered as :db or :fx handler.
   
   :fx handlers receive {:keys [db]} coeffects and return effects map
   :db handlers receive db directly and return updated db"
  {:aoc/on-route-enter (fn [{:keys [db]} [_ path-params]]
                         (let [{:keys [page-data dispatches]}
                               (aoc-use-cases/handle-page-entry
                                {:path-params path-params
                                 :url (url-utils/current-url)
                                 :url-hash (.-hash js/window.location)
                                 :existing-page-data (get-in db state-registry/*aoc-page-path*)
                                 :years-options (get-in db aoc-domain/*aoc-years-options-path*)})]
                           {:db (assoc-in db state-registry/*aoc-page-path* page-data)
                            :dispatch-n dispatches}))
   :aoc/select-year
   (fn [{:keys [db]} [_ year-str]]
     (let [{:keys [state-updates dispatches]} (aoc-use-cases/handle-year-selection year-str)]
       {:db (-> db
                (assoc-in aoc-domain/*aoc-selected-year-path* (:selected-year state-updates))
                (assoc-in aoc-domain/*aoc-selected-challenge-path*
                          (:selected-challenge state-updates))
                (assoc-in aoc-domain/*aoc-challenges-options-path*
                          (:challenges-options state-updates)))
        :dispatch-n dispatches}))
   :aoc/select-challenge
   (fn [{:keys [db]} [_ challenge-str]]
     (let [year (get-in db aoc-domain/*aoc-selected-year-path*)
           {:keys [state-updates dispatches]} (aoc-use-cases/handle-challenge-selection
                                                challenge-str
                                                year)]
       {:db (assoc-in db
                      aoc-domain/*aoc-selected-challenge-path*
                      (:selected-challenge state-updates))
        :dispatch-n dispatches}))
   :aoc/open-modal
   (fn [db [_]]
     (let [{:keys [state-updates]}
           (aoc-use-cases/handle-modal-open
            {:playground-url
             (get-in db (conj state-registry/*aoc-page-path* :modal-data :playground-url))
             :form-year (get-in db (conj aoc-domain/*aoc-form-path* :year))
             :form-challenge (get-in db (conj aoc-domain/*aoc-form-path* :challenge))
             :page-year (get-in db aoc-domain/*aoc-selected-year-path*)
             :page-challenge (get-in db aoc-domain/*aoc-selected-challenge-path*)})]
       (-> db
           (assoc-in aoc-domain/*aoc-modal-open-path* (:modal-open? state-updates))
           (assoc-in (conj aoc-domain/*aoc-form-path* :year) (:form-year state-updates))
           (assoc-in (conj aoc-domain/*aoc-form-path* :challenge) (:form-challenge state-updates))
           (assoc-in (conj state-registry/*aoc-page-path* :modal-data :challenges-options)
                     (:modal-challenges-options state-updates)))))
   :aoc/upload-limit-reached (fn [db [_]]
                               (let [translator (get-in db state-registry/*translator-path*)]
                                 (notification/show-error
                                  (translator :upload-limit-reached)
                                  {:description (translator :uploaded-max-solutions-for-challenge)})
                                 db))
   :aoc/close-modal
   (fn [db [_]]
     (let [{:keys [state-updates]} (aoc-use-cases/handle-modal-close
                                    (get-in db aoc-domain/*aoc-selected-year-path*)
                                    (get-in db aoc-domain/*aoc-selected-challenge-path*))]
       (-> db
           (assoc-in aoc-domain/*aoc-modal-open-path* (:modal-open? state-updates))
           (assoc-in aoc-domain/*aoc-form-path* (:form state-updates))
           (assoc-in (conj state-registry/*aoc-page-path* :modal-data :playground-url)
                     (:playground-url state-updates)))))
   :aoc/update-form
   (fn [db [_ field value]]
     (let [form (get-in db aoc-domain/*aoc-form-path*)
           form-errors (get-in db aoc-domain/*aoc-form-errors-path*)
           {:keys [state-updates]} (aoc-use-cases/handle-form-update form form-errors field value)]
       (-> db
           (assoc-in aoc-domain/*aoc-form-path* (:form state-updates))
           (assoc-in aoc-domain/*aoc-form-errors-path* (:form-errors state-updates)))))
   :aoc/modal-select-year
   (fn [db [_ year-str]]
     (let [{:keys [state-updates]} (aoc-use-cases/handle-modal-year-selection year-str)]
       (-> db
           (assoc-in (conj aoc-domain/*aoc-form-path* :year) (:form-year state-updates))
           (assoc-in (conj aoc-domain/*aoc-form-path* :challenge) (:form-challenge state-updates))
           (assoc-in (conj state-registry/*aoc-page-path* :modal-data :challenges-options)
                     (:modal-challenges-options state-updates)))))
   :aoc/modal-select-challenge
   (fn [db [_ challenge-str]]
     (let [{:keys [state-updates]} (aoc-use-cases/handle-modal-challenge-selection challenge-str)]
       (assoc-in db (conj aoc-domain/*aoc-form-path* :challenge) (:form-challenge state-updates))))
      :aoc/submit-solution
      (fn [{:keys [db]} [_]]
        (let [translator (get-in db state-registry/*translator-path*)
              {:keys [can-submit? reason payload state-updates]}
              (aoc-use-cases/prepare-submission
               {:form (get-in db aoc-domain/*aoc-form-path*)
                :page-year (get-in db aoc-domain/*aoc-selected-year-path*)
                :page-challenge (get-in db aoc-domain/*aoc-selected-challenge-path*)
                :can-upload-fn aoc-cache/can-upload?})
              updated-db (cond-> db
                           (:form-errors state-updates)
                           (assoc-in aoc-domain/*aoc-form-errors-path* (:form-errors state-updates))
                           
                           (contains? state-updates :submitting?)
                           (assoc-in aoc-domain/*aoc-submitting-path* (:submitting? state-updates)))]
          (cond
            (= reason :upload-limit-reached)
            (do (notification/show-error (translator :upload-limit-reached)
                                         {:description (translator
                                                        :uploaded-max-solutions-for-challenge)})
                {:db db})
            
            (= reason :validation-errors)
            (do (notification/show-error (translator :please-fix-form-errors)
                                         {:description (translator :required-fields-missing)})
                {:db updated-db})
            
            can-submit?
            {:db updated-db
             :http {:method :post
                    :url "/api/aoc/solutions"
                    :params payload
                    :event/on-success [:aoc/submit-success]
                    :event/on-error [:aoc/submit-failure]}}
            
            :else {:db db})))
   :aoc/submit-success
   (fn [{:keys [db]} [_ response]]
     (let [translator (get-in db state-registry/*translator-path*)
           {:keys [year challenge state-updates navigate]}
           (aoc-use-cases/handle-submission-success
            {:form (get-in db aoc-domain/*aoc-form-path*)
             :page-year (get-in db aoc-domain/*aoc-selected-year-path*)
             :page-challenge (get-in db aoc-domain/*aoc-selected-challenge-path*)})
           solution-id (:solution-id response)
           updated-db (-> db
                          (assoc-in aoc-domain/*aoc-submitting-path*
                                    (:submitting? state-updates))
                          (assoc-in aoc-domain/*aoc-modal-open-path*
                                    (:modal-open? state-updates))
                          (assoc-in aoc-domain/*aoc-form-path*
                                    (:form state-updates))
                          (assoc-in aoc-domain/*aoc-form-errors-path*
                                    (:form-errors state-updates))
                          (assoc-in (conj state-registry/*aoc-page-path* :modal-data :playground-url)
                                    (:playground-url state-updates))
                          (assoc-in aoc-domain/*aoc-selected-year-path*
                                    (:selected-year state-updates))
                          (assoc-in aoc-domain/*aoc-selected-challenge-path*
                                    (:selected-challenge state-updates))
                          (assoc-in aoc-domain/*aoc-challenges-options-path*
                                    (:challenges-options state-updates))
                          (assoc-in (conj state-registry/*aoc-page-path*
                                          :modal-data
                                          :challenges-options)
                                    (:modal-challenges-options state-updates)))]
       (notification/show-success (translator :solution-submitted-successfully))
       {:db updated-db
        ::cache-consent [year challenge]
        ::cache-solution-id [year challenge solution-id]
        :dispatch-n [navigate [:aoc/fetch-solutions year challenge]]}))
   :aoc/submit-failure (fn [{:keys [db]} [_ error]]
                         (let [translator (get-in db state-registry/*translator-path*)
                               logger (get-in db state-registry/*logger-path*)
                               error-message (or (get-in error [:response :message])
                                                 (get error :message)
                                                 "Unknown error occurred")]
                           (log/error! logger
                                       {:error (ex-info "Failed to submit solution"
                                                        {:type ::submit-solution-failed
                                                         :error error})})
                           (notification/show-error (translator :failed-to-submit-solution)
                                                    {:description error-message})
                           {:db (assoc-in db aoc-domain/*aoc-submitting-path* false)}))
   :aoc/fetch-solutions (fn [{:keys [db]} [_ year challenge]]
                          {:db (assoc-in db aoc-domain/*aoc-loading-path* true)
                           :http {:method :get
                                  :url "/api/aoc/solutions"
                                  :params {:year year
                                           :challenge challenge}
                                  :event/on-success [:aoc/fetch-solutions-success year challenge]
                                  :event/on-error [:aoc/fetch-solutions-failure]}})
   :aoc/fetch-solutions-success
   (fn [db [_ year challenge response]]
     (let [solutions (:solutions response)
           {:keys [state-updates]}
           (aoc-use-cases/handle-fetch-solutions-success
            {:solutions solutions
             :year year
             :challenge challenge
             :has-consented-fn aoc-cache/has-consented?
             :get-user-solution-ids-fn aoc-cache/get-user-solution-ids
             :get-upload-count-fn aoc-cache/get-upload-count
             :enrich-voting-fn #(aoc-domain/enrich-solution-voting-state % aoc-cache/has-voted?)})]
       (-> db
           (assoc-in state-registry/*aoc-solutions-path* (:solutions-entities state-updates))
           (assoc-in aoc-domain/*aoc-solution-ids-path* (:solution-ids state-updates))
           (assoc-in aoc-domain/*aoc-user-solution-ids-path* (:user-solution-ids state-updates))
           (assoc-in aoc-domain/*aoc-upload-count-path* (:upload-count state-updates))
           (assoc-in aoc-domain/*aoc-gated-path* (:gated? state-updates))
           (assoc-in aoc-domain/*aoc-loading-path* (:loading? state-updates)))))
   :aoc/fetch-solutions-failure (fn [{:keys [db]} [_ error]]
                                  (let [logger (get-in db state-registry/*logger-path*)]
                                    (log/error! logger
                                                {:error (ex-info "Failed to fetch solutions"
                                                                 {:type ::fetch-solutions-failed
                                                                  :error error})})
                                    {:db (-> db
                                             (assoc-in aoc-domain/*aoc-solution-ids-path* [])
                                             (assoc-in aoc-domain/*aoc-loading-path* false))}))
   :aoc/vote (fn [{:keys [db]} [_ solution-id vote-type]]
               (let [translator (get-in db state-registry/*translator-path*)
                     {:keys [can-vote?]}
                     (aoc-use-cases/check-can-vote aoc-cache/has-voted? solution-id vote-type)]
                 (if can-vote?
                   {:http {:method :post
                           :url "/api/aoc/solutions/vote"
                           :params {:solution-id solution-id
                                    :vote-type vote-type}
                           :event/on-success [:aoc/vote-success solution-id vote-type]
                           :event/on-error [:aoc/vote-failure]}}
                   (do (notification/show-error (translator :already-voted-for-solution))
                       {:db db}))))
   :aoc/vote-success
   (fn [{:keys [db]} [_ solution-id vote-type response]]
     (let [best-practices-count (:best-practices-count response)
           clever-count (:clever-count response)
           current-solution (get-in db (concat state-registry/*aoc-solutions-path* [solution-id]))
           updated-solution (aoc-use-cases/handle-vote-success current-solution
                                                               vote-type
                                                               best-practices-count
                                                               clever-count)]
       {:db
        (assoc-in db (concat state-registry/*aoc-solutions-path* [solution-id]) updated-solution)
        ::cache-vote [solution-id vote-type]}))
   :aoc/vote-failure (fn [{:keys [db]} [_ error]]
                       (let [translator (get-in db state-registry/*translator-path*)
                             logger (get-in db state-registry/*logger-path*)
                             error-message (or (get-in error [:response :message])
                                               (get error :message)
                                               (translator :failed-to-vote))]
                         (log/error! logger
                                     {:error (ex-info "Failed to vote for solution"
                                                      {:type ::vote-failed
                                                       :error error})})
                         (notification/show-error error-message)
                         {:db db}))
   :aoc/give-consent (fn [{:keys [db]} [_ year challenge]]
                       {::cache-consent [year challenge]
                        :db (assoc-in db aoc-domain/*aoc-gated-path* false)})
   :aoc/highlight-solution
   (fn [db [_ solution-id]] (assoc-in db aoc-domain/*aoc-highlighted-solution-id-path* solution-id))
   :aoc/clear-highlight (fn [db [_]]
                          (assoc-in db aoc-domain/*aoc-highlighted-solution-id-path* nil))})
