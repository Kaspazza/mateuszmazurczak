(ns mateuszmazurczak.portfolio.macros
  "Compile-time macros for portfolio documentation."
  (:require
   [clojure.string :as str]))

(defmacro embed-source
  "Reads source file at compile time and returns as string.
  
  Args:
  - namespace-symbol-or-string: The namespace to read source for (symbol or string)
  
  Example:
  (embed-source mateuszmazurczak.ui.components.avatar)
  (embed-source 'mateuszmazurczak.ui.components.avatar)
  
  Returns the full source code of the file as a string."
  [namespace-symbol-or-string]
  (let [ns-str (cond
                 (symbol? namespace-symbol-or-string) (str namespace-symbol-or-string)
                 (string? namespace-symbol-or-string) namespace-symbol-or-string
                 :else (throw (ex-info "embed-source expects a namespace symbol or string"
                                       {:argument namespace-symbol-or-string})))
        file-path (-> ns-str
                      (str/replace "." "/")
                      (str/replace "-" "_")
                      (str ".cljs"))
        full-path (str "src/cljs/" file-path)]
    (try (slurp full-path)
         (catch Exception e
           (throw (ex-info (str "Failed to read source file: " full-path)
                           {:namespace namespace-symbol-or-string
                            :file-path full-path
                            :error (.getMessage e)}
                           e))))))
