(ns mateuszmazurczak.echo.headers
  "Prints text with headers on the terminal.

  Each content is modified to wrap the `width` of the terminal, and is modified to have a left margin."
  (:require
   [clojure.string               :as str]
   [mateuszmazurczak.echo.common :as echo-common]
   [mateuszmazurczak.echo.text   :as echo-text]))

(defn- current-left-margin
  "Returns the string of the margin to add."
  []
  (->> (-> @echo-common/echo-param
           (get :section 0)
           (repeat \space))
       (apply str)))

;; Screenify
(defn screenify
  "Prepare the `texts` strings to be printed on the screen."
  [texts]
  (let [prefix-str (current-left-margin)]
    (-> (echo-common/screenify prefix-str texts)
        (assoc :prefix-str prefix-str))))

;; Standardized echoing functions
(defn- pure-printing
  [texts]
  (let [{:keys [prefix-str wrapped-strs]} (screenify texts)]
    (doseq [wrapped-str wrapped-strs] (println (str prefix-str wrapped-str)))))

(defn normalln
  "Print text without decoration, with a carriage return included."
  [& texts]
  (print echo-common/normal-font-color)
  (pure-printing texts)
  (print echo-text/font-default))

(defn errorln
  "To print some text that is an error, should be highlighted and rare."
  [& texts]
  (print echo-common/error-font-color)
  (pure-printing texts)
  (print echo-text/font-default))

(defn exceptionln
  "Display exception `e`."
  [e]
  (errorln (ex-cause e))
  (normalln (pr-str e)))

(defn print-exec-cmd-str
  "Prints the execution of command string `cmd-str` with the `prefixs` added."
  [cmd-str]
  (normalln (echo-common/exec-cmd-str cmd-str)))

(defn print-cmd-str
  "Prints the execution of command string `cmd-str`."
  [cmd-str]
  (normalln (echo-common/cmd-str cmd-str)))

;; Header specific echoing functions
(defn- header-printing
  "Print `texts` with the header prefix called `prefix`."
  [prefix texts]
  (let [{:keys [prefix-str wrapped-strs]} (screenify texts)]
    (doseq [wrapped-str wrapped-strs]
      (println (str (apply str (butlast prefix-str)) prefix wrapped-str)))))

; ********************************************************************************
; One liner
; ********************************************************************************
(defn h1-error!
  [& texts]
  (swap! echo-common/echo-param assoc :section 1)
  (print echo-text/font-red)
  (header-printing "!" texts)
  (print echo-text/font-default))

(defn h1-valid!
  [& texts]
  (swap! echo-common/echo-param assoc :section 1)
  (print echo-text/font-green)
  (header-printing ">" texts)
  (print echo-text/font-default))

(defn h2-error!
  [& texts]
  (swap! echo-common/echo-param assoc :section 2)
  (print echo-text/font-red)
  (header-printing "!" texts))
(print echo-text/font-default)

(defn h2-valid!
  [& texts]
  (swap! echo-common/echo-param assoc :section 2)
  (print echo-text/font-green)
  (header-printing ">" texts)
  (print echo-text/font-default))

(defn h3-valid!
  [& texts]
  (swap! echo-common/echo-param assoc :section 3)
  (print echo-text/font-green)
  (header-printing ">" texts)
  (print echo-text/font-default))

(defn h3-error!
  [& texts]
  (swap! echo-common/echo-param assoc :section 3)
  (print echo-text/font-red)
  (header-printing "!" texts)
  (print echo-text/font-default))

;; ********************************************************************************
;; Headers
;; ********************************************************************************
(defn h1
  [& texts]
  (print echo-text/font-white)
  (swap! echo-common/echo-param assoc :section 1)
  (header-printing "*" texts)
  (print echo-text/font-default))

(defn h1-error
  [& texts]
  (swap! echo-common/echo-param assoc :section 1)
  (print (str echo-text/move-oneup echo-text/clear-eol echo-text/font-red))
  (header-printing "!" texts)
  (print echo-text/font-default)
  (print echo-text/font-default))

(defn h1-valid
  [& texts]
  (swap! echo-common/echo-param assoc :section 1)
  (print (str echo-text/move-oneup echo-text/clear-eol echo-text/font-green))
  (header-printing ">" texts))

(defn h2
  [& texts]
  (print echo-text/font-white)
  (swap! echo-common/echo-param assoc :section 2)
  (header-printing "*" texts)
  (print echo-text/font-default))

(defn h2-error
  [& texts]
  (swap! echo-common/echo-param assoc :section 2)
  (print (str echo-text/move-oneup echo-text/clear-eol echo-text/font-red))
  (header-printing "!" texts)
  (print echo-text/font-default))

(defn h2-valid
  [& texts]
  (swap! echo-common/echo-param assoc :section 2)
  (print (str echo-text/move-oneup echo-text/clear-eol echo-text/font-green))
  (header-printing ">" texts)
  (print echo-text/font-default))

(defn h3
  [& texts]
  (print echo-text/font-white)
  (swap! echo-common/echo-param assoc :section 3)
  (header-printing "*" texts)
  (print echo-text/font-default))

(defn h3-error
  [& texts]
  (swap! echo-common/echo-param assoc :section 3)
  (print (str echo-text/move-oneup echo-text/clear-eol echo-text/font-red))
  (header-printing "!" texts)
  (print echo-text/font-default))

(defn h3-valid
  [& texts]
  (swap! echo-common/echo-param assoc :section 3)
  (print (str echo-text/move-oneup echo-text/clear-eol echo-text/font-green))
  (header-printing ">" texts)
  (print echo-text/font-default))

(comment
  (h2 "test  very long one......")
  (errorln "zeaa")
  (h1-error " failed testt")
  (h1-valid " valid test"))

;; Formatting helpers functions.
(defn pprint-str "Pretty print `data`" [data] (echo-common/pprint-str data))

(defn uri-str
  "Returns the string of the `uri`."
  [uri]
  (echo-common/uri-str uri))

(defn current-time-str
  "Returns current time string."
  []
  (echo-common/current-time-str))

(def clear-prev-line (str echo-text/move-oneup echo-text/clear-eol))

(defn clear-lines [n] (print (str/join "" (repeat n clear-prev-line))))

(defn build-writter [] (echo-common/build-writter))

(defn print-writter [str-writter] (echo-common/print-writter str-writter))
