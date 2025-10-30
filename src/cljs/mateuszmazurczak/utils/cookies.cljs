(ns mateuszmazurczak.utils.cookies
  "Adapter to store a cookie on the frontend"
  (:require
   [clojure.string   :as str]
   [goog.net.Cookies :as gnc]))

(defn parse-cookie
  "Parse the content of the cookie
  Params:
  * `cookie-text` is the cookie text content to transform in data structure"
  [cookie-text]
  (let [cookie-data (when (and cookie-text (not= "" cookie-text))
                      (into {}
                            (map (fn [line]
                                   (vec (let [res (->> (str/split line #"=")
                                                       (map str/trim))]
                                          (case (count res)
                                            1 [res nil]
                                            2 res
                                            [(first res) (vec (rest res))]))))
                                 (str/split cookie-text #";"))))]
    cookie-data))

(defn ^:export set-cookie
  "Set the salue of the cookie
  Params:
  * `k` is the key, transformed to a str, so keyword are transformed
  * `v` is the value associated to k
  * `time` is the timestamp of that value
  * `path` path for which that cookie is sent. It and its subdomains"
  ([k v time path] (.set (gnc/getInstance) (str k) v time path))
  ([k v time] (.set (gnc/getInstance) (str k) v time "/"))
  ([k v] (.set (gnc/getInstance) (str k) v -1)))

(defn get-cookie "Get the the cookie map of data" [] (parse-cookie (str (.-cookie js/document))))

(defn get-cookie-val
  "Get the value of a key
  Params:
  * `key` the key which value should be returned, is a keyword to be mapped in a string"
  [key]
  (get (get-cookie) (str key)))
