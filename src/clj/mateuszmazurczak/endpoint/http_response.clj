(ns mateuszmazurczak.endpoint.http-response
  (:require
   [ring.util.http-response :as ring-http-response]))

(defn ok
  "200 OK (Success)"
  ([headers body] (merge (ok body) {:headers headers}))
  ([body] (ring-http-response/ok body)))

(defn no-content
  "204 No Content (Success)
   The server successfully processed the request, but is not returning any content."
  ([] (no-content nil))
  ([body]
   {:status 204
    :headers {}
    :body body}))

(def not-found "404 Not Found (ClientError)" ring-http-response/not-found)

(def method-not-allowed
  "405 Method Not Allowed (ClientError)"
  ring-http-response/method-not-allowed)

(def not-acceptable
  "406 Not Acceptable (ClientError)"
  ring-http-response/not-acceptable)

(def internal-server-error ring-http-response/internal-server-error)
