(ns mateuszmazurczak.tasks.build-jar
  (:require
   [clojure.pprint                :as pprint]
   [clojure.tools.build.api       :as clj-build-api]
   [mateuszmazurczak.cli-opts     :as cli-opts]
   [mateuszmazurczak.echo.cmds    :refer [blocking-cmd]]
   [mateuszmazurczak.echo.headers :refer [build-writter h1-error h1-valid normalln print-writter]]
   [mateuszmazurczak.tasks.css    :as css]
   [mateuszmazurczak.tasks.shadow :as shadow]))

(def cli-opts
  (-> []
      (concat cli-opts/help-options cli-opts/verbose-options)
      cli-opts/parse-cli))

(def verbose
  "Set to true if the cli has been set up to `-v` verbose option."
  (get-in cli-opts [:options :verbose]))

(defn compile-uber-jar
  "Compile code to jar or uber-jar based on `jar-type`."
  ([class-dir target-jar-path jar-main] (compile-uber-jar class-dir target-jar-path jar-main []))
  ([class-dir target-jar-path jar-main java-opts]
   (compile-uber-jar class-dir target-jar-path jar-main java-opts "."))
  ([class-dir target-jar-path jar-main java-opts project-dir]
   (try (clj-build-api/set-project-root! project-dir)
        (let [basis (clj-build-api/create-basis)]
          (clj-build-api/compile-clj {:basis basis
                                      :class-dir class-dir
                                      :java-opts java-opts})
          (clj-build-api/uber {:class-dir class-dir
                               :uber-file target-jar-path
                               :basis basis
                               :main jar-main}))
        {:status :success
         :jar-path target-jar-path}
        (catch Exception e
          {:status :failed
           :exception e}))))

(defn copy-files
  []
  (clj-build-api/copy-dir {:target-dir "target/prod/classes"
                           :src-dirs ["src/cljc" "src/clj" "env/production/src/clj"]})
  (clj-build-api/copy-dir {:target-dir "target/prod/classes/public"
                           :src-dirs ["resources/public"]})
  (clj-build-api/copy-file {:target "target/prod/classes/config.edn"
                            :src "env/production/config.edn"}))

(defn command-print
  ([verbose? success-text fail-text cmd-fn]
   (let [s (build-writter)
         {:keys [cmd-str exit out]} (binding [*out* s] (cmd-fn))]
     (if (zero? exit)
       (h1-valid success-text)
       (do (h1-error fail-text) (print-writter s) (normalln)))
     (when verbose? (normalln cmd-str) (normalln out))))
  ([success-text fail-text cmd-fn] (command-print false success-text fail-text cmd-fn)))

(defn production-css
  []
  (normalln "Generating css...")
  (->> #(blocking-cmd ["css"]
                      (css/tailwind-release-cmd "resources/css/main.css"
                                                "resources/public/css/compiled/styles.css")
                      "."
                      "css generation has failed"
                      false)
       (command-print verbose "Generated css" "Generating css failed")))

(defn npm-install
  []
  (normalln "NPM install...")
  (->> #(blocking-cmd ["shadow-cljs"] (shadow/install-cmd) "." "Npm install has failed" false)
       (command-print verbose "NPM installed" "NPM install failed")))

(defn production-cljs
  [verbose]
  (normalln "Generating js...")
  (->> #(blocking-cmd ["shadow-cljs"]
                      (shadow/cljs-compile-release-cmd :mateuszmazurczak-app)
                      "."
                      "js generation has failed")
       (command-print verbose "Generated js" "Generating js failed")))

(defn build-uber-jar
  []
  (normalln "Jar compilation started...")
  (copy-files)
  (let [{:keys [status]
         :as res}
        (compile-uber-jar "target/prod/classes" "target/prod/mateuszmazurczak.jar"
                          "mateuszmazurczak.core" ["-Dconf-var=config.edn"])]
    (if (= :success status)
      (h1-valid "Jar has been built at:" (:jar-path res))
      (do (h1-error "Jar compilation failed.") (pprint/pprint (:exception res))))))

(defn run [] (npm-install) (production-css) (production-cljs verbose) (build-uber-jar))

