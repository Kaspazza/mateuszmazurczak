(ns mateuszmazurczak.endpoint.router
  "Create web routers"
  (:require
   [mateuszmazurczak.endpoint.error-page :as error-page]
   [mateuszmazurczak.endpoint.handler    :as mm-handler]
   [mateuszmazurczak.endpoint.handler    :as mm-endpoint-handler]
   [mateuszmazurczak.endpoint.middleware :as mm-middleware]
   [mateuszmazurczak.endpoint.routes     :as mm-endpoint-routes]
   [muuntaja.core                        :as m]
   [reitit.coercion                      :as coercion]
   [reitit.ring                          :as reitit-ring]
   [ring.util.http-response              :as http-response]))

(defn not-found-handler
  [request]
  (-> request
      error-page/not-found-page
      http-response/not-found))

(defn not-allowed-handler
  [request]
  (-> request
      error-page/not-found-page
      http-response/method-not-allowed))

(defn not-acceptable-handler
  [request]
  (-> request
      error-page/not-found-page
      http-response/not-acceptable))

(defn resource-handler
  [{:keys [path root index-files nfh]
    :or {path "/"
         root "public"
         index-files []
         nfh not-found-handler}}]
  (reitit-ring/create-resource-handler {:path path
                                        :root root
                                        :index-files index-files
                                        :not-found-handler nfh}))

(defn apply-middlewares
  "Apply the collection of middlewares to the handler
  Params:
  * `handler` handler to wrap
  * `middlewares` is a collection of middlewares, could be a function or compile middlewares"
  [handler middlewares]
  (reduce
   (fn [handler middleware]
     (if (fn? middleware) (middleware handler) ((:wrap middleware) handler)))
   handler
   middlewares))

(defn default-handlers
  [{:keys [not-found not-allowed not-acceptable]
    :or {not-found not-found-handler
         not-allowed not-allowed-handler
         not-acceptable not-acceptable-handler}}
   middlewares]
  (reitit-ring/create-default-handler
   {:not-found (apply-middlewares not-found middlewares)
    :method-not-allowed (apply-middlewares not-allowed middlewares)
    :not-acceptable (apply-middlewares not-acceptable middlewares)}))

(defn router
  [web-routes web-middleware]
  (reitit-ring/router (vector web-routes
                              [{:compile coercion/compile-request-coercers}])
                      {:data {:muuntaja m/instance
                              :middleware web-middleware}}))
(def ring-handler
  "Ring handler for web pages of mateuszmazurczak app
  Params:
  * `ring-handler`"
  (reitit-ring/ring-handler
   (router mm-endpoint-routes/routes mm-middleware/web-middleware)
   (reitit-ring/routes (resource-handler {}) (default-handlers nil []))
   {:middleware mm-middleware/global-middlewares
    :inject-match? true ;; So the `:match` keyword is in the request and you can analyse it
   }))

(defn get-app
  "Web application,
  Transform an http request in an http response
  Params:
  * `http-req`"
  [http-req]
  (try (ring-handler http-req)
       (catch Exception e
         (prn "ring error" e)
         (->> http-req
              error-page/internal-error-page
              http-response/internal-server-error
              mm-endpoint-handler/web-page))
       (catch Error e
         (prn "ring error" e)
         (->> http-req
              error-page/internal-error-page
              http-response/internal-server-error
              mm-endpoint-handler/web-page))))
