(ns mateuszmazurczak.repl.entry-point
  "REPL entry point"
  (:require
   [automaton-core.repl   :as core-repl]
   [mateuszmazurczak.core :as mateuszmazurczak-core])
  (:gen-class))

(defn -main
  "Main entry point for repl"
  [& args]
  (core-repl/start-repl args
                        (core-repl/default-middleware)
                        mateuszmazurczak-core/-main))
