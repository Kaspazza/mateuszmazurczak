(ns mateuszmazurczak.env
  "Define dev specific behavior for customer app"
  (:require
   [babashka.fs                       :as fs]
   [clojure.edn                       :as edn]
   [mateuszmazurczak.endpoint.handler :as mm-endpoint-handler]
   [ring.middleware.reload            :as mr]
   [ring.util.http-response           :as http-response]))

(def deps-edn "deps.edn")

(defn read-file
  "Read the file `target-filename`"
  [target-filename]
  (try (slurp target-filename)
       (catch Exception e
         (throw (ex-info "Impossible to load the file"
                         {:target-filename target-filename
                          :exception e})))))

(defn absolutize
  "Transform a file or dir name in an absolute path"
  [relative-path]
  (when relative-path (str (fs/absolutize relative-path))))

(defn read-edn
  "Read the `.edn` file,
  Params:
  * `edn-filename` name of the edn file to load
  * `loader-fn` (Optional, default: files/read-file) a function returning the content of the file

  Errors:
  * throws an exception if the file is not found
  * throws an exception if the file is a valid edn
  * `file` could be a string representing the name of the file to load
  or a (io/resource) object representing the name of the file to load"
  ([edn-filename loader-fn]
   (let [edn-filename (absolutize edn-filename)
         edn-content (try (loader-fn edn-filename) (catch Exception _ nil))]
     (try (edn/read-string edn-content) (catch Exception e nil))))
  ([edn-filename] (read-edn edn-filename read-file)))

(defn load-deps
  "Load the current project `deps.edn` files"
  []
  (read-edn deps-edn))

(defn extract-paths
  "Extracts the `:paths` and `:extra-paths` from a given `deps.edn`
   e.g. {:run {...}}
  Params:
  * `deps-edn` content the deps edn file to search extract path in
  * `excluded-aliases` is a collection of aliases to exclude"
  ([{:keys [paths aliases]
     :as _deps-edn}
    excluded-aliases]
   (let [selected-aliases (apply dissoc aliases excluded-aliases)
         alias-paths
         (mapcat (fn [[_alias-name paths]]
                   (apply concat
                          (vals (select-keys paths [:extra-paths :paths]))))
          selected-aliases)]
     (->> alias-paths
          (concat paths)
          sort
          dedupe
          (into []))))
  ([deps-edn] (extract-paths deps-edn #{})))

(def runnables
  "Listof classpath that should trigger a reload"
  (-> (load-deps)
      (extract-paths)))

(defn throw-route
  []
  ["/throw-exception"
   {:summary "Raise intentionally an exception"
    :get (fn [_request]
           (throw (ex-info "This exception is raised intentionally"
                           {:to "check"
                            :what "happens"})))}])

(defn portfolio-route
  ([] (portfolio-route "/js/compiled/share.js"))
  ([share-js]
   ["/portfolio"
    {:summary "Show portfolio"
     :get (fn [request]
            (-> (mm-endpoint-handler/build request
                                           [:div ""]
                                           [:script {:type "text/javascript"
                                                     :src share-js}]
                                           [:script
                                            {:type "text/javascript"
                                             :src "/js/compiled/portfolio.js"}])
                http-response/ok
                (assoc-in [:headers "content-type"]
                          "text/html;charset=utf8")))}]))

;; Redefined on purpose, as we are loading either dev or prod.
(defn route
  []
  ["/admin"
   {:summary "Admin subdir"}
   (throw-route)
   (portfolio-route "/js/compiled/mateuszmazurczak-share.js")])

(defn wrap-nocache
  "Dev wrapper to prevent caching of all requests, helpfull to run multiple app locally on the same port for instance"
  [handler]
  (fn [request]
    (-> request
        handler
        (assoc-in [:headers "Pragma"] "no-cache"))))

(defn wrap-reload
  "Reload clj as they are saved"
  [handler]
  (-> handler
      (mr/wrap-reload {:dirs runnables})))

;; Redefined on purpose, as we are loading either dev or prod.
(def env-middlewares [wrap-reload wrap-nocache])
