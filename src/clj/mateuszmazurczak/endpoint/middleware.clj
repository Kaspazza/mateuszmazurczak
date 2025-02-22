(ns mateuszmazurczak.endpoint.middleware
  "Middlewares for mateuszmazurczak project"
  (:require
   [clojure.string                       :as str]
   [mateuszmazurczak.env                 :as mm-env]
   [mateuszmazurczak.i18n.dict.resources :as mm-i18n-dict-resources]
   [mateuszmazurczak.i18n.dict.text      :as mm-i18n-dict-text]
   [mateuszmazurczak.i18n.language       :as lang-web]
   [mateuszmazurczak.i18n.tempura        :as mm-i18n-tempura]
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
   [taoensso.tempura                     :as tempura]))

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
  (->> http-request
       :headers
       (get "host")
       extract-tld-from-host))

(defn accepted-languages
  "Return the accepted languages in the http request
  Params:
  * `http-request` an http request"
  [http-request]
  (-> http-request
      :headers
      (get "accept-language")))

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

(def opts
  (mm-i18n-tempura/create-opts mm-i18n-dict-text/dict
                               mm-i18n-dict-resources/dict))


(defn language-strategy
  "Parse an http request to decide which language to use.
  - If a parameter language is set in the path, just use it,
  - Else If a language is set in the cookie, use it
  - Use the tld
  - If none is set, use the main-lang as a default language
  Params:
  * `web-translator` the translator instance to know the default languages
  * `http-request` request to parse"
  [default-languages http-request]
  (let [par-lang (get-in http-request [:params :lang])
        lang-str (or par-lang
                     (cookies-language http-request)
                     (some-> (accepted-languages http-request)
                             (subs 0 2))
                     (some-> http-request
                             tld-language)
                     (first default-languages))]
    lang-str))

(defn- translate
  [langs-id tr-id resources]
  (let [locales (vec (concat langs-id lang-web/main-langs))
        translated-text (tempura/tr opts locales [tr-id] resources)]
    translated-text))

(defn- wrap-ring-request
  [handler]
  (fn [{:keys [tempura/accept-langs_ locales]
        :as http-request}]
    (let [locales-str [(language-strategy lang-web/main-langs http-request)]
          {:keys [locales]
           :as updated-request}
          (-> http-request
              (assoc :accept-langs accept-langs_ :locales locales-str)
              (dissoc :tempura/accept-langs_))]
      (-> updated-request
          (dissoc :tempura/tr)
          (assoc :tr
                 (fn
                   ([tr-id resources] (translate locales tr-id resources))
                   ([tr-id] (translate locales tr-id nil))))
          handler
          (assoc-in [:headers "locales"] locales-str)))))

(defn wrap-translation
  [handler]
  (tempura/wrap-ring-request (wrap-ring-request handler) {}))

(def global-middlewares
  "Middleware for the whole app"
  [ring-cookies/wrap-cookies ;; It's important to have cookies before translator to allow strategy based on cookie lang
   rrmp/parameters-middleware ;; It's important to have parameters before translator to allow strategy based on parameters lang
   ring-keyword-params/wrap-keyword-params ;; Translator use keyworded parameters
   wrap-translation])
