(ns mateuszmazurczak.i18n.fe.translate
  "Frontend translation for mateuszmazurczak"
  (:require
   [automaton-web.i18n.fe.translator         :as fe-translator]
   [automaton-web.i18n.fe.translator.tempura :as fe-tempura-translator]
   [mateuszmazurczak.i18n.dict.resources     :as
                                             mateuszmazurczak-dict-resources]
   [mateuszmazurczak.i18n.dict.text          :as mateuszmazurczak-dict-text]
   [mateuszmazurczak.i18n.language           :as mateuszmazurczak-language]))

(def main-langs "List of main languages the first matching is used" [:fr])

(def mateuszmazurczak-tempura-translator
  "Instance of `fe-tempura-translator/FeTempuraTranslator` for mateuszmazurczak frontend
  Decides what dictionaries to use and the default language for mateuszmazurczak"
  (fe-tempura-translator/make-fe-tempura-translator
   main-langs
   mateuszmazurczak-language/ui-str-to-id
   mateuszmazurczak-dict-text/dict
   mateuszmazurczak-dict-resources/dict))

(defn tr
  "Translate a translation id
  * `tr-id` is a translation id
  * `resources` all translation resources to translate, all `%1` in the translation will be replaced with the first element of resources and so on..."
  [tr-id & resources]
  (fe-translator/translate mateuszmazurczak-tempura-translator
                           tr-id
                           (cond
                             (empty? resources) []
                             (seq? resources) (vec resources)
                             :else [resources])))

(defn init-lang
  "Init the language to start a frontend instance with
  Is a proxy to the mateuszmazurczak translator init-lang function
  Current tempura implementation refer to url, defaulted to the language defined above"
  []
  (fe-translator/init-lang mateuszmazurczak-tempura-translator))

(defn lang [] (fe-translator/lang mateuszmazurczak-tempura-translator))

(defn tr-for-key
  "Apply tr to each map in the sequence
  Params:
  * `maps` is a sequence of map"
  [maps k]
  (mapv (fn [m] (assoc m k (tr (get m k)))) maps))
