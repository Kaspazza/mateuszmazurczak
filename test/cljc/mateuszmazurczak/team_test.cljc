(ns mateuszmazurczak.team-test
  (:require
   [mateuszmazurczak.team :as sut]
   #?(:clj [clojure.test :refer [deftest is testing]]
      :cljs [cljs.test :refer [deftest is testing] :include-macros true])))

(deftest teammates-map
  (testing "Teammates map is containing all required keys."
    (is (= true
           (every? #(every? % [:id :name :img :title :description])
                   sut/teammates)))))
