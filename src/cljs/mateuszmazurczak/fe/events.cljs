(ns mateuszmazurczak.fe.events
  "Mateuszmazurczak page UI events
  Remember some events are defined in `automaton-web.events.subs`"
  (:require
   [automaton-web.events-proxy     :as web-events-proxy]
   [automaton-web.events.fx        :as web-events-fx]
   [day8.re-frame.tracing          :refer-macros [fn-traced]]
   [mateuszmazurczak.events.db     :as mateuszmazurczak-db-events]
   [mateuszmazurczak.fe.fx         :as mateuszmazurczak-fe-fx]
   [mateuszmazurczak.fe.history    :as mateuszmazurczak-fe-history]
   [mateuszmazurczak.i18n.language :as mateuszmazurczak-language]))

(web-events-proxy/reg-event-db ::initialize-db
                               (fn-traced
                                [_ _]
                                ;; Intentionally not using
                                ;; previous value of db, as it is
                                ;; an init
                                mateuszmazurczak-db-events/default-db))

(web-events-proxy/reg-event-fx
 ::change-lang
 (fn-traced [{:keys [db]} [_ lang-evt]]
            (let [lang (-> lang-evt
                           .-target
                           .-value
                           mateuszmazurczak-language/ui-str-to-id)]
              {:db (assoc db :lang lang)
               ::web-events-fx/set-cookie ["lang" lang]
               ::mateuszmazurczak-fe-fx/history-change
               [(mateuszmazurczak-fe-history/href-delta (:route-match db)
                                                        nil
                                                        nil
                                                        {:lang lang})]})))

(defn client-app-db-init!
  "To be called during cust-app init to init the re-frame"
  []
  (web-events-proxy/client-app-db-init! ::initialize-db))
