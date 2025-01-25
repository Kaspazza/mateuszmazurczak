(ns mateuszmazurczak.navigation.router-test
  (:require
   [cljs.test                          :refer          [deftest is testing]
                                       :include-macros true]
   [mateuszmazurczak.navigation.router :as sut]))

(deftest start-router-test
  (testing
    "Parsing is returning parameter lang = en, and path :mateuszmazurczak.routes/articles"
    (let [match (-> (sut/start-router)
                    (sut/match-from-url "/articles?lang=en"))]
      (is (= :mateuszmazurczak.routes/articles (get-in match [:data :name])))
      (is (= {:lang "en"} (get-in match [:query-params]))))))
