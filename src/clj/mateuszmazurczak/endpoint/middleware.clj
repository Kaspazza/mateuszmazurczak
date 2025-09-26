(ns mateuszmazurczak.endpoint.middleware
  "Middlewares for mateuszmazurczak project"
  (:require
   [clojure.set]
   [clojure.string                       :as str]
   [mateuszmazurczak.endpoint.error-page :as error-page]
   [mateuszmazurczak.endpoint.handler    :as mm-endpoint-handler]
   [mateuszmazurczak.env                 :as mm-env]
   [mateuszmazurczak.i18n                :as i18n]
   [mateuszmazurczak.i18n.language       :as lang-web]
   [mateuszmazurczak.logging             :as log]
   [reitit.ring.coercion                 :as rrc]
   [reitit.ring.middleware.muuntaja      :as rrmm]
   [reitit.ring.middleware.parameters    :as rrmp]
   [ring.middleware.anti-forgery         :as ring-anti-forgery]
   [ring.middleware.content-type         :as ring-content-type]
   [ring.middleware.cookies              :as ring-cookies]
   [ring.middleware.cors                 :as ring-cors]
   [ring.middleware.keyword-params       :as ring-keyword-params]
   [ring.middleware.session              :as ring-session]
   [ring.middleware.session.memory       :as ring-memory]
   [ring.util.http-response              :as http-response]))

