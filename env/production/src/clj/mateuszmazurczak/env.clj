(ns mateuszmazurczak.env
  "Define prod specific behavior for customer app"
  (:require
   [automaton-core.log       :as core-log]
   [automaton-web.middleware :as web-middleware]))

(core-log/info "The production environment has been loaded")

;; Redefined on purpose, as we are loading either dev or prod.
#_{:clj-kondo/ignore [:redefined-var]}
(def route (constantly []))

;; Redefined on purpose, as we are loading either dev or prod.
#_{:clj-kondo/ignore [:redefined-var]}
(def env-middlewares [web-middleware/wrap-gzip])
