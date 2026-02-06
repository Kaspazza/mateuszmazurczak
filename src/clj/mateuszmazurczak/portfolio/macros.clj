(ns mateuszmazurczak.portfolio.macros
  "Compile-time macros for portfolio documentation."
  (:require
   [clojure.string :as str]))

(defmacro embed-source
  "Reads source file at compile time and returns as string.
  
  Args:
  - namespace-symbol: The namespace symbol to read source for
  
  Example:
  (embed-source mateuszmazurczak.ui.components.avatar)
  
  Returns the full source code of the file as a string."
  [namespace-symbol]
  (let [ns-str (str namespace-symbol)
        file-path (-> ns-str
                      (str/replace "." "/")
                      (str/replace "-" "_")
                      (str ".cljs"))
        full-path (str "src/cljs/" file-path)]
    (try (slurp full-path)
         (catch Exception e
           (throw (ex-info (str "Failed to read source file: " full-path)
                           {:namespace namespace-symbol
                            :file-path full-path
                            :error (.getMessage e)}
                           e))))))
