(ns mateuszmazurczak.i18n.translator
  "Backend translator

  Assemble the chosen translation framework (tempura for now), the dictionary and default language"
  (:require
   [mateuszmazurczak.i18n.dict.resources :as mm-i18n-dict-resources]
   [mateuszmazurczak.i18n.dict.text      :as mm-i18n-dict-text]
   [mateuszmazurczak.translator.tempur   :as be-tempura-translator]))

(def web-be-translator
  "Assemble the chosen translation framework, the dictionary and default language"
  (be-tempura-translator/make-tempura-be-translator
   [:en]
   mm-i18n-dict-text/dict
   mm-i18n-dict-resources/dict))
