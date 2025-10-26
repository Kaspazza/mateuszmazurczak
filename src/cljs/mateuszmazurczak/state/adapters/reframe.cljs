(ns mateuszmazurczak.state.adapters.reframe
  "Re-frame adapter for application state management."
  (:require
   [re-frame.core :as rf]))

(rf/reg-event-db ::initialize-db (fn [_db [_ initial-state]] initial-state))

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

(defn get-dispatch-fn
  "Returns the re-frame dispatch function.
   
   This is used by the state port to wire up the dispatch mechanism
   during system initialization."
  []
  rf/dispatch)
