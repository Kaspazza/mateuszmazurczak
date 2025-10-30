(ns mateuszmazurczak.adapters.events.reframe.pages.home
  "Re-frame adapter for home page events.
   
   Implements home page events from events/registry.cljs as a handler map.
   This is INTERNAL adapter code - UI components should dispatch via events/dispatch!"
  (:require
   [mateuszmazurczak.application.pages.home.data :as home-data]
   [mateuszmazurczak.utils.map                   :as utils-map]))

;; =============================================================================
;; Event Handlers (implementing events/registry.cljs contract)
;; =============================================================================

(def handlers
  "Home page event handlers exported as data.
   
   Each handler is a pure function that will be registered with re-frame
   by the wiring namespace. The registry metadata determines whether it's
   registered as :db or :fx handler."
  {:home/refresh (fn [db [_]]
                   (update-in db
                              [:pages :home]
                              utils-map/deep-merge
                              (home-data/build-home-page-data)
                              {:loading? false}))
   :home/on-route-enter (fn [{:keys [_db]} [_]] {:dispatch [:home/refresh]})})
