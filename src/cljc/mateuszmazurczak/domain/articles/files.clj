(ns mateuszmazurczak.domain.articles.files
  (:require
   [clojure.edn :as edn]))

(defmacro read-file [file] (edn/read-string (clojure.core/slurp file)))
