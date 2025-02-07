(ns mateuszmazurczak.env
  "Define prod specific behavior for customer app"
  (:require
   [ring.middleware.gzip :as ring-gzip]))

;; Redefined on purpose, as we are loading either dev or prod.
(def route (constantly []))

;; Redefined on purpose, as we are loading either dev or prod.
(def env-middlewares [ring-gzip/wrap-gzip])
