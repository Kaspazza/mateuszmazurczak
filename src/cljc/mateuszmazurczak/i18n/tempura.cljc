(ns mateuszmazurczak.i18n.tempura
  (:require
   [mateuszmazurczak.configuration :as conf]
   [mateuszmazurczak.i18n.language :as i18n-lang]
   [mateuszmazurczak.utils.map     :as utils-map]))

(def tempura-missing-text
  "Necessary for tempura,  a missing key is expected for all languages marked with `:core-dict?`"
  {:en
   {:missing
    "The text is missing! :( Please let me know at mateusz.mazurczak.dev@gmail.com"}
   :pl
   {:missing
    "Brakuje tłumaczenia tego tekstu! :( Jeśli widzisz tę wiadomość proszę napisz na mateusz.mazurczak.dev@gmail.com"}})

(defn- append-dictionaries
  "Appends dictionaries
  Params:
  * `dicts` list of dictionaries to append together, the default keys for missing keys and the core dictionary are defaulted"
  [dicts]
  (apply utils-map/deep-merge tempura-missing-text dicts))

(defn create-opts
  "Create the options for tempura/tr
  Params:
  * `dicts` list of dictionaries to append together, the default keys for missing keys and the core dictionary are defaulted"
  [& dicts]
  (let [debug (= :development (conf/read-param [:env]))]
    {:dict (append-dictionaries dicts)
     :cache-dict? (not debug)
     :default-local (first i18n-lang/main-langs)
     :cache-locales (not debug)}))
