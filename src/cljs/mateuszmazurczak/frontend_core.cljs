(ns mateuszmazurczak.frontend-core
  "Entry point for customer app frontend"
  (:require
   [mateuszmazurczak.frontend-system :as sys]
   [mateuszmazurczak.logging         :as log]
   [mateuszmazurczak.main            :as lm]
   [mateuszmazurczak.navigation.core]
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
       ;; Restart system on hot reload
       (sys/restart-system!)
       (reset! *root (render-id "app" [lm/main-component]))
       (when-let [logger (get @sys/system :frontend/logging)]
         (log/log! logger
                   {:id ::hot-reload-complete
                    :level :info
                    :msg "Hot reload completed successfully"}))
       (catch :default e
         (when-let [logger (get @sys/system :frontend/logging)]
           (log/error! logger
                       {:error e
                        :id ::hot-reload-failed
                        :data {:stage "hot-reload"}}))
         (throw e))))

(defn mount-root
  []
  (try (reset! *root (render-id "app" [lm/main-component]))
       (catch :default e
         (when-let [logger (get @sys/system :frontend/logging)]
           (log/error! logger
                       {:error e
                        :id ::mount-error
                        :data {:component "main-component"}}))
         (throw (ex-info "Mount error" {:error e})))))

(defn ^:export init!
  []
  (try (sys/start-system!)
       (mount-root)
       (catch :default e
         (when-let [logger (get @sys/system :frontend/logging)]
           (log/error! logger
                       {:error e
                        :id ::app-init-failed
                        :data {:stage "initialization"}}))
         (js/console.error "System initialization failed:" e))))
