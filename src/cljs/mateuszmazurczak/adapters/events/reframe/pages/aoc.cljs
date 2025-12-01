(ns mateuszmazurczak.adapters.events.reframe.pages.aoc
  "Re-frame adapter for AoC page events.
   
   Implements AoC page events from events/registry.cljc as a handler map.
   This is INTERNAL adapter code - UI components should dispatch via events/dispatch!"
  (:require
   [mateuszmazurczak.application.aoc.cache-service :as aoc-cache]
   [mateuszmazurczak.domain.pages.aoc              :as aoc-domain]
   [mateuszmazurczak.domain.state.registry         :as state-registry]
   [mateuszmazurczak.ports.logging                 :as log]
   [mateuszmazurczak.ports.navigation              :as nav]
   [mateuszmazurczak.ui.components.notification    :as notification]
   [mateuszmazurczak.utils.url                     :as url-utils]
   [re-frame.core                                  :as rf]))

;; =============================================================================
;; Internal Effects (not in registry - re-frame specific)
;; =============================================================================

(rf/reg-fx ::cache-consent
           (fn [[year challenge]] (aoc-cache/add-consent! year challenge)))

(rf/reg-fx ::cache-solution-id
           (fn [[year challenge solution-id]]
             (aoc-cache/add-solution-id! year challenge solution-id)))

(rf/reg-fx ::cache-vote (fn [[solution-id vote-type]] (aoc-cache/add-vote! solution-id vote-type)))

;; =============================================================================
;; Helpers
;; =============================================================================

