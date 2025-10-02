(ns mateuszmazurczak.tasks.docker
  (:require
   [clojure.java.io               :as io]
   [clojure.string                :as str]
   [mateuszmazurczak.cli-opts     :as cli-opts]
   [mateuszmazurczak.echo.cmds    :refer [long-living-cmd]]
   [mateuszmazurczak.echo.headers :refer [h1-error! h1-valid! normalln]]
   [mateuszmazurczak.file         :as file]))

(def cli-opts
  (-> [["-p"
        "--profile PROFILE"
        "Environment profile for secrets (default: production)"
        :default
        :production
        :parse-fn
        keyword]]
      (concat cli-opts/help-options cli-opts/verbose-options)
      cli-opts/parse-cli))

(def verbose
  "Set to true if the cli has been set up to `-v` verbose option."
  (get-in cli-opts [:options :verbose]))

(def arguments-validation
  "Arguments validation for docker tasks - requires version tag"
  {:doc-str "<version>"
   :message "Version tag for Docker image (e.g., 1.0.0)"
   :valid-fn (fn [args] (and (seq args) (not (str/blank? (first args)))))})

(def default-image-name "mateuszmazurczak/personal")
(def default-dockerfile "docker/build.dockerfile")

(defn build-image
  "Build Docker image with specified tag"
  ([tag] (build-image default-image-name tag))
  ([image-name tag] (build-image image-name tag default-dockerfile))
  ([image-name tag dockerfile]
   (let [image-tag (str image-name ":" tag)
         cmd ["docker"
              "build"
              "--platform"
              "linux/amd64"
              "-f"
              dockerfile
              "-t"
              image-tag
              "."]]
     (normalln "Building Docker image:" image-tag)
     (let [{:keys [proc]
            :as _result}
           (long-living-cmd ["docker-build"]
                            cmd
                            "."
                            100 ; refresh delay
                            verbose
                            (constantly true)  ; show all out lines
                            (constantly true)) ; show all err lines
           process-result (when proc @proc)
           exit-code (:exit process-result)]
       (if (zero? exit-code)
         (do (h1-valid! "Docker image built successfully:" image-tag)
             {:status :success
              :image image-tag})
         (do (h1-error! "Docker build failed") {:status :failed}))))))

(defn push-image
  "Push Docker image to registry"
  ([tag] (push-image default-image-name tag))
  ([image-name tag]
   (let [image-tag (str image-name ":" tag)
         cmd ["docker" "push" image-tag]]
     (normalln "Pushing Docker image:" image-tag)
     (let [{:keys [proc]
            :as _result}
           (long-living-cmd ["docker-push"]
                            cmd
                            "."
                            100 ; refresh delay
                            verbose
                            (constantly true)  ; show all out lines
                            (constantly true)) ; show all err lines
           process-result (when proc @proc)
           exit-code (:exit process-result)]
       (if (zero? exit-code)
         (do (h1-valid! "Docker image pushed successfully:" image-tag)
             {:status :success
              :image image-tag})
         (do (h1-error! "Docker push failed") {:status :failed}))))))

(defn- load-secrets-for-env
  "Load secrets from .secrets.edn for specific environment profile"
  [env-profile]
  (let [secrets-file ".secrets.edn"]
    (if (.exists (io/file secrets-file))
      (let [{:keys [edn invalid? exception]} (file/read-edn secrets-file)]
        (if invalid?
          (do (h1-error! "Failed to read .secrets.edn:" (ex-message exception))
              nil)
          (get edn env-profile)))
      (do (normalln "No .secrets.edn found, will use ENV variables only")
          nil))))

(def ^:private secrets-env-mapping
  "Map of environment variable names to their paths in secrets.edn
   This is the single source of truth for which secrets are exposed as ENV vars"
  {:DB_URI [:db :uri]
   :SENTRY_BACKEND_DSN [:sentry :backend :dsn]
   :SENTRY_FRONTEND_DSN [:sentry :frontend :dsn]})

(defn- validate-secrets-coverage
  "Validate that all required environment variables have values in secrets
   Logs warnings for missing secrets but doesn't fail (allows ENV var fallback)"
  [secrets profile]
  (doseq [[env-var path] secrets-env-mapping]
    (when-not (get-in secrets path)
      (normalln "Warning: No value found for" (name env-var)
                "at path" path
                "in .secrets.edn profile" profile))))

(defn- secrets->env-args
  "Convert secrets map to docker -e arguments using secrets-env-mapping
   Only includes environment variables that have values in secrets"
  [secrets profile]
  (when secrets
    (validate-secrets-coverage secrets profile)
    (vec (mapcat (fn [[env-var path]]
                   (when-let [value (get-in secrets path)]
                     ["-e" (str (name env-var) "=" value)]))
          secrets-env-mapping))))

(defn run-image
  "Run Docker image with --rm flag
   Automatically loads secrets from .secrets.edn for specified profile
   Options:
   - :profile - environment profile for secrets (default: :production)"
  ([tag] (run-image default-image-name tag))
  ([image-name tag]
   (let [profile (get-in cli-opts [:options :profile])
         image-tag (str image-name ":" tag)
         secrets (load-secrets-for-env profile)
         env-args (secrets->env-args secrets profile)
         current-dir (System/getProperty "user.dir")
         base-cmd
         ["docker"
          "run"
          "--mount"
          (str "type=bind,source=" current-dir "/docker/db,target=/app/data/db")
          "-p"
          "8080:8080"
          "--platform"
          "linux/amd64"
          "--rm"]
         cmd (vec (concat base-cmd env-args [image-tag]))]
     (normalln "Running Docker image:" image-tag)
     (normalln "Using environment profile:" profile)
     (when (seq env-args)
       (normalln "Injecting environment variables from .secrets.edn"))
     (long-living-cmd ["docker-run"]
                      cmd
                      "."
                      100
                      verbose
                      (constantly true)
                      (constantly true)))))

(defn build-and-push
  "Build and push Docker image"
  ([tag] (build-and-push default-image-name tag))
  ([image-name tag]
   (let [build-result (build-image image-name tag)]
     (if (= :success (:status build-result))
       (push-image image-name tag)
       build-result))))

(defn run
  "Run Docker task - entry point for all docker commands
   Argument validation is handled by bb.edn via enter-with-arguments
   
   Usage: 
     bb docker-build <version>
     bb docker-push <version>
     bb docker-run <version>
     bb docker-build-push <version>"
  [task-type]
  (let [tag (first (get-in cli-opts [:arguments]))]
    (case task-type
      "build" (build-image tag)
      "push" (push-image tag)
      "run" (run-image tag)
      "build-push" (build-and-push tag)
      (do (h1-error! "Unknown Docker task:" task-type)
          (normalln "Available tasks: build, push, run, build-push")
          {:status :failed}))))
