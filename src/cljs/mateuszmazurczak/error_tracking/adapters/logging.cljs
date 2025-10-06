(ns mateuszmazurczak.error-tracking.adapters.logging
  "Logging adapter - logs uncaught exceptions via logging system (CLJS)"
  (:require
   [mateuszmazurczak.logging :as log]))

(defn init!
  "Install global uncaught exception handlers for browser environment.
   Handles both synchronous errors (window.onerror) and unhandled promise rejections.
   
   Config map:
   - :logger - logger instance to use for logging uncaught exceptions
   
   Returns nil."
  [{:keys [logger]
    :as config}]
  {:pre [(map? config) (some? logger)]}
  (set! (.-onerror js/window)
        (fn [message source lineno colno error]
          (log/error! logger
                      {:error (or error (js/Error. message))
                       :id ::uncaught-error
                       :data {:message message
                              :source source
                              :line lineno
                              :column colno}})
          ;; Return false to allow default error handling to continue
          false))
  (.addEventListener js/window
                     "unhandledrejection"
                     (fn [event]
                       (log/error! logger
                                   {:error (or (.-reason event)
                                               (js/Error.
                                                "Unhandled promise rejection"))
                                    :id ::unhandled-rejection
                                    :data {:reason (.-reason event)
                                           :promise (.-promise event)}})))
  nil)
