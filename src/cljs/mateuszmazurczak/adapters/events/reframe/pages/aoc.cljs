(ns mateuszmazurczak.adapters.events.reframe.pages.aoc
  "Re-frame adapter for AoC page events.
   
   Implements AoC page events from events/registry.cljc as a handler map.
   This is INTERNAL adapter code - UI components should dispatch via events/dispatch!"
  (:require
   [mateuszmazurczak.domain.pages.aoc      :as aoc-domain]
   [mateuszmazurczak.domain.state.registry :as state-registry]
   [mateuszmazurczak.ports.logging         :as log]))

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
     (let [existing-data (get-in db state-registry/*aoc-page-path*)
           initial-data (aoc-domain/build-initial-page-data)
           ;; Always use initial-data for first load, or merge to ensure options are present
           page-data (if (or (nil? existing-data) (empty? (:years-options existing-data)))
                       initial-data
                       existing-data)
           {:keys [selected-year selected-challenge selected-part]} page-data]
       {:db (assoc-in db state-registry/*aoc-page-path* page-data)
        :dispatch [:aoc/fetch-solutions selected-year selected-challenge selected-part]}))
   ;; === Filters ===
   :aoc/select-year
   (fn [{:keys [db]} [_ year-str]]
     (let [year (js/parseInt year-str)
           challenges (aoc-domain/challenges-for-year year)
           first-challenge (first challenges)
           challenges-options (aoc-domain/build-challenges-options year)
           part (get-in db (conj state-registry/*aoc-page-path* :selected-part))]
       {:db (-> db
                (assoc-in (conj state-registry/*aoc-page-path* :selected-year) year)
                (assoc-in (conj state-registry/*aoc-page-path* :selected-challenge) first-challenge)
                (assoc-in (conj state-registry/*aoc-page-path* :challenges-options)
                          challenges-options))
        :dispatch [:aoc/fetch-solutions year first-challenge part]}))
   :aoc/select-challenge
   (fn [{:keys [db]} [_ challenge-str]]
     (let [challenge (js/parseInt challenge-str)
           year (get-in db (conj state-registry/*aoc-page-path* :selected-year))
           part (get-in db (conj state-registry/*aoc-page-path* :selected-part))]
       {:db (assoc-in db (conj state-registry/*aoc-page-path* :selected-challenge) challenge)
        :dispatch [:aoc/fetch-solutions year challenge part]}))
   :aoc/select-part (fn [{:keys [db]} [_ part-str]]
                      (let [part (js/parseInt part-str)
                            year (get-in db (conj state-registry/*aoc-page-path* :selected-year))
                            challenge
                            (get-in db (conj state-registry/*aoc-page-path* :selected-challenge))]
                        {:db (assoc-in db (conj state-registry/*aoc-page-path* :selected-part) part)
                         :dispatch [:aoc/fetch-solutions year challenge part]}))
   ;; === Modal state (:db handlers) ===
   :aoc/open-modal (fn [db [_]]
                     (assoc-in db (conj state-registry/*aoc-page-path* :modal-open?) true))
   :aoc/close-modal (fn [db [_]]
                      (-> db
                          (assoc-in (conj state-registry/*aoc-page-path* :modal-open?) false)
                          (assoc-in (conj state-registry/*aoc-page-path* :form) (reset-form))))
   :aoc/update-form (fn [db [_ field value]]
                      (assoc-in db (conj state-registry/*aoc-page-path* :form field) value))
   ;; === Solution submission ===
   :aoc/submit-solution
   (fn [{:keys [db]} [_]]
     (let [form (get-in db (conj state-registry/*aoc-page-path* :form))
           year (get-in db (conj state-registry/*aoc-page-path* :selected-year))
           challenge (get-in db (conj state-registry/*aoc-page-path* :selected-challenge))
           part (get-in db (conj state-registry/*aoc-page-path* :selected-part))
           payload (assoc form :year year :challenge challenge :part part)]
       (if (aoc-domain/valid-solution-form? form)
         {:db (assoc-in db (conj state-registry/*aoc-page-path* :submitting?) true)
          :http {:method :post
                 :url "/api/aoc/solutions"
                 :params payload
                 :event/on-success [:aoc/submit-success]
                 :event/on-error [:aoc/submit-failure]}}
         {:db db})))
   :aoc/submit-success
   (fn [{:keys [db]} [_]]
     (let [year (get-in db (conj state-registry/*aoc-page-path* :selected-year))
           challenge (get-in db (conj state-registry/*aoc-page-path* :selected-challenge))
           part (get-in db (conj state-registry/*aoc-page-path* :selected-part))]
       {:db (-> db
                (assoc-in (conj state-registry/*aoc-page-path* :submitting?) false)
                (assoc-in (conj state-registry/*aoc-page-path* :modal-open?) false)
                (assoc-in (conj state-registry/*aoc-page-path* :form) (reset-form)))
        :dispatch [:aoc/fetch-solutions year challenge part]}))
   :aoc/submit-failure
   (fn [{:keys [db]} [_ error]]
     (let [logger (get-in db state-registry/*logger-path*)]
       (log/error! logger
                   {:error (ex-info "Failed to submit solution"
                                    {:type ::submit-solution-failed
                                     :error error})})
       {:db (assoc-in db (conj state-registry/*aoc-page-path* :submitting?) false)}))
   ;; === Solutions fetching ===
   :aoc/fetch-solutions (fn [{:keys [db]} [_ year challenge part]]
                          {:db (assoc-in db (conj state-registry/*aoc-page-path* :loading?) true)
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
           (assoc-in (conj state-registry/*aoc-page-path* :solution-ids) ids)
           (assoc-in (conj state-registry/*aoc-page-path* :loading?) false))))
   :aoc/fetch-solutions-failure
   (fn [{:keys [db]} [_ error]]
     (let [logger (get-in db state-registry/*logger-path*)]
       (log/error! logger
                   {:error (ex-info "Failed to fetch solutions"
                                    {:type ::fetch-solutions-failed
                                     :error error})})
       {:db (-> db
                (assoc-in (conj state-registry/*aoc-page-path* :solution-ids) [])
                (assoc-in (conj state-registry/*aoc-page-path* :loading?) false))}))})
