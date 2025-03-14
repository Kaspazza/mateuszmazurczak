(ns mateuszmazurczak.configuration
  "Configuration parameters, stored in configuration file.
   This namespace is the entry point to call conf.

  prn is used here instead of log and dependencies are limited as much as possible here on purpose to be able to use configuration everywhere."
  (:require
   #?(:clj [mateuszmazurczak.configuration.files :as conf-files]
      :cljs [mateuszmazurczak.utils.keywords :as mm-keyword])
   [clojure.string                             :as str]
   [mateuszmazurczak.configuration.environment :as conf-env]))

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

(def js-var
  #?(:clj "_envVars"
     :cljs js/_envVars))

(defn- read-config
  []
  #?(:clj (merge (conf-files/read-config) conf-env/config)
     :cljs (merge (mm-keyword/sanitize-map-keys
                   (js->clj js-var :keywordize-keys true))
                  conf-env/config)))

(def ^{:doc "A map of configuration variables."} conf (memoize read-config))

(defn read-conf-param [key-path] (get-in (conf) key-path))

(defn config [] (conf))

(defn read-param
  "Returns value under `key-path` vector."
  ([key-path default-value]
   (let [value (read-conf-param key-path)]
     (if (nil? value) default-value value)))
  ([key-path] (read-param key-path nil)))

(defn all-config
  "Returns whole configuration map, with all the keys and values."
  []
  (config))

(defn config-web-reference
  "Configuration variable that is used to save configuration in js code."
  []
  (str "var " js-var " = " (mapToJSmap (config))))
