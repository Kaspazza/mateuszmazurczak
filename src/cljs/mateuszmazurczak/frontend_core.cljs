(ns mateuszmazurczak.frontend-core
  "Entry point for customer app frontend"
  (:require
   [mateuszmazurczak.frontend-system :as sys]
   [mateuszmazurczak.logging         :as log]
   [mateuszmazurczak.navigation.core]
   [mateuszmazurczak.routing         :as lm]
   [mateuszmazurczak.state           :as state]
   [reagent.dom.client               :as rdc]))

(defn render-id
  [app-id component]
  (let [el (js/document.getElementById app-id)
        root (rdc/create-root el)]
    (rdc/render root component)
    root))

(defonce *root (atom nil))

(defn ^:after-load re-render
  []
  (try (.unmount @*root)
       (sys/restart-system!)
       (reset! *root (render-id "app" [lm/main-component]))
       (when-let [logger (get @sys/system :frontend/logging)]
         (log/log! logger
                   {:id ::hot-reload-complete
                    :level :info
                    :msg "Hot reload completed successfully"}))
       (catch :default e
         (log/safe-error! (get @sys/system :frontend/logging)
                          {:error e
                           :id ::hot-reload-failed
                           :data {:stage "hot-reload"}})
         (throw e))))

(defn mount-root
  []
  (try (reset! *root (render-id "app" [lm/main-component]))
       (catch :default e
         (log/safe-error! (get @sys/system :frontend/logging)
                          {:error e
                           :id ::mount-error
                           :data {:component "main-component"}})
         (throw (ex-info "Mount error" {:error e})))))

(defn handle-init-failure!
  "Handle system initialization failure by showing error UI.
   
   This function:
   1. Logs the error (falls back to console if logger unavailable)
   2. Initializes minimal app-db with error state
   3. Mounts the React root to show error screen
   
   Arguments:
   - error: The error that occurred during system initialization"
  [error]
  (log/safe-error! (get @sys/system :frontend/logging)
                   {:error error
                    :id ::app-init-failed
                    :data {:stage "initialization"}})
  (try (state/init-app-db! {:current-route {:panel-id :panels/system-error}
                            :system-error error})
       ;; Mount the UI to show the error screen
       (mount-root)
       (catch :default mount-error
         ;; In case even mounting fails
         (log/safe-error! (get @sys/system :frontend/logging)
                          {:error mount-error
                           :id ::mount-error-during-init-failure
                           :data {:original-error error}}))))

(defn ^:export init!
  []
  (try (sys/start-system!)
       (mount-root)
       (catch :default e (handle-init-failure! e))))
