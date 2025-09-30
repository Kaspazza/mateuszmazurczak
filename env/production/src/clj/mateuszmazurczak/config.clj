(ns mateuszmazurczak.config
  "Production environment configuration"
  (:require
   [mateuszmazurczak.configuration :as conf]))

(def config-path "config.edn")

(defn load-config [] (conf/read-config config-path {:env :production}))

(defn system-config [] (:system (load-config)))
