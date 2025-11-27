(ns mateuszmazurczak.domain.theme.schema
  "Theme domain schemas and validations.
   
   Theme persistence is handled by the cache system (see domain.cache.registry).")

(def theme-values "Valid theme values" #{:light :dark})

(def theme-schema "Schema for theme value" [:enum :light :dark])

;; =============================================================================
;; Constants
;; =============================================================================

(def default-theme :light)

;; =============================================================================
;; Domain Functions
;; =============================================================================

(defn valid-theme? "Check if theme value is valid." [theme] (contains? theme-values theme))
