(ns mateuszmazurczak.navigation.history-fx
  (:require
   [automaton-core.log                  :as core-log]
   [automaton-web.events-proxy          :as web-events-proxy]
   [automaton-web.fe.history            :as web-fe-history]
   [mateuszmazurczak.navigation.history :as mm-fe-history]))

(web-events-proxy/reg-fx ::history-change
                         (fn [[href]]
                           (core-log/trace "History change to `" href "`")
                           (web-fe-history/navigate! @mm-fe-history/history
                                                     href)))
