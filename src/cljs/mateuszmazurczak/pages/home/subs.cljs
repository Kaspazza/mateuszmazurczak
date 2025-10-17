(ns mateuszmazurczak.pages.home.subs
  "Re-frame subscriptions for home page data"
  (:require
   [mateuszmazurczak.events            :as events]
   [mateuszmazurczak.frontend-i18n     :as fi18n]
   [mateuszmazurczak.pages.home.schema :as home-schema]
   [re-frame.core                      :as rf]))

(rf/reg-sub :home/raw-data (fn [db _] (get-in db [:pages :home])))

(rf/reg-sub :panels/home
            :<-
            [:home/raw-data]
            (fn [raw-data _]
              (let [processed-data (-> raw-data
                                       fi18n/translate-tree
                                       events/dispatch-tree)
                    valid? (home-schema/valid-home-page-data? processed-data)]
                (if valid?
                  {:data processed-data
                   :valid? valid?}
                  {:data processed-data
                   :valid? valid?
                   :error {:id ::home-translation-failed
                           :data (home-schema/explain-home-page-data
                                  processed-data)
                           :actual-data processed-data}}))))

