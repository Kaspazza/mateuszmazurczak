(ns mateuszmazurczak.adapters.state.reframe.theme
  "Re-frame subscriptions for theme state."
  (:require
   [mateuszmazurczak.domain.state.registry :as state-registry]
   [re-frame.core                          :as rf]))

(rf/reg-sub :theme/current (fn [db _] (get-in db state-registry/*theme-path*)))

(def watch "Theme watch (subscriptions) for re-frame." #{:theme/current})
