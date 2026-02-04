(ns mateuszmazurczak.utils.xml
  (:require
   [clojure.string :as str]))

(defn escape-xml
  [text]
  (-> text
      (str/replace "&" "&amp;")
      (str/replace "<" "&lt;")
      (str/replace ">" "&gt;")
      (str/replace "\"" "&quot;")
      (str/replace "'" "&apos;")))
