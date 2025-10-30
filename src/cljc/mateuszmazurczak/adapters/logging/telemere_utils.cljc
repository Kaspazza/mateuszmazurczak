(ns mateuszmazurczak.adapters.logging.telemere-utils
  (:require
   #?@(:clj [[clojure.java.io :as io] [clojure.pprint :as pp]])
   #?@(:cljs [[cljs.pprint :as pp]])
   [clojure.string  :as str]
   [taoensso.encore :as encore]
   [taoensso.telemere.impl])
  #?(:clj (:import [java.net URI]
                   [java.nio.file Files LinkOption Path]
                   [java.nio.file.attribute FileAttribute])))

(encore/def* format-id
             "`:foo.bar/baz` -> \"::bar/baz\", etc."
             {:arglists '([ns x])
              :tag #?(:clj 'String
                      :cljs 'string)}
             (encore/fmemoize (fn [x]
                                (if (and (keyword? x) (namespace x))
                                  (let [ns-parts (str/split (namespace x) #"\.")
                                        last-ns (last ns-parts)]
                                    (str "::" last-ns "/" (name x)))
                                  (str x)))))

#?(:clj (defmacro pprint
          [obj]
          `(binding [*print-namespace-maps* false
                     *print-meta* false
                     clojure.pprint/*print-suppress-namespaces* true
                     clojure.pprint/*print-right-margin* 150
                     clojure.pprint/*print-shared* false]
             (with-out-str (clojure.pprint/pprint ~obj))))
   :cljs (defn pprint [obj] (with-out-str (cljs.pprint/pprint obj))))

(def ^:dynamic *fmt-opts*
  {:decimal-separator "."
   :thousands-separator ","})

(defn safe-println [& more] (.write *out* (str (str/join "" more) "\n")))

(defn safe-print [& more] (.write *out* (str/join "" more)))

(defn roundn
  ^double [precision n]
  (let [p (Math/pow 10.0 (long precision))] (/ (double (Math/round (* (double n) p))) p)))

(defn fmt-num
  [precision n]
  ;; Impln is inefficient but sufficient, and consistent between clj/s
  (let [n (roundn precision n)
        neg? (neg? n)
        n-abs (Math/abs n)
        n-int-part (long n-abs)
        fmt-opts *fmt-opts*]
    (str (when neg? "-")
         (->> (str n-int-part)
              (reverse)
              (partition 3 3 "")
              (mapv str/join)
              (str/join (get fmt-opts :thousands-separator))
              (str/reverse))
         (when-let [n-dec-part (and (pos? (long precision)) (- n-abs n-int-part))]
           (str (get fmt-opts :decimal-separator)
                (encore/substr (str n-dec-part "000000") :by-len 2 precision))))))

#?(:clj (defn format-time
          [^java.time.Instant inst]
          (let [formatter (java.time.format.DateTimeFormatter/ofPattern "HH:mm:ss.SSSSSSX")
                utc-time (java.time.ZonedDateTime/ofInstant inst java.time.ZoneOffset/UTC)]
            (.format formatter utc-time)))
   :cljs (defn format-time [date] date))


(defn fmt-nsecs
  [nanosecs]
  (let [ns (double nanosecs)]
    (cond
      (>= ns 6e10) (str (fmt-num 2 (/ ns 6e10)) "m")
      (>= ns 1e9) (str (fmt-num 2 (/ ns 1e9)) "s")
      (>= ns 1e6) (str (fmt-num 0 (/ ns 1e6)) "ms")
      (>= ns 1e3) (str (fmt-num 0 (/ ns 1e3)) "μs")
      :else (str (fmt-num 0 ns) "ns"))))

#?(:clj (defn colorize-level
          [level s]
          (case level
            :error (str "\u001b[31m" s "\u001b[0m")
            :warn (str "\u001b[33m" s "\u001b[0m")
            s))
   :cljs (defn colorize-level [_level s] s))

(defn format:console-minimal
  [{:keys [inst msg_ level error kvs kind id]
    :as _signal}]
  (when-not (or (:ignore-console kvs) (= :spy kind))
    (let [line (str (format-time inst)
                    "|"
                    level
                    (when (= (count (name level)) 4) " ")
                    "|"
                    (format-id id)
                    (when-let [msg (force msg_)] (str "| " msg))
                    (when error (if (string? error) (str "| " error) (str "| \n" (pprint error))))
                    "\n")]
      (colorize-level level line))))



(defn middleware:console-run-time
  [{:keys [data run-nsecs kvs inst level id]
    :as signal}]
  (if (:time-prt kvs)
    (do (safe-println (format-time inst)
                      "|"
                      level
                      (when (= (count (name level)) 4) " ")
                      "|" (format-id id)
                      "|" (str "Finish "
                               (when (and data (:msg data)) (str (:msg data)))
                               (when run-nsecs (str " (time: " (fmt-nsecs run-nsecs) ")"))))
        (assoc-in signal [:kvs :ignore-console] true))
    signal))

#?(:clj
     (do
       (defn as-path
         ^Path [path]
         (if (instance? Path path)
           path
           (if (instance? URI path) (java.nio.file.Paths/get ^URI path) (.toPath (io/file path)))))
       (defn exists?
         "Returns true if f exists."
         [f]
         (try (Files/exists (as-path f) (into-array LinkOption [])) (catch Exception _e false)))
       (defn is-existing-path?
         "Returns true if `filename` path already exist."
         [path]
         (when-not (str/blank? path) (when (exists? path) path)))
       (defn directory?
         "Returns true if f is a directory, using Files/isDirectory."
         [f]
         (Files/isDirectory (as-path f) (into-array LinkOption [])))
       (defn is-existing-dir?
         "Check if this the path exist and is a directory."
         [dirname]
         (when (and (is-existing-path? dirname) (directory? dirname)) dirname))
       (defn create-dirs
         "Creates directories using `Files#createDirectories`. Also creates parents if needed.
  Doesn't throw an exception if the dirs exist already. Similar to `mkdir -p`"
         [path]
         (Files/createDirectories (as-path path) (into-array FileAttribute [])))
       (defn ensure-dir-exists
         "Creates directory `dir` if not already existing."
         [path]
         (when-not (is-existing-dir? path) (create-dirs path)))))

#?(:cljs (defn format-log-line
           "Format a Telemere signal into a plain text log line for Loki"
           [{:keys [inst msg_ level error id]}]
           (str (format-time inst)
                "|"
                level
                (when (= (count (name level)) 4) " ")
                "|"
                (format-id id)
                (when-let [msg (force msg_)] (str "| " msg))
                (when error (if (string? error) (str "| " error) (str "| " (pprint error)))))))

#?(:cljs (defn send-to-loki!
           "Send log line to Loki endpoint via HTTP POST"
           [endpoint log-line]
           (when-not (str/blank? endpoint)
             (try (.catch (js/fetch endpoint
                                    (clj->js {:method "POST"
                                              :headers {"Content-Type" "text/plain"}
                                              :body log-line
                                              :mode "cors"}))
                          (fn [err] (prn "Failed because..." (pr-str err)) nil))
                  (catch :default e (prn "Failed because..." (pr-str e)) nil)))))

#?(:cljs
     (defn handler:loki
       "Creates a Telemere handler that sends logs to Loki via Grafana Alloy.
        
        Options:
        - :endpoint - Loki endpoint URL (e.g., 'http://your-server:9000/loki/api/v1/raw')"
       [{:keys [endpoint]}]
       (fn [signal]
         (when endpoint (let [log-line (format-log-line signal)] (send-to-loki! endpoint log-line)))
         nil)))
