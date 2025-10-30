(ns mateuszmazurczak.common
  (:require
   [mateuszmazurczak.cli-opts   :as cli-opts]
   [mateuszmazurczak.exit-codes :as exit-codes]))

(defn enter
  "When entering the task:

  * Print usage if required.
  * Print options if required."
  [cli-opts current-task]
  (when-let [message (cli-opts/error-msg cli-opts)]
    (println message)
    (println)
    (println (cli-opts/print-usage cli-opts (:name current-task)))
    (exit-codes/exit exit-codes/command-not-found))
  (when (get-in cli-opts [:options :help])
    (when (get-in cli-opts [:options :verbose])
      (println "Options are:")
      (println (pr-str cli-opts)))
    (println (cli-opts/print-usage cli-opts (:name current-task)))
    (exit-codes/exit exit-codes/ok)))

(defn enter-with-arguments
  "When entering the task:

  * Print usage if required.
  * Print options if required."
  [cli-opts
   current-task
   {:keys [doc-str message valid-fn]
    :as _arguments}]
  (when (or (not (fn? valid-fn)) (not (valid-fn (:arguments cli-opts))))
    (println "Arguments are not valid.")
    (println)
    (println (cli-opts/print-usage-with-arguments cli-opts (:name current-task) doc-str message))
    (exit-codes/exit exit-codes/invalid-argument))
  (when-let [message (cli-opts/error-msg cli-opts)]
    (println message)
    (println)
    (println (cli-opts/print-usage cli-opts (:name current-task)))
    (exit-codes/exit exit-codes/command-not-found))
  (when (get-in cli-opts [:options :help])
    (when (get-in cli-opts [:options :verbose])
      (println "Options are:")
      (println (pr-str cli-opts)))
    (println (cli-opts/print-usage cli-opts (:name current-task)))
    (exit-codes/exit exit-codes/ok)))
