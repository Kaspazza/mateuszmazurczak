(ns mateuszmazurczak.config
  "LA environment configuration"
  (:require
   [mateuszmazurczak.configuration :as conf]))

(def config-path "env/la/config.edn")

(defn load-config [] (conf/read-config config-path {:env :la}))

(defn system-config [] (:system (load-config)))
