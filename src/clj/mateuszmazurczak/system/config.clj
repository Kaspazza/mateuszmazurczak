(ns mateuszmazurczak.system.config
  (:require
   [aero.core]
   [clojure.java.io :as io]
   [integrant.core  :as ig]))

(defn- resolve-symbol
  [qualified-sym]
  (or (requiring-resolve qualified-sym)
      (throw (ex-info (str "Could not resolve symbol: " qualified-sym) {:symbol qualified-sym}))))

(defmethod aero.core/reader 'ig/ref [_ _ value] (ig/ref value))

(defmethod aero.core/reader 'var
  [_ _ value]
  (let [parsed (if (string? value) (symbol value) value)] (var-get (resolve-symbol parsed))))

(defmethod aero.core/reader 'var-ref
  [_ _ value]
  (let [parsed (if (string? value) (symbol value) value)] (resolve-symbol parsed)))

(defonce ^:private secrets-cache (atom nil))

(defn- load-secrets
  "Load .secrets.edn file, cached for performance"
  []
  (when (nil? @secrets-cache)
    (let [secrets-file ".secrets.edn"]
      (reset! secrets-cache (when (.exists (io/file secrets-file))
                              (aero.core/read-config secrets-file)))))
  @secrets-cache)

(defmethod aero.core/reader 'secrets
  [{:keys [env]} _ path]
  (when-let [secrets (load-secrets)]
    (let [env-profile (or env :development)] (get-in secrets (into [env-profile] path)))))

(defn read-config
  "Read config from file system or JAR resources"
  [path opts]
  (if-let [resource (io/resource path)]
    (aero.core/read-config resource opts)
    (aero.core/read-config path opts)))
