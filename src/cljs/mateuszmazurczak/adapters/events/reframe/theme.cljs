(ns mateuszmazurczak.adapters.events.reframe.theme
  "Re-frame event handlers for theme management."
  (:require
   [mateuszmazurczak.domain.state.registry :as state-registry]
   [mateuszmazurczak.ports.logging         :as log]
   [mateuszmazurczak.ports.theme           :as theme]
   [re-frame.core                          :as rf]))

(rf/reg-fx ::apply-theme (fn [{:keys [theme]}] (theme/apply-theme! theme)))

(def handlers
  "Theme event handlers exported as data.
   
   Each handler is a pure function that will be registered with re-frame
   by the wiring namespace. The registry metadata determines whether it's
   registered as :fx handler.
   
   Note: Theme persistence is handled by the cache system (see domain.cache.registry).
   These handlers only manage theme in app-db and apply DOM changes."
  {:theme/set (fn [{:keys [db]} [_ theme]]
                (let [logger (get-in db state-registry/*logger-path*)]
                  (log/log! logger
                            {:id ::theme-changed
                             :level :info
                             :msg "Theme changed"
                             :data {:theme theme}})
                  {:db (assoc-in db state-registry/*theme-path* theme)
                   ::apply-theme {:theme theme}}))
   :theme/toggle (fn [{:keys [db]} _]
                   (let [logger (get-in db state-registry/*logger-path*)
                         current-theme (get-in db state-registry/*theme-path*)
                         new-theme (if (= current-theme :light) :dark :light)]
                     (log/log! logger
                               {:id ::theme-toggled
                                :level :info
                                :msg "Theme toggled"
                                :data {:from current-theme
                                       :to new-theme}})
                     {:db (assoc-in db state-registry/*theme-path* new-theme)
                      ::apply-theme {:theme new-theme}}))})

(defn init!
  "Initialize theme effects.
   
   Note: Effects (rf/reg-fx) are registered at namespace load time above.
   This function exists for explicit initialization if needed."
  []
  nil)
