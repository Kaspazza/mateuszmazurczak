(ns mateuszmazurczak.adapters.events.reframe.core
  "Re-frame event adapter - implements events/registry.cljs contract."
  (:require
   [mateuszmazurczak.adapters.events.reframe.admin          :as admin-events]
   [mateuszmazurczak.adapters.events.reframe.cache          :as cache-events]
   [mateuszmazurczak.adapters.events.reframe.http           :as http-events]
   [mateuszmazurczak.adapters.events.reframe.i18n           :as i18n-events]
   [mateuszmazurczak.adapters.events.reframe.navigation     :as nav-events]
   [mateuszmazurczak.adapters.events.reframe.pages.aoc      :as aoc-events]
   [mateuszmazurczak.adapters.events.reframe.pages.home     :as home-events]
   [mateuszmazurczak.adapters.events.reframe.pages.qr-codes :as qr-codes-events]
   [mateuszmazurczak.adapters.events.reframe.theme          :as theme-events]
   [re-frame.core                                           :as rf]))

(def register-fns
  "Map of handler-type -> registration function.
   
   These are the ONLY re-frame-specific functions in this file.
   Everything else is domain/orchestration logic.
   
   Exported as data for the system layer to use when wiring the adapter."
  {:db (fn [event-id handler-fn] (rf/reg-event-db event-id handler-fn))
   :fx (fn [event-id handler-fn] (rf/reg-event-fx event-id handler-fn))})

(def handlers
  "All event handlers from sub-adapters merged into a single map.
   
   Exported as data for the system layer to wire via events/wire!.
   The system layer should NOT call this adapter's init! for wiring -
   it should call events/wire! directly with this handlers map."
  (merge home-events/handlers
         nav-events/handlers
         i18n-events/handlers
         aoc-events/handlers
         theme-events/handlers
         admin-events/handlers
         qr-codes-events/handlers))

(defn init!
  "Initialize adapter-specific setup (effects and subscriptions).
   
   This is ONLY for re-frame-specific initialization that must happen
   before handlers can be used (effects, subscriptions, etc.).
   
   Returns nil as it only performs side-effects."
  []
  (http-events/init-effects!)
  (nav-events/init!)
  (i18n-events/init!)
  (cache-events/init!)
  (theme-events/init!)
  nil)

(defn get-dispatch-fn
  "Returns the re-frame dispatch function.
   
   This is called by the events port (events.cljs) during system initialization
   to wire up the dispatch mechanism.
   
   Returns: re-frame dispatch function"
  []
  rf/dispatch)
