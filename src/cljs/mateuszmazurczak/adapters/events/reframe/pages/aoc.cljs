(ns mateuszmazurczak.adapters.events.reframe.pages.aoc
  "Re-frame adapter for AoC page events.
   
   Implements AoC page events from events/registry.cljc as a handler map.
   This is INTERNAL adapter code - UI components should dispatch via events/dispatch!"
  (:require
   [mateuszmazurczak.application.aoc.cache-service :as aoc-cache]
   [mateuszmazurczak.domain.pages.aoc              :as aoc-domain]
   [mateuszmazurczak.domain.state.registry         :as state-registry]
   [mateuszmazurczak.ports.logging                 :as log]
   [mateuszmazurczak.ui.components.notification    :as notification]
   [re-frame.core                                  :as rf]))

;; =============================================================================
;; Internal Effects (not in registry - re-frame specific)
;; =============================================================================

(rf/reg-fx ::cache-consent
           (fn [[year challenge part]] (aoc-cache/add-consent! year challenge part)))

(rf/reg-fx ::cache-solution-id
           (fn [[year challenge part solution-id]]
             (aoc-cache/add-solution-id! year challenge part solution-id)))

(rf/reg-fx ::cache-vote (fn [[solution-id vote-type]] (aoc-cache/add-vote! solution-id vote-type)))

;; =============================================================================
;; Helpers
;; =============================================================================

(defn- reset-form
  "Reset form to initial state."
  []
  {:author-name ""
   :github-profile ""
   :content-type :code-snippet
   :content ""})

