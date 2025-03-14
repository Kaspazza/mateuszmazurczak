(ns mateuszmazurczak.configuration.environment
  "Get environment data stored in the configuration"
  (:require
   #?@(:clj [[clojure.edn :as edn]]
       :cljs [[cljs.reader] [goog.object :as obj]])
   [clojure.string :as str]))

#?(:cljs (def ^:private nodejs? (exists? js/require)))

#?(:cljs (def ^:private process (when nodejs? (js/require "process"))))

(defn keywordize
  "Change string to appropriate clojure keyword"
  [s]
  (-> (name s)
      str/lower-case
      (str/replace "_" "-")
      (str/replace "." "-")
      (keyword)))

(defn env-key-path
  "Turns key-path ([:a :b :c] -> 'a-b-c') into environment type key."
  [key-path]
  (let [path-str (str/join "-" (map name key-path))]
    (when-not (str/blank? path-str) (keywordize path-str))))

(defn parse-number
  [^String v]
  (try #?(:clj (Long/parseLong v)
          :cljs (parse-long v))
       #?(:clj (catch NumberFormatException _ (BigInteger. v)))
       (catch #?(:clj Exception
                 :cljs js/Error)
         _
         v)))

(defn parse-system-env
  "Turns string type into number. In case of failure in parsing it's returned in a format as it was (a string)."
  [v]
  (cond
    (re-matches #"[0-9]+" v) (parse-number v)
    (re-matches #"^(true|false)$" v) #?(:clj (Boolean/parseBoolean v)
                                        :cljs (parse-boolean v))
    (re-matches #"\w+" v) v
    :else (try (let [f #?(:clj edn/read-string
                          :cljs cljs.reader/read-string)
                     parsed (f v)]
                 (if (symbol? parsed) v parsed))
               (catch #?(:clj Exception
                         :cljs js/Error)
                 _
                 v))))

(defn read-all
  "Reads all system env properties and converts to appropriate type."
  []
  (->> #?(:clj (System/getenv)
          :cljs (if process
                  (let [env (.-env process)]
                    (zipmap (obj/getKeys env) (obj/getValues env)))
                  {}))
       (map (fn [[k v]] [(keywordize k) v]))
       (into {})))

(def config (memoize read-all))

(defn read-conf-param [config key-path] (get config (env-key-path key-path)))
