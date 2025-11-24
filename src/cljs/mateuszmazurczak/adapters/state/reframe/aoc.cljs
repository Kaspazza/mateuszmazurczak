(ns mateuszmazurczak.adapters.state.reframe.aoc
  "Re-frame subscriptions for AoC page state."
  (:require
   [mateuszmazurczak.domain.pages.aoc      :as aoc-domain]
   [mateuszmazurczak.domain.state.registry :as state-registry]
   [mateuszmazurczak.frontend-i18n         :as fi18n]
   [mateuszmazurczak.ports.events          :as events]
   [mateuszmazurczak.ports.logging         :as log]
   [re-frame.core                          :as rf]))

(rf/reg-sub :aoc/raw-data (fn [db _] (get-in db state-registry/*aoc-page-path*)))

(rf/reg-sub :aoc/solutions-entities (fn [db _] (get-in db state-registry/*aoc-solutions-path*)))

(rf/reg-sub :pages/aoc
            :<-
            [:aoc/raw-data]
            :<-
            [:aoc/solutions-entities]
            :<-
            [:theme/current]
            :<-
            [:logger]
            (fn [[raw-data solutions-entities theme logger] _]
              (let [solution-ids (:solution-ids raw-data)
                    solutions (aoc-domain/denormalize-solutions solution-ids solutions-entities)
                    ui-data (-> raw-data
                                (dissoc :solution-ids)
                                (assoc :solutions solutions)
                                (assoc :theme theme)
                                events/dispatch-markers->handlers
                                fi18n/i18n-markers->translation)
                    valid? (aoc-domain/valid-aoc-page-ui-data? ui-data)
                    explanation (when-not valid? (aoc-domain/explain-aoc-page-ui-data ui-data))]
                (when-not valid?
                  (log/error! logger
                              {:error (ex-info "AOC page validation failed"
                                               {:type ::aoc-validation-failed
                                                :explanation explanation
                                                :raw-data raw-data})}))
                (if valid?
                  {:data ui-data
                   :valid? valid?}
                  {:data ui-data
                   :valid? valid?
                   :error {:id ::aoc-validation-failed
                           :actual-data raw-data
                           :explanation explanation}}))))

(def watch
  "AoC page watch (subscriptions) for re-frame."
  #{:aoc/raw-data :aoc/solutions-entities :pages/aoc})
