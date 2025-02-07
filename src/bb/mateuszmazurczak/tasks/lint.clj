(ns mateuszmazurczak.tasks.lint
  "Lint the application.

   [clj-kondo](https://github.com/clj-kondo/clj-kondo?tab=readme-ov-file#usage) "
  (:require
   [mateuszmazurczak.cli-opts     :as cli-opts]
   [mateuszmazurczak.echo.cmds    :refer [blocking-cmd]]
   [mateuszmazurczak.echo.headers :refer [build-writter
                                          errorln
                                          h1
                                          h1-error
                                          h1-valid
                                          normalln
                                          print-writter]]))

(def cli-opts-data
  (concat cli-opts/help-options
          cli-opts/verbose-options
          cli-opts/inverse-options))

(def cli-opts
  (-> cli-opts-data
      cli-opts/parse-cli))

(def verbose
  "Set to true if the cli has been set up to `-v` verbose option."
  (get-in cli-opts [:options :verbose]))

(defn lint-cmd
  "Lint command If `debug?` is set, that informations are displayed."
  [debug?]
  (-> (concat ["clj-kondo"] ;; Project too small : "--parallel"
              (when debug? ["--debug"])
              ["--lint" "."])
      vec))

(defn lint
  "For a project which `deps.edn` is described with `deps-file-desc`.

  The files to lint are extracted fro mthe `paths` and `extra-paths` of the `deps.edn` file.
  Returns `true` if ok."
  []
  (h1 "Linter")
  (let [s (build-writter)
        lint-cmd (lint-cmd verbose)
        {:keys [out err exit]}
        (binding [*out* s] (blocking-cmd ["lint"] lint-cmd "" "" verbose))]
    (cond
      (zero? exit) (h1-valid "Linter ok")
      (re-find #"linting took \d*ms, errors: \d*, warnings: \d*" (str out))
      (do (h1-error "Linter found errors \n") (normalln err out))
      :else (do (errorln "Failed during linting.") (normalln out) (normalln)))
    (print-writter s)
    (zero? exit)))
