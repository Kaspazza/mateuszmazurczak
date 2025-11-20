(ns mateuszmazurczak.application.router
  "Entry point to mateuszmazurczak app"
  (:require
   [mateuszmazurczak.application.pages :as mm-nav-pages]
   [mateuszmazurczak.ports.logging     :as log]
   [mateuszmazurczak.ports.state       :as state]
   [mateuszmazurczak.system.config     :as config]))

(defn handle-page-ex
  [page-data page-id]
  (let [logger @(state/watch [:logger])]
    (when (and (map? page-data) (not (:valid? page-data)))
      (if (config/development?)
        (throw (ex-info "Translated home page data validation failed"
                        {:id (get-in page-data [:error :id] ::router-page-data)
                         :context (if-let [error-data (get-in page-data [:error])]
                                    error-data
                                    page-data)
                         :page-id page-id}))
        (log/error! logger
                    {:error (ex-info "Page data validation failed"
                                     {:page-data page-data
                                      :page-id page-id})
                     :id (get-in page-data [:error :id] ::router-page-data)
                     :data {:page-id page-id
                            :validation-error (if-let [error-data (get-in page-data [:error :data])]
                                                error-data
                                                page-data)}})))))

(defn router-component
  "Component to route to the current page based on current-route.
   
   Watch current route and fetches appropriate page data
   based on page-id, then passes both to the pages multimethod."
  []
  (let [current-route @(state/watch [:nav/current-route])
        page-id (:page-id current-route)
        ;;TODO Other pages than home will be covered soon and this when will not be needed
        page-data (when (and page-id (contains? (set (keys state/watch-reg)) page-id))
                    @(state/watch [page-id]))]
    (handle-page-ex page-data page-id)
    [mm-nav-pages/pages current-route (:data page-data)]))

(defn main-component "Main component replacing app" [] [router-component])
