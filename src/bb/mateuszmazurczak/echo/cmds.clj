(ns mateuszmazurczak.echo.cmds
  "Wrap command functions with action echoing."
  (:require
   [clojure.string                :as str]
   [mateuszmazurczak.cmds         :as cmds]
   [mateuszmazurczak.echo.actions :refer [errorln normalln uri-str]]
   [mateuszmazurczak.echo.common  :as echo-common]))


;; Simple echoing delegation
;; These functions are here to make simple the echoing so all displaying functions are here and no need to require also -cmd
(defn echoed-cmd
  "Wrap a command string `cmd-str` before printing to be seen as a uri in the terminal."
  [cmd-str]
  (echo-common/cmd-str cmd-str))

(defn exec-cmd-str
  "Returns the string of the execution of a command `cmd-str`"
  [cmd-str]
  (echo-common/exec-cmd-str cmd-str))

(defn kill [process] (cmds/kill process))

(defn success "Returns `true` if the result is a success" [result] (cmds/success result))

(defn clj-parameterize
  "Turns `par` into a parameter understood by a clojure `cli`."
  [par]
  (cmds/clj-parameterize par))

(defn force-dirs [cmd-chain dir] (cmds/force-dirs cmd-chain dir))

(defn chain-cmds [cmd-chain] (cmds/chain-cmds cmd-chain))

;; Printing commands
(defn print-cmd-str
  "Print the command string `cmd-str`."
  [prefixs cmd-str]
  (normalln prefixs (echoed-cmd cmd-str)))

(defn print-exec-cmd-str
  "Print a message telling the execution of the command string `cmd-str`."
  [prefixs cmd-str dir]
  (normalln prefixs (exec-cmd-str cmd-str))
  (normalln prefixs "(in directory:" (uri-str dir) ")"))

;; Execute commands
(defn- print-errors-if-cmd-failed
  "Print the result of a command if an error has occured:
  * if the command is not found and an exception is raised,
  * or if the command is found and return a non zero exit code.

  No need to publish this function it is included in `blocking-cmd`.
  Returns `nil`."
  [prefixs {:keys [exit out err cmd-str dir]} non-zero-exit-message]
  (when-not (= 0 exit)
    (errorln prefixs non-zero-exit-message)
    (normalln prefixs "This command has failed:")
    (print-cmd-str prefixs cmd-str)
    (normalln prefixs "(in directory" (echo-common/uri-str dir) ")")
    (when-not (str/blank? out) (normalln prefixs out))
    (when-not (str/blank? err) (normalln prefixs err))
    nil))

(defn blocking-cmd
  "Execute a command in the blocking mode, so the result is returned when the function is returned.

  Print the command `cmd` if verbose is `true`, and execute `cmd` in directory `dir`.
  The `prefixs` are added, the `err-message` also if an error occur."
  ([prefixs cmd dir err-message] (blocking-cmd prefixs cmd dir err-message false))
  ([prefixs cmd dir err-message verbose?]
   (let [dir (cmds/defaulting-dir dir)
         cmd-str (cmds/to-str cmd)]
     (when verbose? (print-exec-cmd-str prefixs cmd-str dir))
     (let [res (cmds/blocking-cmd-str cmd-str dir)]
       (print-errors-if-cmd-failed prefixs res err-message)
       res))))

(defn long-living-cmd
  "Execute and print command `cmd` that is a long living one. So all outputs will be displayed with `prefixs.`

  As there are listeners to achieve that, the `refresh-delay` is specifying the delay between two refreshs.

  With `out-filter-fn` you can decide which lines you display or not, knowing that (constantly true) will accept them all.
  With `err-filter-fn` you can decide which lines you display or not, knowing that (constantly true) will accept them all.

  If there is no need or no will to wait for the end of the command, just call it in a future."
  [prefixs cmd dir refresh-delay verbose? out-filter-fn err-filter-fn]
  (let [dir (cmds/defaulting-dir dir)
        cmd-str (cmds/to-str cmd)]
    (when verbose? (print-exec-cmd-str prefixs cmd-str dir))
    (try (let [proc (cmds/create-process cmd dir)]
           (future (cmds/log-stream
                    proc
                    :out
                    (fn [l] (when (out-filter-fn l) (normalln prefixs l)))
                    (fn []
                      (when verbose?
                        (normalln "Execution of" (cmd-str cmd) "ended, out listener is killed.")))
                    refresh-delay
                    (partial errorln prefixs)))
           (future (cmds/log-stream
                    proc
                    :err
                    (fn [l] (when (err-filter-fn l) (normalln prefixs l)))
                    (fn []
                      (when verbose?
                        (normalln "Execution of" (cmd-str cmd) "ended, err listener is killed.")))
                    refresh-delay
                    (partial errorln prefixs)))
           (cmds/exec proc)
           ;; Without this pause, some messages are lost
           (Thread/sleep 100)
           {:dir dir
            :cmd-str cmd-str
            :proc proc})
         (catch Exception e
           {:e e
            :dir dir
            :cmd-str cmd-str}))))
