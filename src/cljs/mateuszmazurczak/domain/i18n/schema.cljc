(ns mateuszmazurczak.domain.i18n.schema "Malli schemas for i18n data structures")

(def I18nMarker
  "Schema for i18n translation marker: [:i18n :translation-key] or [:i18n :key {:params}]"
  [:or [:tuple [:= :i18n] :keyword] [:tuple [:= :i18n] :keyword :map]])
