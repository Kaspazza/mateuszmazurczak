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
  (let [file-desc (file/read-edn (str project-dir fs/file-separator "shadow-cljs.edn"))
        success? (not (:invalid? file-desc))]
    (when-not success?
      (errorln "Unexpected error, shadow-cljs has not been found in project `project-dir`."))
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

(defn install-cmd "Install components setup in `package.json`." [] ["npm install"])

(defn cljs-compile-cmd
  "Command to compile the `builds` (vector of strings)."
  [builds]
  (reduce conj ["npx" "shadow-cljs" "compile"] builds))

(defn resolve-frontend-config
  "Resolve frontend config using .secrets.edn with ENV variable fallback
   
   Priority order:
   1. .secrets.edn file (for local development)
   2. Environment variables (for CI/Docker builds)
   3. Empty strings (graceful degradation)
   
   This allows builds to work both locally and in containerized environments."
  [env-profile]
  (let [config-path ".secrets.edn"
        config-exists? (fs/exists? config-path)
        config (when config-exists? (:edn (file/read-edn config-path)))
        sentry-dsn (or (get-in config [env-profile :sentry :frontend :dsn])
                       (System/getenv "SENTRY_FRONTEND_DSN")
                       "")
        loki (or (get-in config [env-profile :loki :endpoint]) (System/getenv "LOKI_ENDPOINT") "")
        posthog-api-key
        (or (get-in config [env-profile :posthog :api-key]) (System/getenv "POSTHOG_API_KEY") "")]
    {:closure-defines {'mateuszmazurczak.config/ENV (name env-profile)
                       'mateuszmazurczak.config/LOG_SENTRY_DNS sentry-dsn
                       'mateuszmazurczak.config/POSTHOG_API_KEY posthog-api-key
                       'mateuszmazurczak.config/LOKI_ENDPOINT loki}}))

(defn production-config-merge
  "Generate config-merge string for production builds"
  []
  (pr-str (resolve-frontend-config :production)))

(defn cljs-compile-release-cmd
  "Command to compile the `builds` (vector of strings) with production config."
  [build]
  ["npx" "shadow-cljs" "release" build "--config-merge" (str "'" (production-config-merge) "'")])

(defn karma-test-cmd
  "Returns a command to launch karma test."
  []
  ["npx" "karma" "start" "--single-run"])
