(ns mateuszmazurczak.state
  "Application state management port (Application Service layer).
   
   Provides a clean API for application state operations. Uses the re-frame
   adapter as the default implementation. This is a hybrid port/adapter pattern:
   the port provides the API and directly uses one adapter, making it easy to
   change if requirements evolve.
   
   Separate from the system composition layer (frontend_system.cljs)."
  (:require
   [mateuszmazurczak.state.adapters.reframe :as reframe-adapter]))

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

(defn handle-system-error!
  "Handle a system initialization error by updating state to show error UI.
   
   Arguments:
   - error: The error that occurred during system initialization"
  [error]
  (reframe-adapter/dispatch-system-error! error))
