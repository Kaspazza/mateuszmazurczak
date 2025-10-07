(ns mateuszmazurczak.i18n
  "I18n port - public API for translation functionality"
  (:require
   [mateuszmazurczak.i18n.protocol :as p]
   [mateuszmazurczak.validation    :as validation]))

(def TranslatorSchema
  "Schema for translator objects - validates that it implements the protocol"
  [:fn
   (fn [translator]
     (and (some? translator) (satisfies? p/Translator translator)))])

(defn tr
  "Translate a key to text in the given language.
  
  Params:
  * `translator` - translator instance implementing Translator protocol
  * `language` - keyword or vector of language fallbacks
  * `id` - translation key (keyword)
  
  Returns: translated string"
  [translator language id]
  (when-not (or (keyword? language) (vector? language))
    (throw (ex-info "Language must be a keyword or vector"
                    {:type ::invalid-language
                     :provided language})))
  (when-not (keyword? id)
    (throw (ex-info "Translation ID must be a keyword"
                    {:type ::invalid-id
                     :provided id})))
  (validation/validate-data TranslatorSchema translator "Translator inst")
  (p/-translate translator language id))
