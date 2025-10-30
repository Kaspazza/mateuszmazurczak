(ns mateuszmazurczak.config
  "Production environment configuration"
  (:require
   [mateuszmazurczak.system.config :as conf]))

(def config-path "config.edn")

(defn load-config [] (conf/read-config config-path {:env :production}))

(defn system-config [] (:system (load-config)))
