(ns mateuszmazurczak.tasks.tests
  "Prepare project for proper commit."
  (:refer-clojure :exclude [format])
  (:require
   [mateuszmazurczak.cli-opts              :as cli-opts]
   [mateuszmazurczak.echo.cmds             :refer [blocking-cmd success]]
   [mateuszmazurczak.echo.headers          :refer [build-writter
                                                   errorln
                                                   h1
                                                   h1-error
                                                   h1-error!
                                                   h1-valid
                                                   h1-valid!
                                                   h2
                                                   h2-error
                                                   h2-valid
                                                   normalln
                                                   print-writter]]
   [mateuszmazurczak.exit-codes            :as exit-codes]
   [mateuszmazurczak.project-config.common :as project-config]
   [mateuszmazurczak.tasks.shadow          :as shadow]))

;; ********************************************************************************
;; Task parameters
;; ********************************************************************************
(def cli-opts-data
  (-> [["-f"
        "--tests-frontend"
        "Do not execute frontend tests"
        :default
        true
        :parse-fn
        not]
       ["-b"
        "--tests-backend"
        "Do not execute frontend tests"
        :default
        true
        :parse-fn
        not]]
      (concat cli-opts/help-options
              cli-opts/verbose-options
              cli-opts/inverse-options)))

(def cli-opts
  (-> (cli-opts/parse-cli cli-opts-data)
      (cli-opts/inverse [:tests-frontend :tests-backend])))

(def verbose (get-in cli-opts [:options :verbose]))
(def tests-b? (get-in cli-opts [:options :tests-backend]))
(def tests-f? (get-in cli-opts [:options :tests-frontend]))

;; ********************************************************************************
;; Helpers
;; ********************************************************************************
(defn- npm-install
  [project-dir]
  (h2 "cljs dependencies installation.")
  (let [s (build-writter)
        install-res (binding [*out* s]
                      (-> (shadow/install-cmd)
                          (blocking-cmd ["tests"] project-dir "" verbose)))
        install-success (success install-res)]
    (if install-success
      (h2-valid "npm install ok")
      (h2-error "npm install has failed"))
    (print-writter s)
    (when verbose (normalln (:out install-res)))
    install-success))

(defn- cljs-compilation
  [project-dir]
  (h2 "cljs compilation")
  (let [s (build-writter)
        compile-shadow-res (binding [*out* s]
                             (some->
                               (shadow/read-dir project-dir)
                               shadow/build
                               shadow/cljs-compile-cmd
                               (blocking-cmd ["tests"] project-dir "" verbose)))
        compilation-success (success compile-shadow-res)]
    (if compilation-success
      (h2-valid "cljs compilation ok")
      (h2-error "cljs compilation has failed"))
    (print-writter s)
    (when verbose (normalln (:out compile-shadow-res)))
    compilation-success))

(defn- karma-test
  [project-dir]
  (h2 "karma test")
  (let [s (build-writter)
        cljs-test-res (binding [*out* s]
                        (-> (shadow/karma-test-cmd)
                            (blocking-cmd ["tests"] project-dir "" verbose)))
        cljs-test-success (success cljs-test-res)]
    (if cljs-test-success
      (h2-valid "karma test ok")
      (h2-error "karma has failed"))
    (print-writter s)
    (when verbose (normalln (:out cljs-test-res)))
    cljs-test-success))

;; ********************************************************************************
;; API
;; ********************************************************************************

(defn clj-test
  "Run clj test."
  [project-dir test-aliases]
  (h1 "Test clj")
  (let [s (build-writter)
        clj-res (map #(let [res (binding [*out* s]
                                  (-> ["clojure" (str "-M" %)]
                                      (blocking-cmd ["tests"]
                                                    project-dir
                                                    "Error during tests"
                                                    verbose)))]
                        (when verbose (normalln (:out res)))
                        res)
                     test-aliases)
        clj-success (every? success clj-res)]
    (if-not clj-success
      (do (errorln "clj test have failed.") (print-writter s))
      (h1-valid "clj test ok."))
    clj-success))

(defn cljs-test
  "Run cljs test"
  [project-dir]
  (h1-valid! "Test cljs")
  (let [install-success (npm-install project-dir)
        cljs-compilation-success (when install-success
                                   (cljs-compilation project-dir))
        karma-success (when cljs-compilation-success (karma-test project-dir))]
    (and install-success cljs-compilation-success karma-success)))



(defn run*
  [project-map test-aliases]
  (normalln)
  (let [app-dir (:app-dir project-map)
        status-map (merge (when tests-b?
                            {:clj-tests-check (clj-test app-dir test-aliases)})
                          (when tests-f?
                            {:cljs-tests-check (cljs-test app-dir)}))
        status (->> status-map
                    vals
                    (every? true?))]
    (normalln)
    (normalln)
    (if (true? status)
      exit-codes/ok
      (do (h1 "Synthesis:")
          (doseq [[k v] status-map]
            (if (true? v)
              (h1-valid! (name k) " is ok.")
              (h1-error! (name k) " has failed.")))
          exit-codes/catch-all))))

(defn run
  ([] (run []))
  ([test-aliases]
   (let [project-map (-> (project-config/create-project-map ".")
                         project-config/add-project-config)]
     (run* project-map test-aliases))))
