(ns mateuszmazurczak.i18n.events
  (:require
   [automaton-web.events-proxy             :as web-events-proxy]
   [automaton-web.events.fx                :as web-events-fx]
   [day8.re-frame.tracing                  :refer-macros [fn-traced]]
   [mateuszmazurczak.i18n.language         :as mm-language]
   [mateuszmazurczak.navigation.history    :as mm-fe-history]
   [mateuszmazurczak.navigation.history-fx :as mm-fe-fx]))

(web-events-proxy/reg-event-fx
 ::change-lang
 (fn-traced
  [{:keys [db]} [_ lang-evt]]
  (let [lang (-> lang-evt
                 .-target
                 .-value
                 mm-language/ui-str-to-id)]
    {:db (assoc db :lang lang)
     ::web-events-fx/set-cookie ["lang" lang]
     ::mm-fe-fx/history-change
     [(mm-fe-history/href-delta (:route-match db) nil nil {:lang lang})]})))
