(ns mateuszmazurczak.routes-test
  (:require
   [automaton-web.routes    :as web-routes]
   [mateuszmazurczak.routes :as sut]
   #?(:clj [clojure.test :refer [deftest is testing]]
      :cljs [cljs.test :refer [deftest is testing] :include-macros true])))

(deftest routes-test
  (testing "backend route parsing is exception free"
    (is (web-routes/parse-routes :be sut/routes)))
  (testing "frontend route parsing is exception free"
    (is (web-routes/parse-routes :fe sut/routes))))
