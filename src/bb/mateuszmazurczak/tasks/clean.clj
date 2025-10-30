(ns mateuszmazurczak.tasks.clean
  (:require
   [babashka.fs                   :as fs]
   [clojure.pprint                :as pp]
   [clojure.string                :as str]
   [mateuszmazurczak.cli-opts     :as build-cli-opts]
   [mateuszmazurczak.echo.headers :refer [h1-error! h1-valid! normalln]]))

(defn is-existing-path?
  "Returns true if `filename` path already exist."
  [path]
  (when-not (str/blank? path) (when (fs/exists? path) path)))

(defn is-existing-file?
  "Returns true if `filename` path already exist and is not a directory."
  [filename]
  (when (and (is-existing-path? filename) (not (fs/directory? filename))) filename))

(defn is-existing-dir?
  "Check if this the path exist and is a directory."
  [dirname]
  (when (and (is-existing-path? dirname) (fs/directory? dirname)) dirname))

(defn delete-file
  "Deletes `filename` and returns it.
   If `filename` does not exist, returns nil."
  [filename]
  (when (is-existing-file? filename) (fs/delete filename) filename))

(defn delete-dir
  "Deletes `dir` and returns it.
   If `dir` does not exist, returns nil"
  [dir]
  (when (is-existing-dir? dir) (fs/delete-tree dir) dir))

(defn delete-path
  "Deletes `path` and returns it.
   Returns nil if the `path` does not exists."
  [path]
  (if (fs/directory? path) (delete-dir path) (delete-file path)))

(def cli-opts
  (-> []
      (concat build-cli-opts/help-options build-cli-opts/verbose-options)
      build-cli-opts/parse-cli))

(def verbose (get-in cli-opts [:options :verbose]))

(defn clean
  "Deletes the files which are given in the list.
  They could be regular files or directory, when so the whole subtreee will be removed"
  [file-list]
  (normalln (str "Starting removal of cache: \n" (with-out-str (pp/pprint file-list))))
  (try
    (let [removed-files (mapv (fn [file]
                                (let [res (delete-path file)]
                                  (when (not (or (= res file) (nil? res))) {file res})))
                              file-list)]
      (if (every? nil? removed-files)
        (h1-valid! "Cache cleaned")
        (h1-error! "Removal failed for: " (with-out-str (pp/pprint (remove nil?))) removed-files)))
    (catch Exception e (h1-error! "There was a problem while removing files" e))))
