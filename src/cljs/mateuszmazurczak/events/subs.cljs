(ns mateuszmazurczak.events.subs
  "Reframe subscriptions for mateuszmazurczak project"
  (:require
   [automaton-web.events-proxy :as web-events-proxy]))

(web-events-proxy/reg-sub ::route-match (fn [db _] (:route-match db)))
