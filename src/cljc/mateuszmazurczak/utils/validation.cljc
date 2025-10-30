(ns mateuszmazurczak.utils.validation
  "Shared validation utilities for consistent error handling across the application."
  (:require
   [clojure.string :as str]
   [malli.core     :as m]
   [malli.error    :as me]))

(defn validate-data
  "Validate data against schema and throw informative error on failure.
   Uses consistent ex-info structure with :type, :cause, and context data."
  [schema data error-context]
  (when-not (m/validate schema data)
    (let [errors (-> schema
                     (m/explain data)
                     (me/humanize))]
      (throw (ex-info (str "Validation failed for " error-context)
                      {:type ::validation-error
                       :context error-context
                       :errors errors
                       :schema schema
                       :data data})))))

(defn validate-data-or-nil
  "Like validate-data but returns nil on validation failure instead of throwing.
   Useful for optional validation checks."
  [schema data]
  (when (m/validate schema data) data))

(defn wrap-with-validation
  "Higher-order function that wraps a function with input validation.
   
   Args:
   - f: Function to wrap
   - arg-schemas: Vector of schemas, one for each argument
   - error-context: String describing the operation for error messages
   
   Returns wrapped function that validates arguments before calling f."
  [f arg-schemas error-context]
  (fn [& args]
    (when (not= (count args) (count arg-schemas))
      (throw (ex-info (str "Incorrect number of arguments for " error-context)
                      {:type ::arity-mismatch
                       :expected-count (count arg-schemas)
                       :actual-count (count args)
                       :args args})))
    (doseq [[schema arg idx] (map vector arg-schemas args (range))]
      (validate-data schema arg (str error-context " argument " idx)))
    (apply f args)))

(defn valid-non-empty-string?
  "Predicate for non-empty string validation in :pre conditions"
  [s]
  (and (string? s) (not (str/blank? s))))

(defn valid-collection?
  "Predicate for collection validation in :pre conditions"
  [coll]
  (and (coll? coll) (not (string? coll))))
