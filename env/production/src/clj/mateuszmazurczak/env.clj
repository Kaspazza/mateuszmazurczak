(ns mateuszmazurczak.env
  "Define prod specific behavior for customer app"
  (:require
   [ring.middleware.gzip :as ring-gzip]))

;; Redefined on purpose, as we are loading either dev or prod.
#_{:clj-kondo/ignore [:redefined-var]}
(def route (constantly []))

;; Redefined on purpose, as we are loading either dev or prod.
#_{:clj-kondo/ignore [:redefined-var]}
(def env-middlewares [ring-gzip/wrap-gzip])
