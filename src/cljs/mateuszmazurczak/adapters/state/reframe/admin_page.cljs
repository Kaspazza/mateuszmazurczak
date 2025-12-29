(ns mateuszmazurczak.adapters.state.reframe.admin-page
  "Re-frame subscriptions for admin page state."
  (:require
   [mateuszmazurczak.application.admin.page-data :as page-data]
   [mateuszmazurczak.domain.pages.admin          :as admin-domain]
   [mateuszmazurczak.domain.state.registry       :as state-registry]
   [re-frame.core                                :as rf]))

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
              (page-data/prepare-ui-data raw-data logged-in? logger)))

(def watch
  "Admin page watch (subscriptions) for re-frame."
  #{:admin/raw-data :pages/admin :admin/logged-in?})