;;TODO rethink what logic from here should go to domain
;;TODO some text here that should be :i18n
(def handlers
  "AoC page event handlers exported as data.
   
   Each handler is a pure function that will be registered with re-frame
   by the wiring namespace. The registry metadata determines whether it's
   registered as :db or :fx handler.
   
   :fx handlers receive {:keys [db]} coeffects and return effects map
   :db handlers receive db directly and return updated db"
  {:aoc/on-route-enter
   (fn [{:keys [db]} [_]]
     (let [existing-data (get-in db state-registry/*aoc-page-path*)
           initial-data (aoc-domain/build-initial-page-data)
           years-options (get-in db aoc-domain/*aoc-years-options-path*)
           page-data
           (if (or (nil? existing-data) (empty? years-options)) initial-data existing-data)
           selected-year (get-in db aoc-domain/*aoc-selected-year-path*)
           selected-challenge (get-in db aoc-domain/*aoc-selected-challenge-path*)
           selected-part (get-in db aoc-domain/*aoc-selected-part-path*)]
       {:db (assoc-in db state-registry/*aoc-page-path* page-data)
        :dispatch-n [[:admin/check-status]
                     [:aoc/fetch-solutions selected-year selected-challenge selected-part]]}))
   ;; === Filters ===
   :aoc/select-year (fn [{:keys [db]} [_ year-str]]
                      (let [year (js/parseInt year-str)
                            challenges (aoc-domain/challenges-for-year year)
                            first-challenge (first challenges)
                            challenges-options (aoc-domain/build-challenges-options year)
                            part (get-in db aoc-domain/*aoc-selected-part-path*)]
                        {:db (-> db
                                 (assoc-in aoc-domain/*aoc-selected-year-path* year)
                                 (assoc-in aoc-domain/*aoc-selected-challenge-path* first-challenge)
                                 (assoc-in aoc-domain/*aoc-challenges-options-path*
                                           challenges-options))
                         :dispatch [:aoc/fetch-solutions year first-challenge part]}))
   :aoc/select-challenge (fn [{:keys [db]} [_ challenge-str]]
                           (let [challenge (js/parseInt challenge-str)
                                 year (get-in db aoc-domain/*aoc-selected-year-path*)
                                 part (get-in db aoc-domain/*aoc-selected-part-path*)]
                             {:db (assoc-in db aoc-domain/*aoc-selected-challenge-path* challenge)
                              :dispatch [:aoc/fetch-solutions year challenge part]}))
   :aoc/select-part (fn [{:keys [db]} [_ part-str]]
                      (let [part (js/parseInt part-str)
                            year (get-in db aoc-domain/*aoc-selected-year-path*)
                            challenge (get-in db aoc-domain/*aoc-selected-challenge-path*)]
                        {:db (assoc-in db aoc-domain/*aoc-selected-part-path* part)
                         :dispatch [:aoc/fetch-solutions year challenge part]}))
   ;; === Modal state (:db handlers) ===
   :aoc/open-modal (fn [db [_]]
                     (let [year (get-in db aoc-domain/*aoc-selected-year-path*)
                           challenge (get-in db aoc-domain/*aoc-selected-challenge-path*)
                           part (get-in db aoc-domain/*aoc-selected-part-path*)]
                       (if (aoc-cache/can-upload? year challenge part)
                         (assoc-in db aoc-domain/*aoc-modal-open-path* true)
                         (do (notification/show-error
                              "Upload limit reached"
                              {:description
                               "You have already uploaded 5 solutions for this challenge"})
                             db))))
   :aoc/close-modal (fn [db [_]] (assoc-in db aoc-domain/*aoc-modal-open-path* false))
   :aoc/update-form (fn [db [_ field value]]
                      (-> db
                          (assoc-in (conj aoc-domain/*aoc-form-path* field) value)
                          (update-in aoc-domain/*aoc-form-errors-path* dissoc field)))
   ;; === Solution submission ===
   :aoc/submit-solution
   (fn [{:keys [db]} [_]]
     (let [form (get-in db aoc-domain/*aoc-form-path*)
           year (get-in db aoc-domain/*aoc-selected-year-path*)
           challenge (get-in db aoc-domain/*aoc-selected-challenge-path*)
           part (get-in db aoc-domain/*aoc-selected-part-path*)
           payload (assoc form :year year :challenge challenge :part part)
           validation-errors (aoc-domain/validate-solution-form form)]
       (cond
         (not (aoc-cache/can-upload? year challenge part))
         (do (notification/show-error "Upload limit reached"
                                      {:description
                                       "You have already uploaded 5 solutions for this challenge"})
             {:db db})
         validation-errors
         (do (notification/show-error "Please fix the form errors"
                                      {:description "Some required fields are missing"})
             {:db (assoc-in db aoc-domain/*aoc-form-errors-path* validation-errors)})
         :else {:db (-> db
                        (assoc-in aoc-domain/*aoc-submitting-path* true)
                        (assoc-in aoc-domain/*aoc-form-errors-path* nil))
                :http {:method :post
                       :url "/api/aoc/solutions"
                       :params payload
                       :event/on-success [:aoc/submit-success]
                       :event/on-error [:aoc/submit-failure]}})))
   :aoc/submit-success (fn [{:keys [db]} [_ response]]
                         (let [year (get-in db aoc-domain/*aoc-selected-year-path*)
                               challenge (get-in db aoc-domain/*aoc-selected-challenge-path*)
                               part (get-in db aoc-domain/*aoc-selected-part-path*)
                               solution-id (:solution-id response)]
                           (notification/show-success "Solution submitted successfully!")
                           {:db (-> db
                                    (assoc-in aoc-domain/*aoc-submitting-path* false)
                                    (assoc-in aoc-domain/*aoc-modal-open-path* false)
                                    (assoc-in aoc-domain/*aoc-form-path* (reset-form))
                                    (assoc-in aoc-domain/*aoc-form-errors-path* nil))
                            ::cache-consent [year challenge part]
                            ::cache-solution-id [year challenge part solution-id]
                            :dispatch [:aoc/fetch-solutions year challenge part]}))
   :aoc/submit-failure
   (fn [{:keys [db]} [_ error]]
     (let [logger (get-in db state-registry/*logger-path*)
           error-message
           (or (get-in error [:response :message]) (get error :message) "Unknown error occurred")]
       (log/error! logger
                   {:error (ex-info "Failed to submit solution"
                                    {:type ::submit-solution-failed
                                     :error error})})
       (notification/show-error "Failed to submit solution" {:description error-message})
       {:db (assoc-in db aoc-domain/*aoc-submitting-path* false)}))
   ;; === Solutions fetching ===
   :aoc/fetch-solutions (fn [{:keys [db]} [_ year challenge part]]
                          {:db (assoc-in db aoc-domain/*aoc-loading-path* true)
                           :http {:method :get
                                  :url "/api/aoc/solutions"
                                  :params {:year year
                                           :challenge challenge
                                           :part part}
                                  :event/on-success
                                  [:aoc/fetch-solutions-success year challenge part]
                                  :event/on-error [:aoc/fetch-solutions-failure]}})
   :aoc/fetch-solutions-success
   (fn [db [_ year challenge part response]]
     (let [solutions (:solutions response)
           has-consent? (aoc-cache/has-consented? year challenge part)
           user-solution-ids (set (aoc-cache/get-user-solution-ids year challenge part))
           upload-count (aoc-cache/get-upload-count year challenge part)
           enrich-voting-state (fn [solution]
                                 (-> solution
                                     (update :content-type keyword)
                                     (assoc :voted-best-practices?
                                            (aoc-cache/has-voted? (:id solution) :best-practices))
                                     (assoc :voted-clever?
                                            (aoc-cache/has-voted? (:id solution) :clever))))
           enriched-solutions (mapv enrich-voting-state solutions)
           {:keys [entities ids]} (aoc-domain/normalize-solutions enriched-solutions)]
       (-> db
           (assoc-in state-registry/*aoc-solutions-path* entities)
           (assoc-in aoc-domain/*aoc-solution-ids-path* ids)
           (assoc-in aoc-domain/*aoc-user-solution-ids-path* user-solution-ids)
           (assoc-in aoc-domain/*aoc-upload-count-path* upload-count)
           (assoc-in aoc-domain/*aoc-gated-path* (not has-consent?))
           (assoc-in aoc-domain/*aoc-loading-path* false))))
   :aoc/fetch-solutions-failure (fn [{:keys [db]} [_ error]]
                                  (let [logger (get-in db state-registry/*logger-path*)]
                                    (log/error! logger
                                                {:error (ex-info "Failed to fetch solutions"
                                                                 {:type ::fetch-solutions-failed
                                                                  :error error})})
                                    {:db (-> db
                                             (assoc-in aoc-domain/*aoc-solution-ids-path* [])
                                             (assoc-in aoc-domain/*aoc-loading-path* false))}))
   ;; === Voting ===
   :aoc/vote (fn [{:keys [db]} [_ solution-id vote-type]]
               (if (aoc-cache/has-voted? solution-id vote-type)
                 (do (notification/show-error "You have already voted for this solution") {:db db})
                 {:http {:method :post
                         :url "/api/aoc/solutions/vote"
                         :params {:solution-id solution-id
                                  :vote-type vote-type}
                         :event/on-success [:aoc/vote-success solution-id vote-type]
                         :event/on-error [:aoc/vote-failure]}}))
   :aoc/vote-success
   (fn [{:keys [db]} [_ solution-id vote-type response]]
     (let [best-practices-count (:best-practices-count response)
           clever-count (:clever-count response)
           voted-key (if (= vote-type :best-practices) :voted-best-practices? :voted-clever?)]
       {:db (-> db
                (assoc-in (concat state-registry/*aoc-solutions-path*
                                  [solution-id :best-practices-count])
                          best-practices-count)
                (assoc-in (concat state-registry/*aoc-solutions-path* [solution-id :clever-count])
                          clever-count)
                (assoc-in (concat state-registry/*aoc-solutions-path* [solution-id voted-key])
                          true))
        ::cache-vote [solution-id vote-type]}))
   :aoc/vote-failure (fn [{:keys [db]} [_ error]]
                       (let [logger (get-in db state-registry/*logger-path*)
                             error-message (or (get-in error [:response :message])
                                               (get error :message)
                                               "Failed to vote")]
                         (log/error! logger
                                     {:error (ex-info "Failed to vote for solution"
                                                      {:type ::vote-failed
                                                       :error error})})
                         (notification/show-error error-message)
                         {:db db}))
   ;; === Consent ===
   :aoc/give-consent (fn [{:keys [db]} [_ year challenge part]]
                       {::cache-consent [year challenge part]
                        :db (assoc-in db aoc-domain/*aoc-gated-path* false)})})
