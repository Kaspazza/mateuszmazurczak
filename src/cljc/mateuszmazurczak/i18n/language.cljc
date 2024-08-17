(ns mateuszmazurczak.i18n.language
  "Languages for mateuszmazurczak"
  (:require
   [automaton-web.i18n.language :as web-language]))

(def ^:private mateuszmazurczak-languages
  "Is a map defining all supported languages in `mateuszmazurczak` app

  First level of keyword is the id of the language, used internally, should be present in `automaton-core.i18n.language`
  Then, the map has the following data:
  * For now, no specific data is needed in that app "
  {:fr {}
   :en {}})

(def languages
  "Map of available languages for mateuszmazurczak"
  (web-language/make-automaton-web-languages mateuszmazurczak-languages))

(defn ui-str-to-id
  "Transform a ui string of a language to its id, comparison is based on string is not not case sensitive
  Params:
  * `selected-languages` selected-languages where the search is done"
  [lang-ui-text]
  (web-language/ui-str-to-id languages lang-ui-text))

(defn id-to-str
  "Return the string name of a language, based on its id
  Params:
  * `lang-id` keyword of the language which name is required"
  [lang-id]
  (get-in languages [:core-language :languages lang-id :ui-text]))

(defn create-ui-languages
  "Create options for a select component based on language
  Params:
  * none"
  []
  (web-language/create-ui-languages languages))
