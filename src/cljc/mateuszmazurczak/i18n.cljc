(ns mateuszmazurczak.i18n
  (:require
   [mateuszmazurczak.i18n.dict.resources :as mm-i18n-dict-res]
   [mateuszmazurczak.i18n.dict.text      :as mm-i18n-dict-txt]
   [mateuszmazurczak.i18n.tempura        :as i18n-tempura]
   [taoensso.tempura                     :as tempura]))

(def translation-opts
  (i18n-tempura/create-opts mm-i18n-dict-txt/dict mm-i18n-dict-res/dict))

(def tr*
  "Cached translation function with dictionaries"
  (partial tempura/tr translation-opts))

(defn tr [language id] (tr* (if (vector? language) language [language]) [id]))
