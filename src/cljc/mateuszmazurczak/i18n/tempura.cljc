(ns mateuszmazurczak.i18n.tempura)

(defn deep-merge
  "Deep merge nested maps.
  Last map has higher priority

  This code comes from this [gist](https://gist.github.com/danielpcox/c70a8aa2c36766200a95)"
  [& maps]
  (apply merge-with
         (fn [& args]
           (if (every? #(or (map? %) (nil? %)) args)
             (apply deep-merge args)
             (last args)))
         maps))

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
  (apply deep-merge tempura-missing-text dicts))

(defn create-opts
  "Create the options for tempura/tr
  Params:
  * `dicts` list of dictionaries to append together, the default keys for missing keys and the core dictionary are defaulted"
  [& dicts]
  (let [debug true ;; (= :dev (conf-core/read-param [:env]))
       ]
    {:dict (append-dictionaries dicts)
     :cache-dict? (not debug)
     :default-local :en
     :cache-locales (not debug)}))
