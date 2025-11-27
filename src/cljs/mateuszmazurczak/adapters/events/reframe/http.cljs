(ns mateuszmazurczak.adapters.events.reframe.http
  "HTTP effects for re-frame events adapter.
   
   Provides HTTP request capabilities as standalone functions and re-frame effects.
   All HTTP-related side effects should go through this module."
  (:require
   [ajax.core                                      :as ajax]
   [clojure.string                                 :as string]
   [mateuszmazurczak.application.aoc.cache-service :as aoc-cache]
   [mateuszmazurczak.domain.state.registry         :as state-reg]
   [mateuszmazurczak.ports.logging                 :as log]
   [re-frame.core                                  :as rf]))

(defn get-anti-forgery-token
  "Extract the anti-forgery token from the DOM.
   Returns nil if token element not found."
  []
  (when-let [token-el (.getElementById js/document "__anti-forgery-token")]
    (.getAttribute token-el "data-anti-forgery-token")))

(rf/reg-event-fx ::on-failure
                 (fn [{:keys [db]} event]
                   ;; event => [::on-failure [:any/custom-fx ...] result]
                   ;; or => [::on-failure result]
                   (let [[_ custom-handler result]
                         (if (= (count event) 3) event [(first event) nil (last event)])
                         logger (get-in db state-reg/*logger-path*)]
                     (log/log! logger
                               {:level :error
                                :id ::fetch-failure
                                :msg "HTTP request failed"
                                :data {:response result}})
                     (cond-> {:db db
                              :fx []}
                       custom-handler (update :fx conj [:dispatch (conj custom-handler result)])))))

(rf/reg-event-fx ::on-success
                 (fn [{:keys [db]} event]
                   (let [[_ custom-handler result]
                         (if (= (count event) 3) event [(first event) nil (last event)])
                         logger (get-in db state-reg/*logger-path*)]
                     (log/log! logger
                               {:level :info
                                :id ::fetch-success
                                :msg "HTTP request succeeded"
                                :data {:response result}})
                     (cond-> {:db db
                              :fx []}
                       custom-handler (update :fx conj [:dispatch (conj custom-handler result)])))))

(defn- ajax-method
  "Convert keyword to ajax method function."
  [method]
  (case (some-> method
                name
                string/upper-case
                keyword)
    :GET ajax/GET
    :HEAD ajax/HEAD
    :POST ajax/POST
    :PUT ajax/PUT
    :DELETE ajax/DELETE
    :OPTIONS ajax/OPTIONS
    :TRACE ajax/TRACE
    :PATCH ajax/PATCH
    :PURGE ajax/PURGE
    ajax/GET))

(defn fetch
  "Make a JSON HTTP request.
   
   Options:
   - :method - HTTP method (default :GET)
   - :url - Request URL
   - :params - Request parameters
   - :event/on-success - Callback function for success
   - :event/on-error - Callback function for error
   
   Automatically includes anti-forgery token for POST/PUT/DELETE/PATCH requests."
  [{:keys [method url params]
    :event/keys [on-success on-error]}]
  (let [needs-csrf? (contains? #{:post :put :delete :patch}
                               (some-> method
                                       name
                                       string/lower-case
                                       keyword))
        csrf-token (when needs-csrf? (get-anti-forgery-token))
        admin-key (aoc-cache/get-admin-key)
        headers (cond-> {}
                  csrf-token (assoc "X-CSRF-Token" csrf-token "X-Requested-With" "XMLHttpRequest")
                  admin-key (assoc "X-Admin-Key" admin-key))]
    ((ajax-method method)
     url
     (cond-> {:format (ajax/json-request-format {})
              :response-format (ajax/json-response-format {:keywords? true})
              :handler on-success
              :error-handler on-error}
       params (assoc :params params)
       (seq headers) (assoc :headers headers)))))

(defn init-effects!
  "Register all HTTP effects for re-frame.
   
   This should be called during events adapter initialization."
  []
  (rf/reg-fx :http
             (fn [{:keys [method url params]
                   :event/keys [on-success on-error]}]
               (fetch
                {:method method
                 :url url
                 :params params
                 :event/on-success (fn [result] (rf/dispatch [::on-success on-success result]))
                 :event/on-error (fn [result] (rf/dispatch [::on-failure on-error result]))}))))
