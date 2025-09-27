(ns mateuszmazurczak.logging.telemere
  (:require
   [mateuszmazurczak.logging.protocol       :as p]
   [mateuszmazurczak.logging.telemere-utils :as logging-utils]
   [taoensso.telemere                       :as t])
  #?(:cljs (:require-macros [mateuszmazurczak.logging.telemere
                             :refer
                             [set-min-level! with-min-level!]])))


;;Configuration
(defmacro set-min-level!
  "Overrides globally minimal level of logs to appear. To be used with caution, this will apply filter to all the logs in the monorepo."
  [& args]
  `(t/set-min-level! ~@args))

(defmacro with-min-level!
  "Overrides all the code that's executed inside of it to ignore logs below specified level"
  [& args]
  `(t/with-min-level ~@args))


#?(:clj
     (defn init!
       "Initialize logging, contains defaults on handling the logs and it's level"
       ([]
        (init! {;; :console-default true
                :level :debug}))
       ([{:keys [console-default level path]
          :or {path "log"}}]
        (set-min-level! level)
        (t/set-kind-filter! {:disallow :slf4j})
        (when-not console-default
          (t/remove-handler! :default/console)
          (t/add-handler! :console-handler
                          (t/handler:console
                           {:output-fn logging-utils/format:console-minimal})))
        (t/set-xfn! logging-utils/middleware:console-run-time)
        (logging-utils/ensure-dir-exists path)
        (t/add-handler! :file-handler
                        (t/handler:file {:path (str path "/logs.log")}))))
   :cljs (defn init!
           ([] (init! {:level :info}))
           ([{:keys [level]}]
            (set-min-level! level)
            (t/remove-handler! :default/console)
            (t/add-handler! :console-handler
                            (t/handler:console
                             {:output-fn
                              logging-utils/format:console-minimal})))))


(defrecord TelemereLogger [base-context]
  p/LoggerSystem
    (-init! [_ opts] (init! opts))
    (-shutdown! [_ _] nil)
  p/Logger
    (-log! [_ opts]
      (t/log! {:level (or (:level opts) (:level base-context))
               :msg (:msg opts)
               :data (merge (:data base-context) (:data opts))
               :id (:id opts)}))
    (-event! [_ opts]
      (if (map? opts)
        (t/event! {:level (or (:level opts) (:level base-context))
                   :data (merge (:data base-context) (:data opts))
                   :id (:id opts)})
        (t/event! {:id opts
                   :level (:level base-context)
                   :data (:data base-context)})))
    (-error! [_ opts]
      (t/error! {:data (merge (:data base-context) (:data opts))
                 :error (:error opts)
                 :id (:id opts)}))
    (-spy! [_ opts form]
      (t/spy! {:level (or (:level opts) (:level base-context))
               :data (merge (:data base-context) (:data opts))
               :id (:id opts)}
              form))
    (-with-context [this new-context]
      (assoc this :base-context (merge base-context new-context))))

(defn make-logger
  "Create a new Telemere logger instance"
  ([] (make-logger {}))
  ([context] (->TelemereLogger context)))
