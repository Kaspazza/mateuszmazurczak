(ns mateuszmazurczak.events.db
  "Default value for front end state"
  (:require
   [day8.re-frame.tracing           :refer-macros [fn-traced]]
   [mateuszmazurczak.i18n.translate :as mm-i18n-translate]
   [re-frame.core                   :as rf]))

(def default-db
  "Default value for front end state"
  {:name "mateuszmazurczak"
   :route-match :pending
   :lang (mm-i18n-translate/init-lang)})

(rf/reg-event-db ::initialize-db
                 (fn-traced [_ _]
                            ;; Intentionally not using
                            ;; previous value of db, as it is
                            ;; an init
                            default-db))
