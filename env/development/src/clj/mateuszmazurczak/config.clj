(ns mateuszmazurczak.config
  "Development environment configuration"
  (:require
   [mateuszmazurczak.system.config :as conf]))

(def config-path "env/development/config.edn")

(defn load-config [] (conf/read-config config-path {:env :development}))

(defn system-config [] (:system (load-config)))
