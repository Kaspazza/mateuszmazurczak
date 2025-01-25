(ns mateuszmazurczak.i18n.translator
  "Backend translator

  Assemble the chosen translation framework (tempura for now), the dictionary and default language"
  (:require
   [automaton-web.i18n.be.translator.tempura :as be-tempura-translator]
   [mateuszmazurczak.i18n.dict.resources     :as mm-i18n-dict-resources]
   [mateuszmazurczak.i18n.dict.text          :as mm-i18n-dict-text]))

(def web-be-translator
  "Assemble the chosen translation framework, the dictionary and default language"
  (be-tempura-translator/make-tempura-be-translator
   [:en]
   mm-i18n-dict-text/dict
   mm-i18n-dict-resources/dict))