(defn cors-domain-routes
  [main-domain]
  (let [tlds (->> lang-web/web-languages
                  vals
                  (map :tld)
                  (into #{}))]
    (->> (for [tld tlds] (str/join "." [main-domain tld]))
         (mapv (fn [domain] (re-pattern (str ".*" domain "$")))))))

(defn extract-tld-from-host
  "Extract the tld from an host
  Params:
  * `url` - url to parse"
  [url]
  (some->> url
           (re-find #".*(?:\.([a-zA-Z]\w{1,2}))(?::\d{1,4})?$")
           second))

(defn tld-language
  "Get the tld in the host of the http request"
  [http-request]
  (let [user-tld (-> http-request
                     :headers
                     (get "host")
                     extract-tld-from-host)]
    (some (fn [[id {:keys [tld]}]] (when (= user-tld tld) id))
          lang-web/web-languages)))

(defn accepted-languages
  "Return the accepted languages in the http request
  Params:
  * `http-request` an http request"
  [http-request]
  (let [headers-lang (-> http-request
                         :headers
                         (get "accept-language"))
        user-accepted-languages (when headers-lang
                                  (->> (str/split headers-lang #",")
                                       (map #(subs % 0 2))
                                       (map #(keyword (str/lower-case %)))
                                       vec))]
    (when (seq (clojure.set/intersection (set user-accepted-languages)
                                         (set (keys lang-web/web-languages))))
      user-accepted-languages)))

(defn cookies-language
  "Get cookies value under 'lang' key from req
  Params:
  * `http-request` an http request"
  [http-request]
  (let [lang (-> http-request
                 :cookies
                 (get "lang")
                 :value)]
    (cond
      (= lang "null") nil
      (string? lang) (keyword (str/lower-case lang))
      :else lang)))

(defn wrap-throw [_handler] (fn [_request] (/ 1 0)))

(defn wrap-exception-handling*
  [handler request logger]
  (try (handler request)
       (catch Exception e
         (let [error-data {:request-uri (:uri request)
                           :request-method (:request-method request)
                           :request-headers (select-keys
                                             (:headers request)
                                             ["host" "user-agent" "referer"])
                           :error-type (-> e
                                           ex-data
                                           :type)
                           :original-cause (-> e
                                               ex-data
                                               :cause)}]
           (log/error! logger
                       {:error e
                        :id ::unhandled-request-exception
                        :data error-data})
           (->> request
                error-page/internal-error-page
                http-response/internal-server-error
                mm-endpoint-handler/web-page)))))

(defn wrap-exception-handling
  [logger handler]
  {:pre [logger (fn? handler)]}
  (fn [request] (wrap-exception-handling* handler request logger)))

(defn wrap-request-logging
  "Log incoming requests and responses with route info"
  [handler logger]
  (fn [request]
    (let [start (System/nanoTime)
          method (-> request
                     :request-method
                     name
                     str/upper-case)
          uri (:uri request)]
      (log/log! logger
                {:level :debug
                 :id ::incoming-request
                 :data {:method method
                        :uri uri
                        :query-params (:query-params request)
                        :path-params (some-> request
                                             :reitit.core/match
                                             :path-params)
                        :headers (select-keys
                                  (:headers request)
                                  ["host" "user-agent" "referer"])}})
      (let [response (handler request)
            elapsed-ms (long (/ (- (System/nanoTime) start) 1e6))
            match (:reitit.core/match request)
            route-name (some-> match
                               :data
                               :name)
            route-template (some-> match
                                   :template)
            status (:status response)
            level (cond
                    (>= (long status) 500) :error
                    (>= (long status) 400) :warn
                    :else :info)]
        (log/log! logger
                  {:level level
                   :id ::request-completed
                   :data {:method method
                          :uri uri
                          :status status
                          :duration-ms elapsed-ms
                          :route-name route-name
                          :route-template route-template
                          :path-params (some-> match
                                               :path-params)}})
        response))))


(def web-middleware
  "Midllewares for web pages"
  (vec
   (concat
    [(fn [handler]
       (ring-session/wrap-session handler
                                  {:store (ring-memory/memory-store (atom {}))
                                   :cookies-attrs {:http-only true}}))
     ring-anti-forgery/wrap-anti-forgery
     (fn [handler]
       (ring-cors/wrap-cors handler
                            :access-control-allow-origin
                            (concat (cors-domain-routes "mateuszmazurczak")
                                    ;;TODO make sure this provider is okay after deploy
                                    [#".*my-provider.domain$"])
                            :access-control-allow-methods
                            [:get :post :put :delete]
                            :access-control-allow-credentials "true"))
     ring-content-type/wrap-content-type
     rrc/coerce-exceptions-middleware
     rrc/coerce-request-middleware
     rrc/coerce-response-middleware
     rrmm/format-negotiate-middleware
     rrmm/format-response-middleware
     rrmm/format-request-middleware]
    mm-env/env-middlewares)))


(defn language-strategy
  "Parse an http request to decide which language to use.
  - If a parameter language is set in the path, just use it,
  - Else If a language is set in the cookie, use it
  - Use the tld
  - If none is set, use the main-lang as a default language
  Params:
  * `web-translator` the translator instance to know the default languages
  * `http-request` request to parse"
  [http-request]
  (or (get-in http-request [:params :lang])
      (cookies-language http-request)
      (accepted-languages http-request)
      (tld-language http-request)
      (first lang-web/main-langs)))

(defn wrap-translation
  [handler translator]
  (fn [http-request]
    (let [lang (language-strategy http-request)]
      (-> http-request
          (assoc :tr (fn ([tr-id] (i18n/tr translator lang tr-id))))
          handler))))

(defn global-middlewares
  "Middleware for the whole app
  Params:
  * `translator` - translator function from the system
  * `logger` - logger instance from the system"
  [translator logger]
  [ring-cookies/wrap-cookies ;; It's important to have cookies before translator to allow strategy based on cookie lang
   rrmp/parameters-middleware ;; It's important to have parameters before translator to allow strategy based on parameters lang
   ring-keyword-params/wrap-keyword-params ;; Translator use keyworded parameters
   (fn [handler] (fn [request] (handler (assoc request :logger logger)))) ;; Add logger to request
   (fn [handler] (wrap-translation handler translator))
   (fn [handler] (wrap-request-logging handler logger))
   (partial wrap-exception-handling logger)])
