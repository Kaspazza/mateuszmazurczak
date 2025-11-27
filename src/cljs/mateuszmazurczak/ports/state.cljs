(ns mateuszmazurczak.ports.state
  "Application state management port (Application Service layer).
   
   Provides an API for application state initialization, management, and querying.
   Uses the re-frame adapter as the default implementation.
   
   This port handles:
   - State initialization (initial-state, init-app-db!)
   - State queries (watch) - reactive reads from state
   - State lifecycle (reset-app-db!)"
  (:require
   [clojure.set                             :as set]
   [mateuszmazurczak.adapters.state.reframe :as reframe-adapter]
   [mateuszmazurczak.domain.pages.aoc       :as aoc-domain]
   [mateuszmazurczak.domain.pages.home      :as home-domain]
   [mateuszmazurczak.domain.state.registry  :as state-registry]
   [mateuszmazurczak.domain.theme.schema    :as theme-schema]
   [mateuszmazurczak.system.config          :as config]))


;; =============================================================================
;; Watch (Reactive State Queries)
;; =============================================================================

(defonce
  ^{:private true
    :doc
    "The watch function provided by the wired adapter. 
     Should be a function that takes a watch vector (e.g., [:nav/current-route]) and returns a reactive value (e.g. atom)."}
  watch-fn
  (atom nil))

(defn- validate-completeness!
  "Ensures adapter implements ALL watch from registry.
   
   Throws if any watch are missing."
  [registry-watch adapter-watch]
  (let [required (set (keys registry-watch))
        missing (set/difference required adapter-watch)]
    (when (seq missing)
      (throw (ex-info "Adapter missing required watch keys"
                      {:missing-watch missing
                       :required-watch required
                       :adapter-watch adapter-watch})))))

(defn- validate-correctness!
  "Ensures adapter doesn't implement watch not in registry.
   
   Throws if any unknown watch are found."
  [registry-watch adapter-watch]
  (let [required (set (keys registry-watch))
        unknown (set/difference adapter-watch required)]
    (when (seq unknown)
      (throw (ex-info "Adapter implements unknown watch"
                      {:unknown-watch unknown
                       :registry-watch required
                       :adapter-watch adapter-watch})))))

(def watch-reg
  "Registry of all application watch.
   
   This is the single source of truth for what can be queried from the state."
  state-registry/watch-reg)

(defn set-watch-fn!
  "Set the watch function from the adapter.
   
   Arguments:
   - f: Function that takes a watch vector and returns a reactive value
   
   The function signature depends on the adapter (e.g., re-frame/subscribe returns ratom)."
  [f]
  (reset! watch-fn f))

(defn wire-watch!
  "Wire the watch adapter to this port.
   
   Validates that adapter implements exactly the watch defined in registry:
   - Completeness: all registry watch are implemented
   - Correctness: no unknown watch are implemented
   
   Arguments:
   - adapter-watch: Set of watch-ids that the adapter implements
   
   Throws if validation fails."
  [adapter-watch-keys watch-fn]
  (validate-completeness! watch-reg adapter-watch-keys)
  (validate-correctness! watch-reg adapter-watch-keys)
  (set-watch-fn! watch-fn))

(defn watch
  "Query reactive state value.
   
   Takes a watch vector (e.g., [:nav/current-route]) and returns a reactive value
   that updates when the underlying state changes.
   
   The return type is adapter-specific:
   - re-frame: returns ratom (deref with @)
   - Other adapters: might return signals, atoms, etc.
   
   Arguments:
   - query-v: Watch vector where first element is watch-id keyword
   
   Returns: Reactive value (adapter-specific type)
   
   Throws:
   - If watch function not initialized
   - If watch-id not in registry (in development mode only)
   
   Example:
   ```clojure
   @(state/watch [:nav/current-route])
   @(state/watch [:nav/active-route? :home])
   ```"
  [query-v]
  (when-not @watch-fn
    (throw (ex-info "Watch function not initialized. Call wire-watch! and set-watch-fn! first."
                    {:query query-v})))
  (when (config/development?)
    (let [watch-id (first query-v)]
      (when-not (contains? watch-reg watch-id)
        (throw (ex-info "Unknown watch-id. Not found in registry."
                        {:watch-id watch-id
                         :query query-v
                         :available-watch (keys watch-reg)})))))
  (@watch-fn query-v))

;; =============================================================================
;; App db
;; =============================================================================

(defn initial-state
  "Build the initial app-db state.
   
   Arguments:
   - translator: The i18n translator instance
   - logger: The logging instance
   
   Returns a map with the initial application state structure."
  [translator logger lang-strategy]
  {:name "mateuszmazurczak"
   :current-route {:page-id :pages/pending}
   :lang lang-strategy
   :theme theme-schema/default-theme
   :translator translator
   :logger logger
   :pages {:home (home-domain/initial-home-data)
           :aoc (aoc-domain/initial-aoc-data)}})

(defn init-app-db!
  "Initialize the application state with the given initial state.
   
   Must be called after the system is initialized but before rendering.
   
   Arguments:
   - initial-state: The initial state map from the system's :frontend/state component"
  [initial-state]
  (reframe-adapter/init-db! initial-state))

(defn reset-app-db!
  "Reset the application state, clearing all cached subscriptions.
   
   Used during hot reloads and system restarts."
  []
  (reframe-adapter/reset-db!))