(defn- reset-form
  "Reset form to initial state with current year/challenge from page state.
   
   Args:
   - year: Year value (required)
   - challenge: Challenge value (required)"
  [year challenge]
  {:author-name ""
   :github-profile ""
   :content-type :code-snippet
   :content ""
   :year year
   :challenge challenge})

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
   (fn [{:keys [db]} [_ path-params]]
     (let [;; Path params come directly from controller (already parsed as integers)
           url-year (:year path-params)
           url-challenge (:challenge path-params)
           ;; Check for query params (playground-url from external share)
           query-params (url-utils/parse-queries (url-utils/current-url))
           playground-url (:playground-url query-params)
           ;; Parse year/challenge from query params if present
           query-year (when-let [y (:year query-params)] (js/parseInt y))
           query-challenge (when-let [c (:challenge query-params)] (js/parseInt c))
           ;; Check for hash in URL (e.g., #solution-123)
           url-hash (.-hash js/window.location)
           solution-id (when (and url-hash (not= url-hash ""))
                         (second (re-find #"^#solution-(.+)$" url-hash)))
           existing-data (get-in db state-registry/*aoc-page-path*)
           initial-data (aoc-domain/build-initial-page-data)
           years-options (get-in db aoc-domain/*aoc-years-options-path*)
           page-data
           (if (or (nil? existing-data) (empty? years-options)) initial-data existing-data)
           default-year (get-in page-data [:selector-data :selected-year])
           default-challenge (get-in page-data [:selector-data :selected-challenge])
           selected-year (or url-year default-year)
           selected-challenge (or url-challenge default-challenge)
           valid-year? (some #{selected-year} aoc-domain/years)
           valid-challenge? (some #{selected-challenge}
                                  (aoc-domain/challenges-for-year selected-year))
           final-year (if valid-year? selected-year default-year)
           final-challenge (if (and valid-year? valid-challenge?)
                             selected-challenge
                             (first (aoc-domain/challenges-for-year final-year)))
           challenges-options (aoc-domain/build-challenges-options final-year)
           ;; Determine modal form year/challenge
           ;; Priority: query params > page selection
           modal-year (or query-year final-year)
           modal-challenge (or query-challenge final-challenge)
           valid-modal-year? (some #{modal-year} aoc-domain/years)
           valid-modal-challenge? (some #{modal-challenge}
                                        (aoc-domain/challenges-for-year modal-year))
           final-modal-year (if valid-modal-year? modal-year final-year)
           final-modal-challenge (if (and valid-modal-year? valid-modal-challenge?)
                                   modal-challenge
                                   (first (aoc-domain/challenges-for-year final-modal-year)))
           modal-challenges-options (aoc-domain/build-challenges-options final-modal-year)
           ;; Pre-fill form if solution is provided via query params (external mode)
           ;; In external mode, lock content-type to :repo-link and pre-fill content with playground-url
           form-data (if playground-url
                       {:author-name ""
                        :github-profile ""
                        :content-type :repo-link
                        :content playground-url
                        :year final-modal-year
                        :challenge final-modal-challenge}
                       (get-in page-data [:modal-data :form]))
           updated-page-data (-> page-data
                                 (assoc-in [:selector-data :selected-year] final-year)
                                 (assoc-in [:selector-data :selected-challenge] final-challenge)
                                 (assoc-in [:selector-data :challenges-options] challenges-options)
                                 (assoc-in [:modal-data :form] form-data)
                                 (assoc-in [:modal-data :playground-url] playground-url)
                                 (assoc-in [:modal-data :challenges-options]
                                           modal-challenges-options))
           dispatches (cond-> [[:admin/check-status]
                               [:aoc/fetch-solutions final-year final-challenge]]
                        solution-id (conj [:aoc/highlight-solution solution-id]
                                          [:dispatch-later {:ms 3000
                                                            :dispatch [:aoc/clear-highlight]}])
                        ;; Auto-open modal if solution is provided (external mode)
                        playground-url (conj [:aoc/open-modal]))]
       {:db (assoc-in db state-registry/*aoc-page-path* updated-page-data)
        :dispatch-n dispatches}))
   ;; === Filters ===
   :aoc/select-year (fn [{:keys [db]} [_ year-str]]
                      (let [year (js/parseInt year-str)
                            challenges (aoc-domain/challenges-for-year year)
                            first-challenge (first challenges)
                            challenges-options (aoc-domain/build-challenges-options year)]
                        (nav/navigate! :mateuszmazurczak.adapters.navigation.routes/aoc-specific
                                       {:path-parameters {:year (str year)
                                                          :challenge (str first-challenge)}}
                                       false)
                        {:db (-> db
                                 (assoc-in aoc-domain/*aoc-selected-year-path* year)
                                 (assoc-in aoc-domain/*aoc-selected-challenge-path* first-challenge)
                                 (assoc-in aoc-domain/*aoc-challenges-options-path*
                                           challenges-options))
                         :dispatch [:aoc/fetch-solutions year first-challenge]}))
   :aoc/select-challenge (fn [{:keys [db]} [_ challenge-str]]
                           (let [challenge (js/parseInt challenge-str)
                                 year (get-in db aoc-domain/*aoc-selected-year-path*)]
                             (nav/navigate!
                              :mateuszmazurczak.adapters.navigation.routes/aoc-specific
                              {:path-parameters {:year (str year)
                                                 :challenge (str challenge)}}
                              false)
                             {:db (assoc-in db aoc-domain/*aoc-selected-challenge-path* challenge)
                              :dispatch [:aoc/fetch-solutions year challenge]}))
   ;; === Modal state (:db handlers) ===
   :aoc/open-modal
   (fn [db [_]]
     (let [;; Check if we're in external mode (has playground-url)
           playground-url (get-in db
                                  (conj state-registry/*aoc-page-path* :modal-data :playground-url))
           ;; Get form values (only relevant in external mode)
           form-year (get-in db (conj aoc-domain/*aoc-form-path* :year))
           form-challenge (get-in db (conj aoc-domain/*aoc-form-path* :challenge))
           ;; Get current page state
           page-year (get-in db aoc-domain/*aoc-selected-year-path*)
           page-challenge (get-in db aoc-domain/*aoc-selected-challenge-path*)
           ;; In external mode, use form values (from query params).
           ;; In normal mode, ALWAYS use current page state.
           year (if playground-url form-year page-year)
           challenge (if playground-url form-challenge page-challenge)
           ;; Build challenges options for the selected year
           challenges-options (aoc-domain/build-challenges-options year)]
       (-> db
           (assoc-in aoc-domain/*aoc-modal-open-path* true)
           ;; Pre-fill year/challenge in form when opening modal
           (assoc-in (conj aoc-domain/*aoc-form-path* :year) year)
           (assoc-in (conj aoc-domain/*aoc-form-path* :challenge) challenge)
           ;; Update challenges options to match selected year
           (assoc-in (conj state-registry/*aoc-page-path* :modal-data :challenges-options)
                     challenges-options))))
   :aoc/upload-limit-reached
   (fn [db [_]]
     (let [year (get-in db aoc-domain/*aoc-selected-year-path*)
           challenge (get-in db aoc-domain/*aoc-selected-challenge-path*)]
       (notification/show-error "Upload limit reached"
                                {:description
                                 (str "You have already uploaded 5 solutions for "
                                      year
                                      " Day "
                                      challenge)})
       db))
   :aoc/close-modal
   (fn [db [_]]
     (let [year (get-in db aoc-domain/*aoc-selected-year-path*)
           challenge (get-in db aoc-domain/*aoc-selected-challenge-path*)]
       (-> db
           (assoc-in aoc-domain/*aoc-modal-open-path* false)
           (assoc-in aoc-domain/*aoc-form-path* (reset-form year challenge))
           (assoc-in (conj state-registry/*aoc-page-path* :modal-data :playground-url) nil))))
   :aoc/update-form (fn [db [_ field value]]
                      (-> db
                          (assoc-in (conj aoc-domain/*aoc-form-path* field) value)
                          (update-in aoc-domain/*aoc-form-errors-path* dissoc field)))
   ;; === Modal selectors ===
   :aoc/modal-select-year
   (fn [db [_ year-str]]
     (let [year (js/parseInt year-str)
           challenges (aoc-domain/challenges-for-year year)
           first-challenge (first challenges)
           challenges-options (aoc-domain/build-challenges-options year)]
       (-> db
           (assoc-in (conj aoc-domain/*aoc-form-path* :year) year)
           (assoc-in (conj aoc-domain/*aoc-form-path* :challenge) first-challenge)
           (assoc-in (conj state-registry/*aoc-page-path* :modal-data :challenges-options)
                     challenges-options))))
   :aoc/modal-select-challenge
   (fn [db [_ challenge-str]]
     (let [challenge (js/parseInt challenge-str)]
       (assoc-in db (conj aoc-domain/*aoc-form-path* :challenge) challenge)))
   ;; === Solution submission ===
   :aoc/submit-solution
   (fn [{:keys [db]} [_]]
     (let [form (get-in db aoc-domain/*aoc-form-path*)
           ;; In normal mode, year/challenge are nil in form - use page state
           ;; In external mode, they're in the form
           page-year (get-in db aoc-domain/*aoc-selected-year-path*)
           page-challenge (get-in db aoc-domain/*aoc-selected-challenge-path*)
           year (or (:year form) page-year)
           challenge (or (:challenge form) page-challenge)
           ;; Build payload with resolved year/challenge
           payload (-> form
                       (assoc :year year :challenge challenge))
           validation-errors (aoc-domain/validate-solution-form form)]
       (cond
         (not (aoc-cache/can-upload? year challenge))
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
   :aoc/submit-success
   (fn [{:keys [db]} [_ response]]
     (let [form (get-in db aoc-domain/*aoc-form-path*)
           page-year (get-in db aoc-domain/*aoc-selected-year-path*)
           page-challenge (get-in db aoc-domain/*aoc-selected-challenge-path*)
           ;; Resolve actual year/challenge that was submitted
           year (or (:year form) page-year)
           challenge (or (:challenge form) page-challenge)
           solution-id (:solution-id response)
           ;; After successful submission, update page selectors to match the submitted solution
           ;; This ensures consistency between what was submitted and what's displayed
           challenges-options (aoc-domain/build-challenges-options year)]
       (notification/show-success "Solution submitted successfully!")
       ;; Update URL to match submitted solution
       (nav/navigate! :mateuszmazurczak.adapters.navigation.routes/aoc-specific
                      {:path-parameters {:year (str year)
                                         :challenge (str challenge)}}
                      false)
       {:db (-> db
                (assoc-in aoc-domain/*aoc-submitting-path* false)
                (assoc-in aoc-domain/*aoc-modal-open-path* false)
                (assoc-in aoc-domain/*aoc-form-path* (reset-form year challenge))
                (assoc-in aoc-domain/*aoc-form-errors-path* nil)
                (assoc-in (conj state-registry/*aoc-page-path* :modal-data :playground-url) nil)
                ;; Update page selectors to match submitted solution
                (assoc-in aoc-domain/*aoc-selected-year-path* year)
                (assoc-in aoc-domain/*aoc-selected-challenge-path* challenge)
                (assoc-in aoc-domain/*aoc-challenges-options-path* challenges-options)
                ;; Update modal selectors to match submitted solution for next open
                (assoc-in (conj state-registry/*aoc-page-path* :modal-data :challenges-options)
                          challenges-options))
        ::cache-consent [year challenge]
        ::cache-solution-id [year challenge solution-id]
        :dispatch [:aoc/fetch-solutions year challenge]}))
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
   :aoc/fetch-solutions (fn [{:keys [db]} [_ year challenge]]
                          {:db (assoc-in db aoc-domain/*aoc-loading-path* true)
                           :http {:method :get
                                  :url "/api/aoc/solutions"
                                  :params {:year year
                                           :challenge challenge}
                                  :event/on-success
                                  [:aoc/fetch-solutions-success year challenge]
                                  :event/on-error [:aoc/fetch-solutions-failure]}})
   :aoc/fetch-solutions-success
   (fn [db [_ year challenge response]]
     (let [solutions (:solutions response)
           has-consent? (aoc-cache/has-consented? year challenge)
           user-solution-ids (set (aoc-cache/get-user-solution-ids year challenge))
           upload-count (aoc-cache/get-upload-count year challenge)
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
   :aoc/give-consent (fn [{:keys [db]} [_ year challenge]]
                       {::cache-consent [year challenge]
                        :db (assoc-in db aoc-domain/*aoc-gated-path* false)})
   ;; === Highlight ===
   :aoc/highlight-solution
   (fn [db [_ solution-id]] (assoc-in db aoc-domain/*aoc-highlighted-solution-id-path* solution-id))
   :aoc/clear-highlight (fn [db [_]]
                          (assoc-in db aoc-domain/*aoc-highlighted-solution-id-path* nil))})
