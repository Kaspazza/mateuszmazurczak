(ns mateuszmazurczak.i18n
  "I18n port - public API for translation functionality"
  (:require
   [mateuszmazurczak.i18n.dict.resources :as mm-i18n-dict-res]
   [mateuszmazurczak.i18n.dict.text      :as mm-i18n-dict-txt]
   [mateuszmazurczak.i18n.tempura        :as i18n-tempura]
   [taoensso.tempura                     :as tempura]))

(defn create-translator
  "Creates a translator function configured for the environment.
  Uses tempura adapter internally.
  Params:
  * `debug?` - boolean indicating whether to enable debug mode (no caching)"
  [debug?]
  (let [translation-opts (i18n-tempura/create-opts debug?
                                                   mm-i18n-dict-txt/dict
                                                   mm-i18n-dict-res/dict)]
    (partial tempura/tr translation-opts)))

(defn tr
  "Helper function for using a translator with common pattern.
  Params:
  * `translator` - translator function created by create-translator
  * `language` - keyword or vector of language fallbacks  
  * `id` - translation key"
  [translator language id]
  (translator (if (vector? language) language [language]) [id]))
