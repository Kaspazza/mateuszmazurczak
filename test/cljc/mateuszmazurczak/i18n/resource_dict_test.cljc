(ns mateuszmazurczak.i18n.resource-dict-test
  (:require
   [automaton-core.i18n.missing-translation-report :as b-language]
   [mateuszmazurczak.i18n.dict.resources                    :as sut]
   #?(:clj [clojure.test :refer [deftest is testing]]
      :cljs [cljs.test :refer [deftest is testing] :include-macros true])))

(def languages (into #{} (keys sut/dict)))

(deftest mateuszmazurczak-dictionary
  (testing
    (apply
     str
     "Dictionary is matching all expected languages, list of languages to expect"
     languages)
    (is (= [] (b-language/key-with-missing-languages sut/dict languages #{})))))
