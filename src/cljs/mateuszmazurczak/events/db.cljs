(ns mateuszmazurczak.events.db
  "Default value for front end state"
  (:require
   [mateuszmazurczak.i18n.fe.translate :as mateuszmazurczak-fe-translate]))

(def default-db
  "Default value for front end state"
  {:name "mateuszmazurczak"
   :route-match :pending
   :lang (mateuszmazurczak-fe-translate/init-lang)})
