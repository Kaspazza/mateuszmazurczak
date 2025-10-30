(ns mateuszmazurczak.file
  (:require
   [babashka.fs    :as fs]
   [clojure.edn    :as edn]
   [clojure.string :as str]))

(defn absolute?
  "Returns true if `path` is an absolute directory."
  [path]
  (= (str fs/file-separator) (str (first path))))

(defn remove-trailing-separator
  "If exists, remove the trailing separator in a path, remove unwanted spaces either"
  [path]
  (let [path (str/trim path)]
    (if (= (str fs/file-separator) (str (last path)))
      (->> (dec (count path))
           (subs path 0)
           remove-trailing-separator)
      path)))

(defn create-file-path
  "Creates a path for which each element of `dirs` is a subdirectory."
  [& dirs]
  (-> (if (some? dirs)
          (->> dirs
               (mapv str)
               (filter #(not (str/blank? %)))
               (mapv remove-trailing-separator)
               (interpose fs/file-separator)
               (apply str))
          "./")
      str))

(defn create-dir-path
  "Creates a path with the list of parameters.
  Removes the empty strings, add needed separators, including the trailing ones"
  [& dirs]
  (if (empty? dirs) "." (str (apply create-file-path dirs) fs/file-separator)))

(defn extract-path
  "Extract the directory path to the `filename`."
  [filename]
  (when-not (str/blank? filename)
    (if (or (fs/directory? filename) (= fs/file-separator (str (last filename))))
      filename
      (let [filepath (->> filename
                          fs/components
                          butlast
                          (mapv str))]
        (cond
          (= [] filepath) ""
          (absolute? filename) (apply create-dir-path fs/file-separator filepath)
          :else (apply create-dir-path filepath))))))

(defn read-file
  "Read the file named `filename`.

   Returns:

  * `filename`
  * `raw-content` if file can be read.
  * `invalid?` to `true` whatever why.
  * `exception` if something wrong happened."
  [filename]
  (let [filename (str filename)]
    (try {:filename filename
          :dir (extract-path filename)
          :raw-content (slurp filename)}
         (catch Exception e
           {:filename filename
            :exception e
            :invalid? true}))))

(defn read-edn
  "Read file which name is `edn-filename`.

  Returns:

  * `filename`
  * `raw-content` if file can be read.
  * `invalid?` to `true` whatever why.
  * `exception` if something wrong happened.
  * `edn` if the translation."
  [edn-filename]
  (let [{:keys [raw-content invalid?]
         :as res}
        (read-file edn-filename)]
    (if invalid?
      res
      (try (assoc res :edn (edn/read-string raw-content))
           (catch Exception e (assoc res :exception e :invalid? true))))))
