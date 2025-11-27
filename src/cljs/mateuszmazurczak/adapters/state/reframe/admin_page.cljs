(ns mateuszmazurczak.adapters.state.reframe.admin-page
  "Re-frame subscriptions for admin page state."
  (:require
   [mateuszmazurczak.domain.pages.admin    :as admin-domain]
   [mateuszmazurczak.domain.state.registry :as state-registry]
   [mateuszmazurczak.frontend-i18n         :as fi18n]
   [mateuszmazurczak.ports.events          :as events]
   [mateuszmazurczak.ports.logging         :as log]
   [re-frame.core                          :as rf]))

(rf/reg-sub :admin/logged-in? (fn [db _] (get-in db state-registry/*admin-logged-in-path*)))

(rf/reg-sub :admin/raw-data (fn [db _] (get-in db admin-domain/*admin-page-path*)))

(rf/reg-sub :pages/admin
            :<-
            [:admin/raw-data]
            :<-
            [:admin/logged-in?]
            :<-
            [:logger]
            (fn [[raw-data logged-in? logger] _]
              (let [ui-data (-> raw-data
                                (assoc :logged-in? logged-in?)
                                events/dispatch-markers->handlers
                                fi18n/i18n-markers->translation)
                    valid? (admin-domain/valid-admin-page-ui-data? ui-data)
                    explanation (when-not valid? (admin-domain/explain-admin-page-ui-data ui-data))]
                (when-not valid?
                  (log/error! logger
                              {:error (ex-info "Admin page validation failed"
                                               {:type ::admin-validation-failed
                                                :explanation explanation
                                                :raw-data raw-data})}))
                (if valid?
                  {:data ui-data
                   :valid? valid?}
                  {:data ui-data
                   :valid? valid?
                   :error {:id ::admin-validation-failed
                           :actual-data raw-data
                           :explanation explanation}}))))

(def watch
  "Admin page watch (subscriptions) for re-frame."
  #{:admin/raw-data :pages/admin :admin/logged-in?})
