(ns mateuszmazurczak.endpoint.middleware
  "Middlewares for mateuszmazurczak project
  Should mainly select middlewares from `automaton-web.middleware`"
  (:require
   [automaton-web.i18n.language         :as web-language]
   [automaton-web.middleware            :as web-middleware]
   [mateuszmazurczak.env                :as env]
   [mateuszmazurczak.i18n.be.translator :as mateuszmazurczak-be-translator]
   [mateuszmazurczak.i18n.language      :as mateuszmazurczak-language]))

(def web-middleware
  "Midllewares for web pages"
  (vec
   (concat [web-middleware/wrap-log ;; It is important to have response first as it will log the result at that point, everything above will be ignored
            web-middleware/wrap-session
            web-middleware/wrap-anti-forgery
            [web-middleware/wrap-cors
             :access-control-allow-origin
             (concat (web-language/cors-domain-routes
                      mateuszmazurczak-language/languages
                      "hephaistox")
                     ;;TODO update it for my provider
                     [#".*cleverapps.io$"])
             :access-control-allow-methods
             [:get :post :put :delete]
             :access-control-allow-credentials
             "true"]
            web-middleware/wrap-content-type
            web-middleware/coerce-exceptions-middleware
            web-middleware/coerce-request-middleware
            web-middleware/coerce-response-middleware
            web-middleware/format-negotiate-middleware
            web-middleware/format-response-middleware
            web-middleware/format-request-middleware]
           env/env-middlewares)))

(def global-middlewares
  "Middleware for the whole app"
  (web-middleware/translation-middlewares
   mateuszmazurczak-be-translator/web-be-translator))
