(ns mateuszmazurczak.i18n.language
  "Languages for mateuszmazurczak"
  (:require
   [clojure.string :as str]))

(def main-langs
  "Default language preference, if all language strategy fail to select a language"
  [:en :pl])

(def web-languages
  "Is a map defining all supported languages in a web app and add specific data
  Then, the map has the following data:
  * `:tld` name of the language in the tld, e.g. mateuszmazurczak.com will be directed to `:en`
  * `:ui-text` how this language should be displayed"
  {:pl {:tld "pl"
        :ui-text "PL"
        :desc "Polski"}
   :en {:tld "com"
        :core-dict? true
        :ui-text "EN"
        :desc "English"}})

(defn ui-str-to-id
  "Transform a ui string of a language to its id, comparison is based on string is not not case sensitive
  Params:
  * `selected-languages` selected-languages where the search is done"
  [lang-ui-text]
  (->> web-languages
       (filter (fn [[_ {:keys [ui-text]}]]
                 (when (every? string? [lang-ui-text ui-text])
                   (= (str/upper-case lang-ui-text) (str/upper-case ui-text)))))
       ffirst))

(defn id-to-str
  "Return the string name of a language, based on its id
  Params:
  * `lang-id` keyword of the language which name is required"
  [lang-id]
  (get-in web-languages [lang-id :ui-text]))
