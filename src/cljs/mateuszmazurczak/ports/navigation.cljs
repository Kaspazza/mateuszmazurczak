(ns mateuszmazurczak.ports.navigation
  "Public API for navigation in the application.
   This namespace encapsulates all routing and history implementation details,
   providing a clean interface for UI components."
  (:require
   [mateuszmazurczak.adapters.navigation.reitit-history :as history-reitit]
   [mateuszmazurczak.adapters.navigation.reitit-router  :as router-reitit]))

(defn init-history!
  "Initialize history tracking.
   
   Arguments:
   - router: The router instance
   - dispatch-fn: Function to dispatch route change events (e.g., state/dispatch!)"
  [router dispatch-fn]
  (history-reitit/start-history! router
                                 (fn [route-data] (dispatch-fn [:nav/route-changed route-data]))))

(defn stop-history! "Stop history tracking" [history] (history-reitit/stop-history! history))

;; Global state - to be initialized by the system
(defonce ^:private router-instance (atom nil))
(defonce ^:private history-instance (atom nil))

(defn set-router!
  "Set the router instance - called by system during initialization"
  [router]
  (reset! router-instance router))

(defn set-history!
  "Set the history instance - called by system during initialization"
  [history]
  (reset! history-instance history))

(defn get-router "Get the current router instance" [] @router-instance)

(defn get-history "Get the current history instance" [] @history-instance)

(defn route-name "Get the name of the current route" [current-route] (:route-name current-route))

(defn path-params
  "Get path parameters from current route"
  [current-route]
  (:path-parameters current-route))

(defn query-params
  "Get query parameters from current route"
  [current-route]
  (:query-parameters current-route))

(defn route-details
  "Find route data for a given path"
  [path]
  (when-let [match (router-reitit/match-by-path (get-router) path)]
    {:route-name (router-reitit/route-name-from-match match)
     :page-id (router-reitit/page-id-from-match match)
     :path-parameters (router-reitit/path-params-from-match match)
     :query-parameters (router-reitit/query-params-from-match match)}))


(defn href
  "Generate a URL for the given route."
  ([] (href nil nil nil))
  ([route-name] (href route-name nil nil))
  ([route-name path-params] (href route-name path-params nil))
  ([route-name path-params query-params]
   (history-reitit/href (get-history) route-name path-params query-params)))

(defn change-query-parameters!
  "Change only query-params"
  ([query-params] (change-query-parameters! query-params true))
  ([query-params preserve-history?]
   (history-reitit/set-query! (get-history) query-params (not preserve-history?))))

(defn navigate!
  "Navigate to URL, with optional history preservation"
  ([route-name] (navigate! route-name {}))
  ([route-name params] (navigate! route-name params true))
  ([route-name {:keys [path-parameters query-parameters]} preserve-history?]
   (history-reitit/navigate (get-history)
                            route-name
                            {:replace (not preserve-history?)
                             :path-params path-parameters
                             :query-params query-parameters})))
