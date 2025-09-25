(ns mateuszmazurczak.configuration
  (:require
   [aero.core]
   [integrant.core :as ig]))

(defn- resolve-symbol
  [qualified-sym]
  (or (requiring-resolve qualified-sym)
      (throw (ex-info (str "Could not resolve symbol: " qualified-sym)
                      {:symbol qualified-sym}))))

(defmethod aero.core/reader 'ig/ref [_ _ value] (ig/ref value))

(defmethod aero.core/reader 'var
  [_ _ value]
  (let [parsed (if (string? value) (symbol value) value)]
    (var-get (resolve-symbol parsed))))

(defmethod aero.core/reader 'var-ref
  [_ _ value]
  (let [parsed (if (string? value) (symbol value) value)]
    (resolve-symbol parsed)))


(defn read-config [path] (aero.core/read-config path))
