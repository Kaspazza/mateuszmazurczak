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

;;TODO rethink what logic from here should go to domain
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
   (let [solution-ids (get-in raw-data
                              (aoc-domain/relative-path aoc-domain/*aoc-solution-ids-path*))
         solutions (aoc-domain/denormalize-solutions solution-ids solutions-entities)
         ui-data
         (-> raw-data
             events/dispatch-markers->handlers
             fi18n/i18n-markers->translation
             (assoc-in (aoc-domain/relative-path aoc-domain/*aoc-solutions-path*) solutions)
             (update-in (aoc-domain/relative-path aoc-domain/*aoc-solutions-data-path*)
                        dissoc
                        :solution-ids)
             (assoc-in (aoc-domain/relative-path aoc-domain/*aoc-solutions-theme-path*) theme)
             (assoc-in (aoc-domain/relative-path aoc-domain/*aoc-solutions-admin-logged-in-path*)
                       admin-logged-in?)
             (update-in (aoc-domain/relative-path aoc-domain/*aoc-solutions-path*)
                        events/dispatch-markers->handlers))
         solutions-text (get-in ui-data
                                (aoc-domain/relative-path aoc-domain/*aoc-solutions-text-path*))
         enriched-solutions
         (mapv #(assoc % :theme theme :text solutions-text)
               (get-in ui-data (aoc-domain/relative-path aoc-domain/*aoc-solutions-path*)))
         ui-data (assoc-in ui-data
                  (aoc-domain/relative-path aoc-domain/*aoc-solutions-path*)
                  enriched-solutions)
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
