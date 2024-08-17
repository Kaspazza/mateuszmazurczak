(ns mateuszmazurczak.fe.router-test
  (:require
   [cljs.test         :refer          [deftest is testing]
                      :include-macros true]
   [mateuszmazurczak.fe.router :as sut]))

(deftest start-router-test
  (testing
    "Parsing is returning parameter lang = fr, and path :mateuszmazurczak.routes/privacy"
    (let [match (-> (sut/start-router)
                    (sut/match-from-url "/legal/privacy?lang=fr"))]
      (is (= :mateuszmazurczak.routes/privacy (get-in match [:data :name])))
      (is (= {:lang "fr"} (get-in match [:query-params]))))))
