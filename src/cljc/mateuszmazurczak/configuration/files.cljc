(ns mateuszmazurczak.configuration.files
  "Namespace for simple configuration based on local file.
   Log is not used and outside dependencies limited to comply with the configuration requirements."
  (:require
   #?@(:clj [[clojure.edn :as edn] [clojure.java.io :as io]]
       :cljs [[cljs.reader]])
   [clojure.string                  :as str]
   [mateuszmazurczak.utils.keywords :as core-keywords]
   [mateuszmazurczak.utils.map      :as utils-map]))

#?(:clj (defn get-java-properties
          "Get the java properties"
          []
          (into {} (System/getProperties))))

#?(:clj
     (defn get-java-property
       "Get a java property named `property-name`, if not found return `default-value`"
       ([property-name default-value]
        (or (System/getProperty property-name) default-value))
       ([property-name] (System/getProperty property-name))))

(defn split-property-value
  "Returns a collection of values from `property-value` splitted by ','."
  [property-value]
  (str/split property-value #","))

#?(:cljs (def ^:private nodejs? (exists? js/require)))
#?(:cljs (def ^:private fs (when nodejs? (js/require "fs"))))

(defn slurp-file
  [f]
  (try #?(:clj (when-let [file (or (io/resource f) (io/file f))] (slurp file))
          :cljs (when ^js (.existsSync fs f) (str ^js (.readFileSync fs f))))
       (catch #?(:clj Exception
                 :cljs js/Error)
         _
         (println (str "Reading file " f " failed")))))

(defn read-config-file
  "Reads config file, on purpose fn defined here to keep dependencies as small as possible."
  [f]
  (when-let [content (slurp-file f)]
    (into {}
          (core-keywords/sanitize-map-keys #?(:clj (edn/read-string content)
                                              :cljs (cljs.reader/read-string
                                                     content))))))

(def config-file
  ;; conf-var can be added e.g. in deps.edn alias like:
  ;;    :jvm-opts ["-Dconf-var=env/test/config.edn,env/other_file.edn"]
  #?(:clj "conf-var"
     :cljs "config.edn"))

#?(:clj (defn property->config-files
          [property-name]
          (some-> property-name
                  get-java-property
                  split-property-value)))

(defn- warn-on-overwrite
  [ms]
  (let [kseq (reduce (fn [acc m] (concat acc (keys m))) [] ms)]
    (for [[id freq] (frequencies kseq)
          :when (> freq 1)]
      (println "WARNING: configuration keys are duplicated for:" id))))

(defn merge-configs [& m] (warn-on-overwrite m) (apply utils-map/deep-merge m))

(defn read-config
  "Reads configuration, currently it's based on config.edn file. On js part, if nodejs is not available to get the configuration from file. If used with js, config-js-reference variable is expected to be set publicly."
  []
  #?(:clj (->> config-file
               property->config-files
               (mapv read-config-file)
               (filterv some?)
               (apply merge-configs))
     :cljs (if nodejs? (read-config-file config-file) {})))

(def config (read-config))

(defn read-conf-param [key-path] (get-in config key-path))
