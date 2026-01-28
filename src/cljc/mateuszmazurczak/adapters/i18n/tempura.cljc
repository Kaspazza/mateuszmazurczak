(ns mateuszmazurczak.adapters.i18n.tempura
  "Tempura adapter for i18n - implements translation using taoensso/tempura"
  (:require
   [mateuszmazurczak.adapters.i18n.protocol :as p]
   [mateuszmazurczak.domain.i18n.language   :as i18n-lang]
   [mateuszmazurczak.utils.map              :as utils-map]
   [taoensso.tempura                        :as tempura]))

(def ^:private tempura-missing-text
  "Necessary for tempura, a missing key is expected for all languages marked with `:core-dict?`"
  {:en {:missing "The text is missing! :( Please let me know at mateusz.mazurczak.dev@gmail.com"}
   :pl
   {:missing
    "Brakuje tłumaczenia tego tekstu! :( Jeśli widzisz tę wiadomość proszę napisz na mateusz.mazurczak.dev@gmail.com"}})

(defn- append-dictionaries
  "Appends dictionaries.
  
  Params:
  * `dicts` - list of dictionaries to append together, the default keys for missing keys and the core dictionary are defaulted"
  [dicts]
  (apply utils-map/deep-merge tempura-missing-text dicts))

(defn- create-tempura-opts
  "Create the options for tempura/tr.
  
  Params:
  * `debug?` - boolean indicating whether to enable debug mode (no caching)
  * `dicts` - list of dictionaries to append together"
  [debug? & dicts]
  {:dict (append-dictionaries dicts)
   :cache-dict? (not debug?)
   :default-local (first i18n-lang/main-langs)
   :cache-locales (not debug?)})

(defn- params-map->vec
  "Convert params map to Tempura vector format.
   Map keys like :1, :2, :3 become vector positions [val1 val2 val3].
   This allows port to use maps while adapter converts to Tempura's vector format."
  [params-map]
  (when params-map (let [sorted-keys (sort (keys params-map))] (mapv params-map sorted-keys))))

(defrecord TempuraTranslator [translation-opts]
  p/Translator
    (-translate [_ language id]
      (try (tempura/tr translation-opts (if (vector? language) language [language]) [id])
           (catch #?(:clj Exception
                     :cljs :default)
             e
             (throw (ex-info "Failed to translate text"
                             {:language language
                              :id id}
                             e)))))
    (-translate [_ language id params]
      (try (let [params-vec (params-map->vec params)
                 locales (if (vector? language) language [language])]
             (tempura/tr translation-opts locales [id] params-vec))
           (catch #?(:clj Exception
                     :cljs :default)
             e
             (throw (ex-info "Failed to translate text with params"
                             {:language language
                              :id id
                              :params params}
                             e))))))

(defn make-translator
  "Creates a Tempura translator instance.
  
  Params:
  * `debug?` - boolean indicating whether to enable debug mode (no caching)
  * `dicts` - variable number of dictionaries to merge"
  [debug? & dicts]
  (when-not (boolean? debug?)
    (throw (ex-info "debug? must be a boolean"
                    {:type ::invalid-debug-flag
                     :provided debug?})))
  (try (let [translation-opts (apply create-tempura-opts debug? dicts)]
         (->TempuraTranslator translation-opts))
       (catch #?(:clj Exception
                 :cljs :default)
         e
         (throw (ex-info "Failed to create translator" {:debug? debug?} e)))))
