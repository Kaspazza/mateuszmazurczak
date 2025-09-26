(ns mateuszmazurczak.i18n
  "I18n port - public API for translation functionality"
  (:require
   [mateuszmazurczak.i18n.dict.resources :as mm-i18n-dict-res]
   [mateuszmazurczak.i18n.dict.text      :as mm-i18n-dict-txt]
   [mateuszmazurczak.i18n.tempura        :as i18n-tempura]
   [mateuszmazurczak.validation          :as validation]
   [taoensso.tempura                     :as tempura]))

(defn create-translator
  "Creates a translator function configured for the environment.
  Uses tempura adapter internally.
  Params:
  * `debug?` - boolean indicating whether to enable debug mode (no caching)"
  [debug?]
  {:pre [(boolean? debug?)]}
  (try (let [translation-opts (i18n-tempura/create-opts debug?
                                                        mm-i18n-dict-txt/dict
                                                        mm-i18n-dict-res/dict)]
         (partial tempura/tr translation-opts))
       (catch #?(:clj Exception
                 :cljs :default)
         e
         (throw (ex-info "Failed to create translator" {:debug? debug?} e)))))

(defn tr
  "Helper function for using a translator with common pattern.
  Params:
  * `translator` - translator function created by create-translator
  * `language` - keyword or vector of language fallbacks  
  * `id` - translation key"
  [translator language id]
  {:pre
   [(fn? translator) (or (keyword? language) (vector? language)) (keyword? id)]}
  (try (translator (if (vector? language) language [language]) [id])
       (catch #?(:clj Exception
                 :cljs :default)
         e
         (throw (ex-info "Failed to translate text"
                         {:language language
                          :id id}
                         e)))))
