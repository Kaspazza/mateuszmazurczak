(ns mateuszmazurczak.state.adapters.reframe
  "Re-frame adapter for application state management."
  (:require
   [re-frame.core :as rf]))

;; Event registrations
(rf/reg-event-db
 ::initialize-db
 (fn [_db [_ initial-state]]
   initial-state))

(rf/reg-event-db
 ::system-failed
 (fn [db [_ error]]
   (assoc db
          :current-route {:panel-id :panels/system-error}
          :system-error error)))

;; Public adapter API

(defn init-db!
  "Initialize the app-db with the given initial state."
  [initial-state]
  (rf/clear-subscription-cache!)
  (rf/dispatch-sync [::initialize-db initial-state])
  initial-state)

(defn reset-db!
  "Reset the app-db, clearing all cached subscriptions."
  []
  (rf/clear-subscription-cache!))

(defn dispatch-system-error!
  "Dispatch a system error event to update state."
  [error]
  (rf/dispatch-sync [::system-failed error]))
