(ns mateuszmazurczak.state
  "Application state management port (Application Service layer).
   
   Provides a clean API for application state initialization and management.
   Uses the re-frame adapter as the default implementation."
  (:require
   [mateuszmazurczak.frontend-i18n          :as fi18n]
   [mateuszmazurczak.pages.home.data        :as home-data]
   [mateuszmazurczak.state.adapters.reframe :as reframe-adapter]))

(defn initial-state
  "Build the initial app-db state.
   
   Arguments:
   - translator: The i18n translator instance
   - logger: The logging instance
   
   Returns a map with the initial application state structure."
  [translator logger]
  {:name "mateuszmazurczak"
   :current-route {:panel-id :panels/pending}
   :lang (fi18n/language-strategy)
   :translator translator
   :logger logger
   :pages {:home (home-data/initial-home-data)}})

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
