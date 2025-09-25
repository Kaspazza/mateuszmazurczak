(ns mateuszmazurczak.config
  "Development environment configuration"
  (:require
   [mateuszmazurczak.configuration :as conf]))

(def config-path "env/development/config.edn")

(defn load-config [] (conf/read-config config-path))

(defn system-config [] (:system (load-config)))