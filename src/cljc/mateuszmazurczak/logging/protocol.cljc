(ns mateuszmazurczak.logging.protocol)

(defprotocol LoggerSystem
  "System-wide logging configuration and lifecycle"
  (-init! [logger config]
   "Initialize the logging system with handlers and configuration")
  (-shutdown! [logger system]
   "Shutdown the logging system and clean up resources"))

(defprotocol Logger
  "Logging abstraction for hexagonal architecture"
  (-log! [logger opts]
   "Record system health and debugging info")
  (-event! [logger event-data-or-id]
   "Record business or operational events")
  (-error! [logger opts-or-error]
   "Record error occurrences")
  (-spy! [logger opts-or-id form]
   "Trace performance of executed code")
  (-with-context [logger context]
   "Create new logger with additional context"))
