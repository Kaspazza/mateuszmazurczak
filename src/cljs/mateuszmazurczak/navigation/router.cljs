(ns mateuszmazurczak.navigation.router
  "Mateuszmazurczak cust-app front end router"
  (:require
   [mateuszmazurczak.navigation.router.reitit :as router-impl]
   [mateuszmazurczak.navigation.routes        :as mm-fe-routes]
   [mount.core                                :refer [defstate]]
   [re-frame.core                             :as rf]))

(defn on-element-exist
  "Waits for `selector` element to appear in document and executes `on-exist-fn`"
  [selector on-exist-fn]
  (-> (new js/Promise
           (fn [resolve]
             (when (.querySelector js/document selector)
               (resolve (.querySelector js/document selector)))
             #_{:clj-kondo/ignore [:inline-def]}
             (def observer
               (new js/MutationObserver
                    (fn [_mutations]
                      (when (.querySelector js/document selector)
                        (.disconnect observer)
                        (resolve (.querySelector js/document selector))))))
             (.observe observer
                       (.-body js/document)
                       #js {:childList true
                            :subtree true})))
      (.then on-exist-fn)))




(rf/reg-fx :handle-fragment-scroll
           (fn [fragment]
             (if fragment
               (on-element-exist (str "#" fragment) #(.scrollIntoView %))
               (.scrollTo js/window 0 0))))

(defstate
 router
 :start
 (try (js/console.log "Router started")
      (router-impl/create-router mm-fe-routes/routes)
      (catch :default e (js/console.error "Router failed to start" e) nil)))

(defn panel-id
  "Get panel ID for a route"
  [route-name & [path-params]]
  (router-impl/panel-id @router route-name path-params))

(defn path-params
  "Get default path parameters for a route"
  [route-name]
  (router-impl/path-params @router route-name))

(defn all-routes "Get all route names" [] (router-impl/all-routes @router))

(defn current-route-name
  "Get the name of the current route"
  [current-route]
  (:route-name current-route))

(defn current-path-params
  "Get path parameters from current route"
  [current-route]
  (:path-parameters current-route))

(defn current-query-params
  "Get query parameters from current route"
  [current-route]
  (:query-parameters current-route))

(defn route-details
  "Find route data for a given path"
  [path]
  (when-let [match (router-impl/match-by-path @router path)]
    {:route-name (router-impl/route-name-from-match match)
     :panel-id (router-impl/panel-id-from-match match)
     :path-parameters (router-impl/path-params-from-match match)
     :query-parameters (router-impl/query-params-from-match match)
     :fragment (:fragment match)}))
