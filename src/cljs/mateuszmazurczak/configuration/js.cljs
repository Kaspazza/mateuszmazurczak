(ns mateuszmazurczak.configuration.js
  (:require
   [clojure.string :as str]))

(def js-var js/_envVars)

(defn keywordize
  "Change string to appropriate clojure keyword"
  [s]
  (-> (name s)
      str/lower-case
      (str/replace "_" "-")
      (str/replace "." "-")
      (keyword)))

(defn sanitize-map-keys
  "Changes all keywords in a map to appropriate clojure keys."
  [map]
  (reduce-kv (fn [acc key value]
               (let [new-key (if (keyword? key) (keywordize key) key)
                     new-val (if (map? value) (sanitize-map-keys value) value)]
                 (assoc acc new-key new-val)))
             {}
             map))

(defn- kw-to-js
  "Transform a keyword in a javascript compatible name"
  [k]
  (str "\"" (str/replace (name k) #"-" "_") "\""))

(defn mapToJSmap
  "Transform a map to a javascript map"
  [m]
  (str "{"
       (str/join ",\n"
                 (for [[k v] m]
                   (str (kw-to-js k)
                        ": "
                        (cond
                          (string? v) (str "\"" v "\"")
                          (keyword? v) (kw-to-js v)
                          (map? v) (mapToJSmap v)
                          (nil? v) 'null
                          :else v))))
       "}"))

(defn read-config [] (sanitize-map-keys (js->clj js-var :keywordize-keys true)))

(def configuration (memoize read-config))

(defn read-conf-param [key-path] (get-in (configuration) key-path))

(defn config-web-reference
  []
  (str "var " js-var " = " (mapToJSmap configuration)))
