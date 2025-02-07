(ns mateuszmazurczak.i18n.language
  "Languages for mateuszmazurczak"
  (:require
   [clojure.string :as str]))


(def languages
  {:en {:core-dict? true
        :ui-text "EN"
        :desc "English"}
   :pl {:ui-text "PL"
        :desc "Polski"}})

(defn ui-str-to-id
  "Transform a ui string of a language to its id, comparison is based on string is not not case sensitive
  Params:
  * `selected-languages` selected-languages where the search is done"
  [lang-ui-text]
  (->> languages
       (filter (fn [[_ lang]]
                 (when (every? string? [lang-ui-text (:ui-text lang)])
                   (= (str/upper-case lang-ui-text)
                      (str/upper-case (:ui-text lang))))))
       ffirst))

(defn id-to-str
  "Return the string name of a language, based on its id
  Params:
  * `lang-id` keyword of the language which name is required"
  [lang-id]
  (get-in languages [lang-id :ui-text]))

(defn create-ui-languages
  "Create options for a select component based on language
  Params:
  * none"
  []
  (->> languages
       (map (fn [[lang-id lang]]
              (let [ui-text (:ui-text lang)]
                {:name (name lang-id)
                 :id lang-id
                 :value ui-text})))))
