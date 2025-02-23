(ns mateuszmazurczak.navigation.router
  "Mateuszmazurczak cust-app front end router"
  (:require
   [day8.re-frame.tracing                       :refer-macros [fn-traced]]
   [mateuszmazurczak.navigation.router.protocol :as mm-router]
   [mateuszmazurczak.navigation.router.reitit   :as mm-router-reitit]
   [mateuszmazurczak.navigation.routes          :as mm-fe-routes]
   [mount.core                                  :refer [defstate]]
   [re-frame.core                               :as rf]))

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

(rf/reg-sub ::route-match (fn [db _] (:route-match db)))

(rf/reg-fx :new-route-scroll-position
           (fn [fragment]
             (if fragment
               (on-element-exist (str "#" fragment) #(.scrollIntoView %))
               (.scrollTo js/window 0 0))))

(rf/reg-event-fx ::new-route-match
                 (fn-traced [{:keys [db]} [_ match]]
                            {:db (assoc db :route-match match)
                             :new-route-scroll-position (:fragment match)}))

(defn start-router
  []
  (mm-router-reitit/make-reitit-router mm-fe-routes/routes {}))

(defstate router
          :start
          (try (start-router)
               (catch :default e (ex-info "Impossible to start router" e))))

(defn match-from-url
  "Match the `url`
  For instance match `#legal/disclaimer` into reitit matcher to `:disclaimer`, query params, ...
  Params:
  * `router` (Optional, default to this namespace router )
  * `url` to analyse"
  ([url] (mm-router/match-from-url @router url))
  ([router url] (mm-router/match-from-url router url)))

(defn panel-id
  "Return the name of the panel to retrieve"
  [match]
  (mm-router/panel-id @router match))

(defn url-params
  "Return the url parameters of the matched route
Params:
  * `match` match"
  [match]
  (mm-router/url-params @router match))
