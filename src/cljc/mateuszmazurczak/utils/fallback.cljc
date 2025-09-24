(ns mateuszmazurczak.utils.fallback
  "Fallback utilties"
  (:require
   [mateuszmazurczak.logging :as log])
  #?(:cljs (:require-macros [mateuszmazurczak.utils.fallback])))

(defn cljs-env?
  "Take the &env from a macro, and tell whether we are expanding into cljs."
  [env]
  (boolean (:ns env)))

(defmacro always-return
  "To be used with great caution, as this is not a good practice to ignore exceptions. This should be used only in if it's really important for some application part to always return the value and should be used as a last resort.
  Like e.g. it's used in not-found-page for it to be displayed without a problem and inform a user that something is wrong.
  `expr-fn` fails returns `ret-val`
  `logger` (optional) - if provided, the exception will be logged"
  ([expr-fn ret-val]
   `(always-return ~expr-fn ~ret-val nil))
  ([expr-fn ret-val logger]
   (let [catch-level# (if (cljs-env? &env)
                        :default
                        #?(:clj Throwable
                           :cljs :default))
         ns (str *ns*)]
     `(try (~expr-fn)
           (catch ~catch-level# e#
             (when ~logger
               (log/error! ~logger
                           {:error e#
                            :id ::fallback-exception-caught
                            :data {:ret-val ~ret-val
                                   :expr-fn '~expr-fn
                                   :ns ~ns}}))
             ~ret-val)))))
