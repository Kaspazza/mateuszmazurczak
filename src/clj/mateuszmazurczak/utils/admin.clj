(ns mateuszmazurczak.utils.admin
  "Admin authentication utilities.
   
   Simple admin check based on API key in request headers.
   For moderation purposes only (single admin user)."
  (:require
   [clojure.string :as str]))

(defn valid-admin-key?
  "Check if admin key meets security requirements.
   
   Must be at least 32 characters for security."
  [key]
  (and (string? key) (not (str/blank? key)) (>= (count key) 32)))

(defn admin-authenticated?
  "Check if request has valid admin authentication.
   
   Expects:
   - :admin-api-key in request (from config system)
   - X-Admin-Key header with matching key
   
   Arguments:
   - request: Ring request with :admin-api-key from middleware"
  [request]
  (let [expected-key (:admin-api-key request)
        provided-key (get-in request [:headers "x-admin-key"])]
    (and (some? expected-key)
         (valid-admin-key? expected-key)
         (some? provided-key)
         (= expected-key provided-key))))
