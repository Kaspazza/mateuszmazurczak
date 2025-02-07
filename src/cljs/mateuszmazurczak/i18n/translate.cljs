(ns mateuszmazurczak.i18n.translate
  "Frontend translation for mateuszmazurczak"
  (:require
   [mateuszmazurczak.i18n.dict.resources :as mm-i18n-dict-res]
   [mateuszmazurczak.i18n.dict.text      :as mm-i18n-dict-txt]
   [mateuszmazurczak.i18n.events         :as mm-i18n-evts]
   [mateuszmazurczak.i18n.language       :as mm-language]
   [mateuszmazurczak.i18n.tempura        :as tempura-translator]
   [mateuszmazurczak.navigation.utils    :as mm-nav-utils]
   [mateuszmazurczak.utils.cookies       :as mm-cookies]
   [re-frame.core                        :as rf]
   [taoensso.tempura                     :as tempura]))

(def main-langs "List of main languages the first matching is used" [:en :pl])

(def tempura-opts
  (tempura-translator/create-opts mm-i18n-dict-txt/dict mm-i18n-dict-res/dict))

(defn tr
  "translate the `:tr-id` with the resources as parameters (first resource is %1, second is %2, ...), trying to translate with first language lang-ids, then second, ...
  * `tr-id` is a translation id
  * `resources` all translation resources to translate, all `%1` in the translation will be replaced with the first element of resources and so on..."
  [tr-id & resources]
  (let [resources (cond
                    (empty? resources) []
                    (seq? resources) (vec resources)
                    :else [resources])
        lang (some-> (rf/subscribe [::mm-i18n-evts/lang])
                     deref)
        translated-text (tempura/tr tempura-opts
                                    (vec (concat (when (keyword? lang) [lang])
                                                 main-langs))
                                    [tr-id]
                                    resources)]
    translated-text))

(defn init-lang
  "Init the language to start a frontend instance with
  Is a proxy to the mateuszmazurczak translator init-lang function
  Current tempura implementation refer to url, defaulted to the language defined above"
  []
  (let [par-lang (-> (mm-nav-utils/current-url)
                     mm-nav-utils/lang-in-url-par
                     mm-language/ui-str-to-id)
        cookies-lang_ (delay (-> "lang"
                                 mm-cookies/get-cookie-val
                                 mm-language/ui-str-to-id))
        main-lang main-langs]
    (if-let [language (or par-lang @cookies-lang_ main-lang)]
      language
      (let [default-language (-> main-langs
                                 first)]
        default-language))))

(defn tr-for-key
  "Apply tr to each map in the sequence
  Params:
  * `maps` is a sequence of map"
  [maps k]
  (mapv (fn [m] (assoc m k (tr (get m k)))) maps))
