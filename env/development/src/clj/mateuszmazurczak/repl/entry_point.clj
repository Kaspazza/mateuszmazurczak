(ns mateuszmazurczak.repl.entry-point
  "REPL entry point"
  (:require
   [babashka.fs                    :as fs]
   [clojure.string                 :as str]
   [mateuszmazurczak.configuration :as conf]
   [mateuszmazurczak.core          :as mateuszmazurczak-core]
   [nrepl.server                   :refer
                                   [default-handler start-server stop-server]]
   [portal.api                     :as p])
  (:gen-class))

(def directory-separator
  "Symbol to separate directories.
  Is usually `/` on linux based OS And `\\` on windows based ones"
  fs/file-separator)

(defn absolutize
  "Transform a file or dir name in an absolute path"
  [relative-path]
  (when relative-path (str (fs/absolutize relative-path))))

(defn hidden? "Return true if the path is hidden" [path] (fs/hidden? path))

(defn delete-files
  "Deletes the files which are given in the list.
  They could be regular files or directory, when so the whole subtreee will be removed"
  [file-list]
  (doseq [file file-list]
    (if (fs/directory? file) (fs/delete-tree file) (fs/delete-if-exists file))))

(defn directory-exists?
  "Check directory existance"
  [directory-path]
  (and (fs/exists? directory-path) (fs/directory? directory-path)))

(defn is-existing-file?
  "Check if this the path exist and is not a directory"
  [path]
  (and (fs/exists? path) (not (fs/directory? path))))

(defn is-existing-dir?
  "Check if this the path exist and is a directory"
  [path]
  (and (fs/exists? path) (fs/directory? path)))

(defn remove-trailing-separator
  "If exists, remove the trailing separator in a path, remove unwanted spaces either"
  [path]
  (when-not (str/blank? path)
    (when-let [path (str/trim path)]
      (str/replace path (re-pattern (str directory-separator "*$")) ""))))

