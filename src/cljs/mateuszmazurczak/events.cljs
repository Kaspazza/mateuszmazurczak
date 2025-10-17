(ns mateuszmazurczak.events
  "Event dispatching API    
   Frontend is event driven. All side-effects happen through dispatching and realizing events. 
   The complete list of all events is defined in `mateuszmazurczak.events.registry`
   
   This namespace is the port/application service layer for event wiring:
   - Validates adapter completeness (all required events implemented)
   - Validates adapter correctness (no unknown events)
   - Orchestrates handoff to adapter-specific registration"
  (:refer-clojure :exclude [dispatch-fn])
  (:require
   [clojure.set                      :as set]
   [clojure.walk                     :as walk]
   [mateuszmazurczak.events.registry :as registry]))

;; For system layer, when stable won't be needed as will be replaced by protocol
(defonce ^:private dispatch-fn (atom nil))

(defn set-dispatch!
  "Set the dispatch function - called by system during initialization.
   
   Arguments:
   - dispatch: The dispatch function to use for sending events"
  [dispatch]
  (reset! dispatch-fn dispatch))

(defn get-dispatch-fn
  "Get the current dispatch function.
   
   Used by system initialization to wire up the dispatch mechanism."
  []
  @dispatch-fn)

;; Application layer API
(defn dispatch!
  "Dispatch an event to the application state.
   
   This is the primary way for UI components to send events to update state.
   
   Arguments:
   - event-vector: A vector containing the event keyword and any parameters,
                   e.g., [:nav/navigate :route-name {:param value}]
   
   Example:
   (dispatch! [:nav/navigate ::routes/home])"
  [event-vector]
  (when-not @dispatch-fn
    (throw (ex-info
            "Dispatch function not initialized. Ensure the system is started."
            {:type :events/dispatch-not-initialized
             :event event-vector})))
  (@dispatch-fn event-vector))

(defn dispatch-tree
  "Convert [:dispatch event-vector] markers into actual dispatch functions.
   
   Example transformation:
   {:on-click [:dispatch [:nav/navigate ::routes/home]]}
   =>
   {:on-click #(mateuszmazurczak.events/dispatch! [:nav/navigate ::routes/home])}
   "
  [coll]
  (walk/prewalk
   #(if (and (vector? %) (= :dispatch (first %)) (vector? (second %)))
      (fn [] (dispatch! (second %)))
      %)
   coll))

;; Adapter wiring (port/application service layer)

(defn- validate-adapter-completeness
  "Validate that all required events from registry are implemented.
   
   Throws if required events are missing from adapter map.
   
   Arguments:
   - adapter-map: map of {event-id handler-fn}
   
   Throws:
   - ex-info if required events are missing"
  [adapter-map]
  (let [required-event-ids (set (keys registry/events))
        implemented-event-ids (set (keys adapter-map))
        missing-event-ids (set/difference required-event-ids
                                          implemented-event-ids)]
    (when (seq missing-event-ids)
      (throw
       (ex-info
        "Required events not implemented by adapter"
        {:type :wiring/missing-required-events
         :missing missing-event-ids
         :required required-event-ids
         :implemented implemented-event-ids
         :hint
         "Add these events to your adapter map or use 'bb generate-adapter-stub' to generate code"})))))

(defn- validate-no-unknown-events
  "Validate that adapter doesn't implement events not in registry.
   
   This prevents accidental registration of unregistered events.
   
   Arguments:
   - adapter-map: map of {event-id handler-fn}
   
   Throws:
   - ex-info if unknown events are found"
  [adapter-map]
  (let [registry-event-ids (set (keys registry/events))
        adapter-event-ids (set (keys adapter-map))
        unknown-event-ids (set/difference adapter-event-ids registry-event-ids)]
    (when (seq unknown-event-ids)
      (throw
       (ex-info
        "Adapter implements events not in registry"
        {:type :wiring/unknown-events
         :unknown unknown-event-ids
         :hint
         "Add these events to events/registry.cljs or remove from adapter"})))))

(defn- validate-register-fns
  "Validate that register-fns map is complete and correct.
   
   Ensures:
   1. All required handler types from registry are supported
   2. All values are callable (functions)
   
   Arguments:
   - register-fns: map of {handler-type register-fn!}
   
   Throws:
   - ex-info if validation fails (fail-fast at initialization)"
  [register-fns]
  (let [required-types (set (map :handler-type (vals registry/events)))
        provided-types (set (keys register-fns))
        missing-types (set/difference required-types provided-types)]
    (when (seq missing-types)
      (throw
       (ex-info
        "Register-fns missing required handler types"
        {:type :wiring/incomplete-register-fns
         :missing-types missing-types
         :required-types required-types
         :provided-types provided-types
         :hint
         "Adapter must provide register-fns for all handler types used in registry"})))
    (doseq [[handler-type register-fn] register-fns]
      (when-not (fn? register-fn)
        (throw (ex-info "Register function is not callable"
                        {:type :wiring/invalid-register-fn
                         :handler-type handler-type
                         :received (type register-fn)
                         :expected "function"}))))))

(defn wire!
  "Wire an adapter by validating and registering all handlers.
   
   This is the main entry point for integrating a state management adapter.
   Called by the system layer (frontend_system.cljs), NOT by adapters themselves.
   
   Process:
   1. Validate register-fns (all required types present, all callable)
   2. Validate adapter completeness (all required events)
   3. Validate no unknown events
   4. For each event, look up its handler-type in registry
   5. Call the adapter's handler-type-specific registration function
   
   Arguments:
   - handlers: map of {event-id handler-fn} from the adapter
   - register-fns: map of {handler-type register-fn!}
                   where handler-type is :db or :fx
                   and register-fn! is (fn [event-id handler-fn])
   
   Example:
   (wire! adapter-handlers {:db rf/reg-event-db :fx rf/reg-event-fx})
   
   Returns: nil (side-effectful)"
  [handlers register-fns]
  (validate-register-fns register-fns)
  (validate-adapter-completeness handlers)
  (validate-no-unknown-events handlers)
  (doseq [[event-id handler-fn] handlers]
    (let [spec (get registry/events event-id)
          handler-type (:handler-type spec)
          register-fn (get register-fns handler-type)]
      (register-fn event-id handler-fn))))
