(ns mateuszmazurczak.adapters.state.reframe.aoc
  "Re-frame subscriptions for AoC page state."
  (:require
   [mateuszmazurczak.application.aoc.page-data :as page-data]
   [mateuszmazurczak.domain.state.registry     :as state-registry]
   [re-frame.core                              :as rf]))

(rf/reg-sub :aoc/raw-data (fn [db _] (get-in db state-registry/*aoc-page-path*)))

(rf/reg-sub :aoc/solutions-entities (fn [db _] (get-in db state-registry/*aoc-solutions-path*)))

(rf/reg-sub
 :pages/aoc
 :<-
 [:aoc/raw-data]
 :<-
 [:aoc/solutions-entities]
 :<-
 [:theme/current]
 :<-
 [:logger]
 :<-
 [:admin/logged-in?]
 (fn [[raw-data solutions-entities theme logger admin-logged-in?] _]
   (page-data/prepare-ui-data raw-data solutions-entities theme admin-logged-in? logger)))

(def watch
  "AoC page watch (subscriptions) for re-frame."
  #{:aoc/raw-data :aoc/solutions-entities :pages/aoc})
