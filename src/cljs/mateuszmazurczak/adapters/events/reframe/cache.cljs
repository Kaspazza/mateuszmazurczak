(ns mateuszmazurczak.adapters.events.reframe.cache
  "Re-frame adapter for cache effects.
   
   Provides effects for persisting state to localStorage."
  (:require
   [mateuszmazurczak.ports.cache :as cache]
   [re-frame.core                :as rf]))

(rf/reg-fx ::persist-state
           ;; "Effect: Persist changed domains from app-db to localStorage.
           ;;  Uses editscript to detect changes and saves only modified domains.
           ;;  Arguments:
           ;;  - db: The app-db value to persist"
           (fn [db] (cache/persist-changed! db)))

(defn init!
  "Initialize cache effects.
   
   Registers re-frame effects for state persistence."
  []
  ;; Effects registered at namespace load time above
  nil)
