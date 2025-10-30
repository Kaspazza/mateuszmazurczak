(ns mateuszmazurczak.adapters.i18n.dict.resources-text
  (:require
   #?(:clj [clojure.test :refer [deftest is testing]]
      :cljs [cljs.test :refer [deftest is testing] :include-macros true])
   [clojure.set                                   :as set]
   [clojure.walk                                  :as walk]
   [mateuszmazurczak.adapters.i18n.dict.resources :as mm-i18n-dict-res]))


(defn prefixify-map
  [prefix thing]
  (if (map? thing)
    (set/rename-keys
     thing
     (->> (keys thing)
          (map (fn [k] [k (keyword (str (name prefix) "." (name k)))]))
          (into {})))
    thing))

(defn prefixify-vec
  [prefix thing]
  (let [rename
        (fn [el]
          (if (map? el)
            (seq (set/rename-keys
                  el
                  (->> (keys el)
                       (map
                        (fn [k] [k (keyword (str (name prefix) "." (name k)))]))
                       (into {}))))
            el))]
    (if (vector? thing)
      (let [maps (filter map? thing)
            non-maps (remove map? thing)]
        (merge (->> (map rename maps)
                    (apply concat)
                    (group-by key)
                    (map (fn [[k vs]] {k (map second vs)}))
                    (into {}))
               (when (seq non-maps) {prefix non-maps})))
      thing)))

(defn prefixify-children
  [thing]
  (if (map? thing)
    (->> thing
         (map (fn [[k v]]
                (cond
                  (map? v) (let [prefixed (prefixify-map k v)]
                             (if (map? prefixed) prefixed {k v}))
                  (vector? v) (let [prefixed (prefixify-vec k v)]
                                (if (map? prefixed) prefixed {k v}))
                  :else {k v})))
         (apply merge))
    thing))

(defn crush
  "Crush the map"
  [m]
  (when (map? m)
    (->> (walk/postwalk prefixify-children m)
         (map (fn [[k v]] {k (if (sequential? v) (flatten v) v)}))
         (into {}))))

(defn language-report
  "For all keys of a dictionnary, return the list of languages set
  `expected-languages` is the languages sequence the report is limited to"
  [dictionary expected-languages]
  (let [filtered-dictionary (select-keys dictionary expected-languages)]
    (apply merge-with
           set/union
           (map (fn [[language dict-map]]
                  (into {}
                        (map (fn [v] [v #{language}]) (keys (crush dict-map)))))
                filtered-dictionary))))

(defn key-with-missing-languages
  "Return a map with the path to a translation, with the list of existing languages
  key-exceptions is a sequence or set of all keys that should be excluded from the error list
  `expected-languages` is the languages the report is limited to"
  [dictionary expected-languages key-exceptions]
  (let [key-set-exceptions (into #{} key-exceptions)]
    (filter (fn [[k v]]
              (and (not (contains? key-set-exceptions k))
                   (not= v expected-languages)))
            (language-report dictionary expected-languages))))

(def languages (into #{} (keys mm-i18n-dict-res/dict)))

(deftest mateuszmazurczak-dictionary
  (testing
    (apply
     str
     "Dictionary is matching all expecting languages, list all languages, expect "
     languages)
    (is (= []
           (key-with-missing-languages mm-i18n-dict-res/dict languages #{})))))
