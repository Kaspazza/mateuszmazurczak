(ns mateuszmazurczak.adapters.events.reframe.pages.home
  "Re-frame adapter for home page events.
   
   Implements home page events from events/registry.cljs as a handler map.
   This is INTERNAL adapter code - UI components should dispatch via events/dispatch!"
  (:require
   [mateuszmazurczak.adapters.navigation.routes :as-alias mm-routes]
   [mateuszmazurczak.domain.articles.core       :as articles]
   [mateuszmazurczak.domain.pages.home          :as home-domain]
   [mateuszmazurczak.domain.state.registry      :as state-registry]
   [mateuszmazurczak.ports.navigation           :as navigation]
   [mateuszmazurczak.utils.map                  :as utils-map]))

(def handlers
  "Home page event handlers exported as data.
   
   Each handler is a pure function that will be registered with re-frame
   by the wiring namespace. The registry metadata determines whether it's
   registered as :db or :fx handler."
  {:home/refresh (fn [db [_]]
                   (let [navigation-href (navigation/href ::mm-routes/articles)
                         page-data (home-domain/build-home-page-data articles/articles
                                                                     navigation-href)]
                     (update-in db
                                state-registry/*home-page-path*
                                utils-map/deep-merge
                                page-data
                                {:loading? false})))
   :home/on-route-enter (fn [{:keys [_db]} [_]] {:dispatch [:home/refresh]})})
