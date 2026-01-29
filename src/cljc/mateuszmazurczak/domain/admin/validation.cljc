(ns mateuszmazurczak.domain.admin.validation "Admin key validation logic.")

(def min-admin-key-length 32)

(defn valid-admin-key?
  "Check if admin key is valid.
   
   Admin key must be:
   - Non-nil
   - Non-empty string
   - At least 32 characters long"
  [admin-key]
  (and (string? admin-key) (not (empty? admin-key)) (>= (count admin-key) min-admin-key-length)))

(defn validate-admin-login
  "Validate admin login attempt.
   
   Returns map with:
   - :valid? - boolean indicating if login can proceed
   - :reason - keyword explaining why validation failed (if invalid)
   
   Possible reasons:
   - :invalid-key - Key is nil, empty, or too short"
  [admin-key]
  (if (valid-admin-key? admin-key)
    {:valid? true}
    {:valid? false
     :reason :invalid-key}))
