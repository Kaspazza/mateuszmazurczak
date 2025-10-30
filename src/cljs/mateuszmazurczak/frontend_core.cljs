(ns mateuszmazurczak.frontend-core
  "Entry point for customer app frontend"
  (:require
   [mateuszmazurczak.system.core      :as sys]
   [mateuszmazurczak.ports.logging    :as log]
   [mateuszmazurczak.application.router :as lm]
   [mateuszmazurczak.ui.errors        :as mm-ui-errors]
   [reagent.dom.client                :as rdc]
   [reagent.dom.server                :as rds]))

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
   
   Bypasses the entire React/state system and uses Reagent server-side rendering
   to convert our error page hiccup to HTML string. This ensures error screen
   shows even if the state system is completely broken.
   
   Arguments:
   - error: The error that occurred during system initialization"
  [error]
  (log/safe-error! (get @sys/system :frontend/logging)
                   {:error error
                    :id ::app-init-failed
                    :data {:stage "initialization"}})
  (when-let [app-el (js/document.getElementById "app")]
    (set!
     (.-innerHTML app-el)
     (rds/render-to-static-markup
      [mm-ui-errors/internal-error
       {:title "System Initialization Failed"
        :description
        "We encountered an error while starting the application. Please refresh the page or contact support if the problem persists."
        :back-home-text "Refresh Page"
        :back-link "javascript:window.location.reload()"}]))))

(defn ^:export init!
  []
  (try (sys/start-system!)
       (mount-root)
       (catch :default e (handle-init-failure! e))))
