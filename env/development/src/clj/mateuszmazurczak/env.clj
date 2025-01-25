(ns mateuszmazurczak.env
  "Define dev specific behavior for customer app"
  (:require
   [automaton-core.log                          :as core-log]
   [automaton-web-dev.endpoint.web.dev-handlers :as dev-handlers]
   [automaton-web.pages.portfolio.route         :as web-page-portfolio-route]
   [automaton-web.pages.throw-exception.route
    :as web-pages-throw-exception-route]))

(core-log/info "The development environment has been loaded")

;; Redefined on purpose, as we are loading either dev or prod.
(defn route
  []
  ["/admin"
   {:summary "Admin subdir"}
   (web-pages-throw-exception-route/route)
   (web-page-portfolio-route/route "/js/compiled/mateuszmazurczak-share.js")])

;; Redefined on purpose, as we are loading either dev or prod.
(def env-middlewares dev-handlers/middlewares)
