(ns mateuszmazurczak.adapters.events.reframe.pages.aoc
  "Re-frame adapter for AoC page events.
   
   Implements AoC page events from events/registry.cljc as a handler map.
   This is INTERNAL adapter code - UI components should dispatch via events/dispatch!"
  (:require
   [mateuszmazurczak.domain.pages.aoc           :as aoc-domain]
   [mateuszmazurczak.domain.state.registry      :as state-registry]
   [mateuszmazurczak.ports.logging              :as log]
   [mateuszmazurczak.ui.components.notification :as notification]))

(defn- reset-form
  "Reset form to initial state."
  []
  {:author-name ""
   :github-profile ""
   :content-type :code-snippet
   :content ""})

(def handlers
  "AoC page event handlers exported as data.
   
   Each handler is a pure function that will be registered with re-frame
   by the wiring namespace. The registry metadata determines whether it's
   registered as :db or :fx handler.
   
   :fx handlers receive {:keys [db]} coeffects and return effects map
   :db handlers receive db directly and return updated db"
  {:aoc/on-route-enter
   (fn [{:keys [db]} [_]]
     (let [existing-data (get-in db aoc-domain/*aoc-page-path*)
           initial-data (aoc-domain/build-initial-page-data)
           page-data (if (or (nil? existing-data) (empty? (:years-options existing-data)))
                       initial-data
                       existing-data)
           {:keys [selected-year selected-challenge selected-part]} page-data]
       {:db (assoc-in db aoc-domain/*aoc-page-path* page-data)
        :dispatch [:aoc/fetch-solutions selected-year selected-challenge selected-part]}))
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
   :aoc/open-modal (fn [db [_]] (assoc-in db aoc-domain/*aoc-modal-open-path* true))
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
       (if validation-errors
         (do (notification/show-error "Please fix the form errors"
                                      {:description "Some required fields are missing"})
             {:db (assoc-in db aoc-domain/*aoc-form-errors-path* validation-errors)})
         {:db (-> db
                  (assoc-in aoc-domain/*aoc-submitting-path* true)
                  (assoc-in aoc-domain/*aoc-form-errors-path* nil))
          :http {:method :post
                 :url "/api/aoc/solutions"
                 :params payload
                 :event/on-success [:aoc/submit-success]
                 :event/on-error [:aoc/submit-failure]}})))
   :aoc/submit-success (fn [{:keys [db]} [_]]
                         (let [year (get-in db aoc-domain/*aoc-selected-year-path*)
                               challenge (get-in db aoc-domain/*aoc-selected-challenge-path*)
                               part (get-in db aoc-domain/*aoc-selected-part-path*)]
                           (notification/show-success "Solution submitted successfully!")
                           {:db (-> db
                                    (assoc-in aoc-domain/*aoc-submitting-path* false)
                                    (assoc-in aoc-domain/*aoc-modal-open-path* false)
                                    (assoc-in aoc-domain/*aoc-form-path* (reset-form))
                                    (assoc-in aoc-domain/*aoc-form-errors-path* nil))
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
                                  :event/on-success [:aoc/fetch-solutions-success]
                                  :event/on-error [:aoc/fetch-solutions-failure]}})
   :aoc/fetch-solutions-success
   (fn [db [_ solutions]]
     (let [normalized-solutions (mapv (fn [solution] (update solution :content-type keyword))
                                      solutions)
           {:keys [entities ids]} (aoc-domain/normalize-solutions normalized-solutions)]
       (-> db
           (assoc-in state-registry/*aoc-solutions-path* entities)
           (assoc-in aoc-domain/*aoc-solution-ids-path* ids)
           (assoc-in aoc-domain/*aoc-loading-path* false))))
   :aoc/fetch-solutions-failure (fn [{:keys [db]} [_ error]]
                                  (let [logger (get-in db state-registry/*logger-path*)]
                                    (log/error! logger
                                                {:error (ex-info "Failed to fetch solutions"
                                                                 {:type ::fetch-solutions-failed
                                                                  :error error})})
                                    {:db (-> db
                                             (assoc-in aoc-domain/*aoc-solution-ids-path* [])
                                             (assoc-in aoc-domain/*aoc-loading-path* false))}))})
