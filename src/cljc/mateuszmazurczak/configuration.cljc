(ns mateuszmazurczak.configuration
  "Configuration parameters, stored in configuration file.
   This namespace is the entry point to call conf.

  We use prn instead of log and try to limit outside dependencies as much as possible here on purpose to be able to use configuration everywhere.

  For a parameter `p`:
  * Create the parameter, in the current implementation, in the `util/conf.clj`
  * Read the parameter with  `conf/read-param`"
  (:require
   #?(:clj [mateuszmazurczak.configuration.files :as conf-files]
      :cljs [mateuszmazurczak.utils.keywords :as mm-keyword])
   [clojure.string :as str]))

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
  #?(:clj (conf-files/read-config)
     :cljs (mm-keyword/sanitize-map-keys
            (js->clj js-var :keywordize-keys true))))

(def ^{:doc "A map of configuration variables."} conf (memoize read-config))

(defn read-conf-param [key-path] (get-in (conf) key-path))

(defn config [] (conf))

(defn read-param
  "Returns value under `key-path` vector."
  ([key-path default-value]
   (let [value (read-conf-param key-path)]
     (if (nil? value)
       (do (prn "Value for " key-path
                " is not set, use default value" default-value)
           default-value)
       (do (prn "Read key-path " key-path " = " value) value))))
  ([key-path] (read-param key-path nil)))

(defn all-config
  "Returns whole configuration map, with all the keys and values."
  []
  (config))

(defn config-web-reference
  "Configuration variable that is used to save configuration in js code."
  []
  (str "var " js-var " = " (mapToJSmap (config))))
