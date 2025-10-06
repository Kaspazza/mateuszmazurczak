(ns mateuszmazurczak.tasks.shadow
  "cljs compiler toolings. Use shadow on npx.
   (https://shadow-cljs.github.io/docs/UsersGuide.html#_command_line)"
  (:require
   [babashka.fs                   :as fs]
   [mateuszmazurczak.echo.headers :refer [errorln]]
   [mateuszmazurczak.file         :as file]))

(defn read-dir
  "Read the project `shadow-cljs.edn`, echo in terminal if an error occur."
  [project-dir]
  (let [file-desc (file/read-edn
                   (str project-dir fs/file-separator "shadow-cljs.edn"))
        success? (not (:invalid? file-desc))]
    (when-not success?
      (errorln
       "Unexpected error, shadow-cljs has not been found in project `project-dir`."))
    (:edn file-desc)))

(defn build
  "Returns builds defined in`shadow-cljs-edn`."
  [shadow-cljs-edn]
  (some-> shadow-cljs-edn
          (get :builds)
          keys
          vec))

(defn cljs-watch-cmd
  "Watch modification on frontend code for aliases `shadow-cljs-aliases`."
  [shadow-cljs-aliases]
  (-> ["npx" "shadow-cljs" "watch"]
      (concat shadow-cljs-aliases)
      vec))

(defn install-cmd
  "Install components setup in `package.json`."
  []
  ["npm install"])

(defn cljs-compile-cmd
  "Command to compile the `builds` (vector of strings)."
  [builds]
  (reduce conj ["npx" "shadow-cljs" "compile"] builds))

(defn resolve-frontend-config
  "Resolve frontend config using the same Aero system as backend"
  [env-profile]
  (let [config-path ".secrets.edn"
        config (:edn (file/read-edn config-path))
        sentry-dsn (get-in config [env-profile :sentry :frontend :dsn])
        loki (get-in config [env-profile :loki :endpoint])
        posthog-api-key (get-in config [env-profile :posthog :api-key])]
    {:closure-defines
     {'mateuszmazurczak.config/ENV (name env-profile)
      'mateuszmazurczak.config/LOG_SENTRY_DNS (or sentry-dsn "")
      'mateuszmazurczak.config/POSTHOG_API_KEY (or posthog-api-key "")
      'mateuszmazurczak.config/LOKI_ENDPOINT (or loki "")}}))

(defn production-config-merge
  "Generate config-merge string for production builds"
  []
  (pr-str (resolve-frontend-config :production)))

(defn cljs-compile-release-cmd
  "Command to compile the `builds` (vector of strings) with production config."
  [build]
  ["npx"
   "shadow-cljs"
   "release"
   build
   "--config-merge"
   (str "'" (production-config-merge) "'")])

(defn karma-test-cmd
  "Returns a command to launch karma test."
  []
  ["npx" "karma" "start" "--single-run"])
