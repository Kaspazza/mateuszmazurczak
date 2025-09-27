(ns mateuszmazurczak.config
  "Production environment configuration"
  (:require
   [mateuszmazurczak.configuration :as conf]))

(def config-path "env/production/config.edn")

(defn load-config [] (conf/read-config config-path))

(defn system-config [] (:system (load-config)))