(defn create-file-path
  "Creates a path with the list of parameters.
  Removes the empty strings, add needed separators"
  [& dirs]
  (if (some? dirs)
    (->> dirs
         (map str)
         (filter #(not (str/blank? %)))
         (map remove-trailing-separator)
         (interpose directory-separator)
         (apply str))
    "."))

(defn create-dir-path
  "Creates a path with the list of parameters.
  Removes the empty strings, add needed separators, including the trailing ones"
  [& dirs]
  (when-let [file-path (apply create-file-path dirs)]
    (str file-path directory-separator)))

(defn search-files
  "Search files.
  * `root` is where the root directory of the search-files
  * `pattern` is a regular expression as described in [java doc](https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String))
  * `options` (Optional, default = {}) are boolean value for `:hidden`, `:recursive` and `:follow-lins`. See [babashka fs](https://github.com/babashka/fs/blob/master/API.md#glob) for details.
  For instance:
  * `(search-files \"\" \"**{.clj,.cljs,.cljc,.edn}\")` search all clj files in pwd directory"
  ([root pattern options]
   (if (not (directory-exists? root))
     nil
     (->> options
          (merge {:hidden true
                  :recursive true
                  :follow-links true})
          (fs/glob root pattern)
          (map str)
          (into []))))
  ([root pattern] (search-files root pattern {})))

(defn file-in-same-dir
  "Use the relative-name to create in file in the same directory than source-file"
  [source-file relative-name]
  (let [source-subdirs (fs/components source-file)
        subdirs (mapv str
                      (if (fs/directory? source-file)
                        source-subdirs
                        (butlast source-subdirs)))
        new-name (conj subdirs relative-name)]
    (apply create-file-path new-name)))

(defn extract-path
  "Extract if the filename is a file, return the path that contains it,
  otherwise return the path itself"
  [filename]
  (when-not (str/blank? filename)
    (if (fs/directory? filename)
      filename
      (str (when (= (str directory-separator) (str (first filename)))
             directory-separator)
           (->> filename
                fs/components
                butlast
                (map str)
                (apply create-dir-path))))))

(defn rename-file
  "Rename a file `src` to destination file `dst`"
  [src dst]
  (try (-> dst
           extract-path
           fs/create-dirs)
       (fs/move src (fs/path dst))
       (catch Exception e
         (throw (ex-info "Impossible to rename the file"
                         {:src (absolutize src)
                          :dst (absolutize dst)
                          :exception e})))))

(defn rename-dir
  "Rename a dir"
  [src dst]
  (try (fs/move src (fs/path dst))
       (catch Exception e
         (throw (ex-info "Impossible to rename the directory"
                         {:src (absolutize src)
                          :dst (absolutize dst)
                          :exception e})))))

(defn remove-file
  "Remove the file `filename`"
  [filename]
  (try (fs/delete-if-exists filename)
       (catch Exception e
         (throw (ex-info "Impossible to remove the file"
                         {:filename filename
                          :exception e})))))

(defn read-file
  "Read the file `target-filename`"
  [target-filename]
  (try (slurp target-filename)
       (catch Exception e
         (throw (ex-info "Impossible to load the file"
                         {:target-filename target-filename
                          :exception e})))))

(defn file-ized
  "Transform a name, like a namespace name or application name, in a directory compatible names"
  [namespace]
  (str/replace namespace #"-" "_"))

(defn add-suffix
  "Add a suffix of the filename before the extension"
  [filename suffix]
  (let [[_ prefix extension] (re-find #"(.*)(\..*)" filename)]
    (str/join [prefix suffix extension])))

(defn- rename-recursively-attempt
  "Make one attempt for file renaming.
  Search for all files matching target-dir and file-pattern
  Will stop at the first modification
  Return modification? telling if at least one renaming has been done"
  [target-dir file-filter pattern pattern-replacement]
  (loop [files (search-files target-dir file-filter)
         modification? false]
    (if (empty? files)
      modification?
      (let [filename (str (first files))
            new-filename (str/replace filename pattern pattern-replacement)]
        (if (= new-filename filename)
          (recur (rest files) modification?)
          (cond
            (is-existing-file? filename) (do (rename-file filename new-filename)
                                             (recur (rest files) true))
            (is-existing-dir? filename) (do (rename-dir filename new-filename)
                                            (recur (rest files) true))
            :else (recur (rest files) modification?)))))))

(defn rename-recursively
  "Search recursively all sub-dirs to be renamed from `template-app` in the `target-dir` directory
  * `target-dir` is the root directory of the searched files
  * `file-filter` filter for files, [according to syntax in crate regexp](https://docs.rs/regex/1.9.1/regex/#syntax).
  * `pattern` is to find content in the namespace, for instance, as seen in [java pattern](https://docs.oracle.com/javase/10/docs/api/java/util/regex/Pattern.html), e.g. #\"foo(.*)bar\"
  * `pattern-replacement` is what to replace, according to the [replace specification](https://clojuredocs.org/clojure.string/replace). e.g. \"foo_$1_bar\" "
  [target-dir file-filter pattern pattern-replacement]
  (let [pattern (remove-trailing-separator (str pattern))
        pattern-replacement (remove-trailing-separator (str
                                                        pattern-replacement))]
    (loop [iterations-left 30]
      (if (> iterations-left 0)
        (when (rename-recursively-attempt target-dir
                                          file-filter
                                          pattern
                                          pattern-replacement)
          (recur (dec iterations-left)))
        nil))))

(defn delete-files-starting-with
  "Remove all files matching:
  * `files-map` where the files are searched
  * `starting-regexp` is a regular expression matching the first line of the file "
  [files-map starting-regexp]
  (doseq [[filename file-content] files-map]
    (let [first-line (-> (str/split-lines file-content)
                         first)]
      (when (re-find starting-regexp first-line) (remove-file filename)))))

(defn write-file
  "Spit the file, the directory where to store the file is created if necessary
  * `filename` is the name of the file to write, could be absolute or relative
  * `content` is the content to store there"
  [filename content]
  (try (let [filepath (extract-path filename)]
         (fs/create-dirs filepath)
         (spit filename content))
       (catch Exception _e nil)))

(defn ensure-directory-exists
  "If directory doesn't exist, create it.
  Params:
  * `dir` directory to check"
  [dir]
  (try (when-not (directory-exists? dir) (fs/create-dirs (extract-path dir)))
       dir
       (catch Exception _ nil)))

(defn create-temp-dir
  "Creates a temporary directory managed by the system
  Params:
  * `sub-dirs` is an optional list of strings, each one is a sub directory
  Returns the string of the directory path"
  [& sub-dirs]
  (let [tmp-dir (apply create-dir-path
                       (-> (fs/create-temp-dir)
                           str)
                       sub-dirs
                       directory-separator)]
    (ensure-directory-exists tmp-dir)))

(defn filter-existing-dir
  "Filter only existing dirs
  Params:
  * `dirs` sequence of string of directories"
  [dirs]
  (apply vector
         (mapcat (fn [sub-dir]
                   (let [sub-dir-rpath (absolutize sub-dir)]
                     (when (directory-exists? sub-dir-rpath) [sub-dir-rpath])))
          dirs)))

(defn empty-path?
  "Is the directory empty
  Params:
  * `dir` the directory to search in"
  [dir]
  (boolean (and (fs/directory? dir) (empty? (fs/list-dir dir)))))

(defn file-name
  "Return the file name without the path"
  [path]
  (fs/file-name path))

(defn relativize
  "Turn the `path` into a relative directory starting from `root-dir`"
  [path root-dir]
  (let [path (-> path
                 remove-trailing-separator
                 absolutize)
        root-dir (-> root-dir
                     remove-trailing-separator
                     absolutize)]
    (when-not (str/blank? root-dir)
      (->> path
           (fs/relativize root-dir)
           str))))


(defn default-port
  "Port where the server is started"
  []
  (conf/read-param [:dev :portal-port] 8351))

(defn app-name
  "Application name as displayed in the portal"
  []
  (conf/read-param [:app-name] "Non defined"))

(def ^:private submit "Sumbitting data to the portal" #'p/submit)

(defn- portal-connect "Regular portal add-tap fn proxy." [] (add-tap #'submit))

(defn portal-start
  "Starts portal app
   Params:
   * port (optional) defaults to `default-port`, defines what port portal should be started."
  ([] (portal-start (default-port)))
  ([port]
   (let [res (p/open {:port port})]
     (portal-connect)
     (tap> (format "Portal server has started for app `%s` on port %d"
                   (app-name)
                   port))
     res)))

(defn portal-stop
  "Close portal app"
  ([] (p/clear) (p/close))
  ([portal-server] (p/clear) (p/close portal-server)))

(defn require-ns
  "Require the namespace of the body-fn
  Params:
  * `f` is a function full qualified symbol. It could be a string"
  [f]
  (some-> f
          symbol
          namespace
          symbol
          require))

(defn try-require [ns] (try (require-ns ns) ns (catch Exception _ nil)))

(defn- force-option?
  [args]
  (and args
       (seq args)
       (filter some? (map #(contains? #{"-f" "--force"} %) args))))

(defn default-middleware
  []
  (let [cider-middlewares (try-require 'cider.nrepl/cider-middleware)
        nrepl-middleware (try-require 'refactor-nrepl.middleware/wrap-refactor)]
    (cond-> []
      cider-middlewares (concat @(resolve cider-middlewares))
      nrepl-middleware (conj nrepl-middleware)
      true vec)))
(def nrepl-port-filename "Name of the `.nrepl-port` file" ".nrepl-port")

(def repl "Store the repl instance in the atom" (atom {}))

(defn get-nrepl-port-parameter [] (conf/read-param [:dev :clj-nrepl-port] 8000))

(defn get-active-nrepl-port
  "Retrieve the nrepl port, available for REPL"
  []
  (:nrepl-port @repl))

(defn- stop-repl
  "Stop the repl"
  []
  (stop-server (:repl @repl))
  (reset! repl {}))

(defn create-nrepl-files
  "Consider all deps.edn files as the root of a clojure project and creates a .nrepl-port file next to it"
  [repl-port]
  (let [build-configs (search-files "" "**build_config.edn")
        nrepl-ports (map #(file-in-same-dir % nrepl-port-filename)
                         build-configs)]
    (doseq [nrepl-port nrepl-ports] (write-file nrepl-port (str repl-port)))))


(defn- start-repl*
  [middlewares]
  (let [repl-port (get-nrepl-port-parameter)]
    (create-nrepl-files repl-port)
    (reset! repl {:nrepl-port repl-port
                  :repl (do (prn "-> Repl port is available on: " repl-port)
                            (start-server :port repl-port
                                          :handler (apply default-handler
                                                          middlewares)))})
    (portal-start)
    (.addShutdownHook
     (Runtime/getRuntime)
     (Thread. #(do
                 (prn "SHUTDOWN in progress, stop repl on port `" repl-port "`")
                 (shutdown-agents)
                 (stop-repl)
                 (-> (search-files "" (str "**" nrepl-port-filename))
                     (delete-files))
                 (portal-stop)
                 (println "SHUTDOWN ends successfully"))))))

(defn start-repl
  "Start repl, setup and catch errors
  Params:
  * `mdws` List of middlewares"
  [args mdws main-fn]
  (try (when-not (force-option? args) (main-fn))
       (start-repl* mdws)
       :started
       (catch Exception e
         (ex-info "Failed to start, relaunch with -force option" {:error e})
         nil)))

(defn -main
  "Main entry point for repl"
  [& args]
  (start-repl args (default-middleware) mateuszmazurczak-core/-main))
