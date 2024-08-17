(ns mateuszmazurczak.endpoint.router
  "Create web routers"
  (:require
   [automaton-core.log                      :as core-log]
   [automaton-web.adapters.be.http-response :as http-response]
   [automaton-web.pages.errors              :as error-pages]
   [automaton-web.router                    :as web-router]
   [mateuszmazurczak.endpoint.handler                :as mateuszmazurczak-handler]
   [mateuszmazurczak.endpoint.middleware             :as mateuszmazurczak-middleware]
   [mateuszmazurczak.endpoint.routes                 :as mateuszmazurczak-endpoint-routes]))

(def ring-handler
  "Ring handler for web pages of mateuszmazurczak app
  Params:
  * `ring-handler`"
  (web-router/ring-handler
   {:web-routes (mateuszmazurczak-endpoint-routes/web-routes mateuszmazurczak-handler/registry)
    :web-middleware mateuszmazurczak-middleware/web-middleware
    :translator-middlewares []
    :global-middlewares mateuszmazurczak-middleware/global-middlewares}))

(defn get-app
  "Web application,
  Transform an http request in an http response
  Params:
  * `http-req`"
  [http-req]
  (try
    (ring-handler http-req)
    (catch Exception e
      (core-log/error
       (ex-info
        "While running app routes an exception has happened, look into :error to find more information"
        {:error e}))
      (http-response/internal-server-error (error-pages/internal-error-page
                                            http-req)))
    (catch Error e
      (core-log/fatal
       (ex-info
        "While running app routes an error has happened, look into :error to find more information"
        {:error e}))
      (http-response/internal-server-error (error-pages/internal-error-page
                                            http-req)))))
