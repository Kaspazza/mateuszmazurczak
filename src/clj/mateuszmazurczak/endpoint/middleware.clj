(ns mateuszmazurczak.endpoint.middleware
  "Middlewares for mateuszmazurczak project"
  (:require
   [clojure.string                       :as str]
   [mateuszmazurczak.env                 :as mm-env]
   [mateuszmazurczak.i18n.dict.resources :as mm-i18n-dict-resources]
   [mateuszmazurczak.i18n.dict.text      :as mm-i18n-dict-text]
   [mateuszmazurczak.i18n.tempura        :as mm-i18n-tempura]
   [mateuszmazurczak.utils               :as mm-utils]
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
  (let [tlds ["pl" "com"]]
    (->> (for [tld tlds] (str/join "." [main-domain tld]))
         (mapv (fn [domain] (re-pattern (str ".*" domain "$")))))))

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

(def main-langs [:en])

(def opts
  (mm-i18n-tempura/create-opts mm-i18n-dict-text/dict
                               mm-i18n-dict-resources/dict))

(defn language-choice-strategy*
  "Apply the language selection strategy
  - If a parameter language is set in the path, just use it,
  - Else If a language is set in the cookie, use it
  - Use the tld
  - If none is set, use the main-lang as a default language
  All variables with trailing _ are delays, so they'll be evaluted only if the previous step has failed to find a value
  Params:
  * `par-lang` language imposed in the parameters
  * `cookies-lang_` language stored in the cookie
  * `accepted-languages_` accepted languages by the user browser
  * `tld-lang_` is the language in the tld
  * `main-lang_` main language"
  [par-lang cookies-lang_ accepted-languages_ tld-lang_ main-lang_]
  (or par-lang @cookies-lang_ @accepted-languages_ @tld-lang_ @main-lang_))

(defn lang-str-choice-strategy-def
  "Parse an http request to apply the strategy to decide which language we use
  This is a design choice to let the strategy decides how default-language is used, so it is not left to tempura to apply default
  Params:
  * `web-translator` the translator instance to know the default languages
  * `http-request` request to parse"
  [default-languages http-request]
  (let [par-lang (mm-utils/get-param http-request :lang)
        lang-str (if (or (not (string? par-lang)) (str/blank? par-lang))
                   (language-choice-strategy*
                    par-lang
                    (delay (mm-utils/cookies-language http-request))
                    (delay (some-> (mm-utils/accepted-languages http-request)
                                   (subs 0 2)))
                    (delay (some-> http-request
                                   mm-utils/tld-language))
                    (delay (first default-languages)))
                   par-lang)]
    lang-str))

(defn translate
  [langs-id tr-id resources]
  (let [locales (vec (concat langs-id main-langs))
        translated-text (tempura/tr opts locales [tr-id] resources)]
    translated-text))

(defn wrap-ring-request
  [handler]
  (fn [{:keys [tempura/accept-langs_ locales]
        :as http-request}]
    (let [locales-str [(lang-str-choice-strategy-def main-langs http-request)]
          {:keys [locales]
           :as updated-request}
          (-> http-request
              (assoc :accept-langs accept-langs_ :locales locales-str)
              (dissoc :tempura/accept-langs_ :tempura/tr))]
      (-> updated-request
          (assoc :tr
                 (fn
                   ([tr-id resources] (translate locales tr-id resources))
                   ([tr-id] (translate locales tr-id nil))))
          handler
          (assoc-in [:headers "locales"] locales-str)))))

(def global-middlewares
  "Middleware for the whole app"
  [ring-cookies/wrap-cookies  ;; It's important to have cookies before
   ;; translator to allow strategy based on cookie
   ;; lang
   rrmp/parameters-middleware ;; It's important to have parameters before
   ;; translator to allow strategy based on
   ;; parameters lang
   ring-keyword-params/wrap-keyword-params ;; Translator use keyworded parameters
   (fn [handler] (tempura/wrap-ring-request (wrap-ring-request handler) {}))])
