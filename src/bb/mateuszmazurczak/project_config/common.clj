(ns mateuszmazurczak.project-config.common
  (:require
   [babashka.fs           :as fs]
   [mateuszmazurczak.file :as file]))

(def project-cfg-filename "project.edn")

(defn read-from-dir
  "Returns the project configuration file descriptor in `app-dir`."
  [app-dir]
  (-> (str app-dir fs/file-separator project-cfg-filename)
      file/read-edn))

(defn create-project-map
  "Creates a project map based on app in `app-dir`."
  [app-dir]
  {:app-dir app-dir})

(defn add-project-config
  "Adds project configuration file desc `project-config-filedesc`to the `project-map`."
  [{:keys [app-dir]
    :as project-map}]
  (let [project-config-filedesc (read-from-dir app-dir)]
    (assoc project-map
           :project-config-filedesc (when-not (:invalid? project-config-filedesc)
                                      project-config-filedesc)
           :app-name (get-in project-config-filedesc [:edn :app-name]))))
