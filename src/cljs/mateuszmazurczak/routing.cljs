(ns mateuszmazurczak.routing
  "Entry point to mateuszmazurczak app"
  (:require
   [mateuszmazurczak.config            :as config]
   [mateuszmazurczak.logging           :as log]
   [mateuszmazurczak.navigation.panels :as mm-nav-panels]
   [mateuszmazurczak.state             :as state]))

(defn handle-page-ex
  [page-data panel-id]
  (let [logger @(state/watch [:logger])]
    (when (and (map? page-data) (not (:valid? page-data)))
      (if (config/development?)
        (throw (ex-info "Translated home page data validation failed"
                        {:id (get-in page-data [:error :id] ::router-page-data)
                         :context (if-let [error-data (get-in page-data
                                                              [:error])]
                                    error-data
                                    page-data)
                         :panel-id panel-id}))
        (log/error! logger
                    {:id (get-in page-data [:error :id] ::router-page-data)
                     :data {:error (if-let [error-data (get-in page-data
                                                               [:error :data])]
                                     error-data
                                     page-data)
                            :panel-id panel-id}})))))

(defn router-component
  "Component to route to the current panel based on current-route.
   
   Watch current route and fetches appropriate page data
   based on panel-id, then passes both to the panels multimethod."
  []
  (let [current-route @(state/watch [:nav/current-route])
        panel-id (:panel-id current-route)
        ;;TODO Other panels than home will be covered soon and this when will not be needed
        page-data (when (and panel-id
                             (contains? (set (keys state/watch-reg)) panel-id))
                    @(state/watch [panel-id]))]
    (handle-page-ex page-data panel-id)
    [mm-nav-panels/panels current-route (:data page-data)]))

(defn main-component "Main component replacing app" [] [router-component])
