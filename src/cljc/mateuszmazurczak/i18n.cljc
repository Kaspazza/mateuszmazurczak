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
  "Translate a key to text in the given language, optionally with interpolation params.
  
  Params:
  * `translator` - translator instance implementing Translator protocol
  * `language` - keyword or vector of language fallbacks
  * `id` - translation key (keyword)
  * `params` - optional map of parameters for interpolation (defaults to nil)
  
  Returns: translated string"
  ([translator language id]
   (tr translator language id nil))
  ([translator language id params]
   (when-not (or (keyword? language) (vector? language))
     (throw (ex-info "Language must be a keyword or vector"
                     {:type ::invalid-language
                      :provided language})))
   (when-not (keyword? id)
     (throw (ex-info "Translation ID must be a keyword"
                     {:type ::invalid-id
                      :provided id})))
   (when (and params (not (map? params)))
     (throw (ex-info "Params must be a map"
                     {:type ::invalid-params
                      :provided params})))
   (validation/validate-data TranslatorSchema translator "Translator inst")
   (if params
     (p/-translate translator language id params)
     (p/-translate translator language id))))
