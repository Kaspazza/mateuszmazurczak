(ns mateuszmazurczak.adapters.events.reframe.admin
  "Re-frame adapter for admin events."
  (:require
   [mateuszmazurczak.application.aoc.cache-service :as aoc-cache]
   [mateuszmazurczak.domain.pages.admin            :as admin-domain]
   [mateuszmazurczak.domain.state.registry         :as state-registry]
   [mateuszmazurczak.ports.logging                 :as log]
   [mateuszmazurczak.ui.components.notification    :as notification]
   [re-frame.core                                  :as rf]))

;; =============================================================================
;; Internal Effects
;; =============================================================================

(rf/reg-fx ::cache-admin-key (fn [admin-key] (aoc-cache/set-admin-key! admin-key)))

(rf/reg-fx ::clear-admin-key (fn [_] (aoc-cache/clear-admin-key!)))

;; =============================================================================
;; Event Handlers
;; =============================================================================

(def handlers
  {:admin/on-route-enter
   (fn [{:keys [db]} [_]]
     (let [initial-data (admin-domain/initial-admin-data)]
       {:db (assoc-in db admin-domain/*admin-page-path* (assoc initial-data :loading? false))
        :dispatch [:admin/check-status]}))
   :admin/update-form (fn [{:keys [db]} [_ field value]]
                        (let [form-path (conj admin-domain/*admin-page-path* :form field)]
                          {:db (assoc-in db form-path value)}))
   :admin/login
   (fn [{:keys [db]} [_]]
     (let [admin-key (get-in db (conj admin-domain/*admin-page-path* :form :admin-key))
           {:keys [valid?]} (admin-domain/validate-admin-login admin-key)]
       (if-not valid?
         (do (notification/show-error "Invalid admin key"
                                      {:description "Key must be at least 32 characters"})
             {:db db})
         (do (notification/show-success "Admin mode activated")
             {:db (-> db
                      (assoc-in state-registry/*admin-logged-in-path* true)
                      (assoc-in (conj admin-domain/*admin-page-path* :form :admin-key) ""))
              ::cache-admin-key admin-key}))))
   :admin/logout (fn [{:keys [db]} _]
                   (notification/show-success "Logged out from admin mode")
                   {:db (assoc-in db state-registry/*admin-logged-in-path* false)
                    ::clear-admin-key true})
   :admin/check-status (fn [{:keys [db]} [_]]
                         (let [has-key? (aoc-cache/is-admin?)]
                           {:db (assoc-in db state-registry/*admin-logged-in-path* has-key?)}))
   :admin/delete-solution (fn [_ [_ solution-id]]
                            {:http {:method :delete
                                    :url (str "/api/aoc/solutions/" solution-id)
                                    :params {}
                                    :event/on-success [:admin/delete-solution-success solution-id]
                                    :event/on-error [:admin/delete-solution-failure]}})
   :admin/delete-solution-success (fn [{:keys [db]} [_ _solution-id _response]]
                                    (let [year (get-in db [:pages :aoc :selected-year])
                                          challenge (get-in db [:pages :aoc :selected-challenge])
                                          part (get-in db [:pages :aoc :selected-part])]
                                      (notification/show-success "Solution deleted successfully")
                                      {:dispatch [:aoc/fetch-solutions year challenge part]}))
   :admin/delete-solution-failure (fn [{:keys [db]} [_ error]]
                                    (let [logger (get-in db state-registry/*logger-path*)
                                          error-message (or (get-in error [:response :error])
                                                            (get error :message)
                                                            "Failed to delete solution")]
                                      (log/error! logger
                                                  {:error (ex-info "Failed to delete solution"
                                                                   {:type ::delete-solution-failed
                                                                    :error error})})
                                      (notification/show-error error-message)
                                      {:db db}))})
