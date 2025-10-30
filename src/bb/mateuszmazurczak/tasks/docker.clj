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


(defn- load-secrets-for-env
  "Load secrets from .secrets.edn for specific environment profile.
   Fails fast if .secrets.edn doesn't exist or can't be read."
  [env-profile]
  (let [secrets-file ".secrets.edn"]
    (when-not (.exists (io/file secrets-file))
      (h1-error! "Missing required file:" secrets-file)
      (throw (ex-info "Build requires .secrets.edn file"
                      {:type ::missing-secrets-file
                       :file secrets-file
                       :profile env-profile})))
    (let [{:keys [edn invalid? exception]} (file/read-edn secrets-file)]
      (when invalid?
        (h1-error! "Failed to read .secrets.edn:" (ex-message exception))
        (throw (ex-info "Failed to read .secrets.edn"
                        {:type ::invalid-secrets-file
                         :file secrets-file
                         :profile env-profile}
                        exception)))
      (get edn env-profile))))

(def ^:private runtime-secrets-env-mapping
  "Map of RUNTIME environment variable names to their paths in secrets.edn
   These secrets are injected when the container RUNS (not during build)"
  {:DB_URI [:db :uri]
   :SENTRY_BACKEND_DSN [:sentry :backend :dsn]})

(def ^:private build-secrets-mapping
  "Map of BUILD-TIME secrets to their paths in secrets.edn
   These secrets are needed during Docker image BUILD (for frontend compilation)
   They get baked into the JS bundle via closure-defines"
  {:POSTHOG_API_KEY [:posthog :api-key]
   :SENTRY_FRONTEND_DSN [:sentry :frontend :dsn]
   :LOKI_ENDPOINT [:loki :endpoint]})

(defn- validate-secrets-coverage
  "Validate that all required environment variables have values in secrets
   Logs warnings for missing secrets but doesn't fail (allows ENV var fallback)"
  [secrets profile mapping]
  (doseq [[env-var path] mapping]
    (when-not (get-in secrets path)
      (normalln "Warning: No value found for" (name env-var)
                "at path" path
                "in .secrets.edn profile" profile))))

(defn- secrets->runtime-env-args
  "Convert secrets map to docker -e arguments for RUNTIME using runtime-secrets-env-mapping
   Only includes environment variables that have values in secrets"
  [secrets profile]
  (when secrets
    (validate-secrets-coverage secrets profile runtime-secrets-env-mapping)
    (vec (mapcat (fn [[env-var path]]
                   (when-let [value (get-in secrets path)] ["-e" (str (name env-var) "=" value)]))
          runtime-secrets-env-mapping))))

(defn- secrets->build-args
  "Convert secrets map to docker --build-arg arguments for BUILD TIME
   These secrets are needed during image build for frontend compilation"
  [secrets profile]
  (validate-secrets-coverage secrets profile build-secrets-mapping)
  (vec (mapcat (fn [[env-var path]]
                 (when-let [value (get-in secrets path)]
                   ["--build-arg" (str (name env-var) "=" value)]))
        build-secrets-mapping)))

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

(defn build-image
  "Build Docker image with specified tag
   Automatically loads build-time secrets from .secrets.edn for specified profile"
  ([tag] (build-image default-image-name tag))
  ([image-name tag] (build-image image-name tag default-dockerfile))
  ([image-name tag dockerfile]
   (let [profile (get-in cli-opts [:options :profile])
         secrets (load-secrets-for-env profile)
         build-args (secrets->build-args secrets profile)
         image-tag (str image-name ":" tag)
         base-cmd ["docker" "build" "--platform" "linux/amd64" "-f" dockerfile "-t" image-tag]
         cmd (vec (concat base-cmd build-args ["."]))]
     (normalln "Building Docker image:" image-tag)
     (normalln "Using environment profile:" profile)
     (when (seq build-args) (normalln "Injecting build-time secrets from .secrets.edn"))
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

(defn run-image
  "Run Docker image with --rm flag
   Automatically loads runtime secrets from .secrets.edn for specified profile
   Options:
   - :profile - environment profile for secrets (default: :production)"
  ([tag] (run-image default-image-name tag))
  ([image-name tag]
   (let [profile (get-in cli-opts [:options :profile])
         image-tag (str image-name ":" tag)
         secrets (load-secrets-for-env profile)
         env-args (secrets->runtime-env-args secrets profile)
         current-dir (System/getProperty "user.dir")
         base-cmd ["docker"
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
     (when (seq env-args) (normalln "Injecting runtime environment variables from .secrets.edn"))
     (long-living-cmd ["docker-run"] cmd "." 100 verbose (constantly true) (constantly true)))))

(defn build-and-push
  "Build and push Docker image"
  ([tag] (build-and-push default-image-name tag))
  ([image-name tag]
   (let [build-result (build-image image-name tag)]
     (if (= :success (:status build-result)) (push-image image-name tag) build-result))))

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
