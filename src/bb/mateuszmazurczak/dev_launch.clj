(ns mateuszmazurczak.dev-launch
  "Task for starting a local development environment"
  (:require
   [clojure.string                :as str]
   [mateuszmazurczak.cli-opts     :as cli-opts]
   [mateuszmazurczak.echo.actions :refer [action errorln exceptionln normalln]]
   [mateuszmazurczak.echo.cmds    :refer [blocking-cmd long-living-cmd]]
   [mateuszmazurczak.tasks.css    :as css]
   [mateuszmazurczak.tasks.shadow :as shadow]))

(def ^:private cli-opts
  (-> [["-r" "--repl" "Don't start the clj REPL" :default true :parse-fn not]
       ["-f" "--frontend" "Don't start the cljs REPL" :default true :parse-fn not]
       ["-c" "--css" "Don't start the css" :default true :parse-fn not]]
      (concat cli-opts/help-options cli-opts/verbose-options cli-opts/inverse-options)
      cli-opts/parse-cli
      (cli-opts/inverse [:repl :mermaid :frontend :css])))

(def verbose (get-in cli-opts [:options :verbose]))

(defn start-repl
  "Start the REPL."
  [repl-aliases]
  (let [prefixs ["repl"]
        app-dir ""
        action (partial action prefixs)
        errorln (partial errorln prefixs)
        exceptionln (partial exceptionln prefixs)
        long-living-cmd (partial long-living-cmd prefixs)]
    (try (when (get-in cli-opts [:options :repl])
           (if (every? keyword? repl-aliases)
             (let [cmd ["clojure" (apply str "-M" repl-aliases)]]
               (action "Start the clojure REPL.")
               (long-living-cmd cmd app-dir 100 verbose (constantly true) (constantly true))
               (errorln "REPL has stopped."))
             (errorln "REPL can't start - aliases are not valid.")))
         (catch Exception e
           (if (str/includes? (pr-str e) "Address already in use")
             (do (errorln "A REPL is still running.")
                 (normalln "Execute" "prgrep java" "to know what process to kill."))
             (do (errorln "Unexpected error during execution of REPL: ") (exceptionln e)))))))

(defn start-db
  "Starts local db"
  []
  (try (let [cmd ["dpm" "up"]
             prefixs ["db"]
             app-dir ""]
         (long-living-cmd prefixs cmd app-dir 100 verbose (constantly true) (constantly true)))
       (catch Exception e
         (println "Unexpected error during execution of db start")
         (println (pr-str e)))))

(defn css-watch
  "Watch the css modificatoin with tailwind."
  []
  (try (when (get-in cli-opts [:options :css])
         (let [prefixs ["css"]
               app-dir ""
               action (partial action prefixs)
               long-living-cmd (partial long-living-cmd prefixs)]
           (action "Watch css modifications.")
           (let [cmd (css/tailwind-watch-cmd "resources/css/main.css"
                                             "resources/public/css/compiled/styles.css")]
             (long-living-cmd cmd app-dir 100 verbose (constantly true) (constantly true)))))
       (catch Exception e
         (println "Unexpected error during execution of css watch")
         (println (pr-str e)))))

(defn fe-watch
  "Watch the front end."
  [run-aliases]
  (try (when (get-in cli-opts [:options :frontend])
         (let [prefixs ["fe"]
               app-dir ""
               errorln (partial errorln prefixs)
               action (partial action prefixs)
               blocking-cmd (partial blocking-cmd prefixs)
               long-living-cmd (partial long-living-cmd prefixs)]
           (action "Install frontend deps.")
           (blocking-cmd (shadow/install-cmd)
                         app-dir
                         "Install frontend deps has stopped unexpectedly:"
                         verbose)
           (if (every? keyword? run-aliases)
             (do (action "Watch frontend aliases and start REPL:" run-aliases)
                 (-> (mapv name run-aliases)
                     shadow/cljs-watch-cmd
                     (long-living-cmd app-dir 100 verbose (constantly true) (constantly true))))
             (errorln "REPL can't start - run aliases are not valid"))))
       (catch Exception e (errorln "Unexpected error" e))))
