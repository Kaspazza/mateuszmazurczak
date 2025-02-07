(ns mateuszmazurczak.i18n.language.web
  "Defines the possible languages for the app")

(def main-langs
  "Default language if all language strategy fail to select a language
  As it is not supposed to happen, this is set to `:en`"
  [:en])

(def ^:private web-languages-map
  "Is a map defining all supported languages in a web app and add specific data
  Then, the map has the following data:
  * `:tld` name of the language in the tld, e.g. mateuszmazurczak.com will be directed to `:en`"
  {:pl {:tld "pl"}
   :en {:tld "com"}})

(defn get-web-lang
  "Return the language linked to `lang-id`"
  [lang-id]
  (get web-languages-map lang-id))
