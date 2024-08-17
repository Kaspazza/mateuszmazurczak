(ns mateuszmazurczak.fe.routes-test
  (:require
   [cljs.test                  :refer          [deftest is testing]
                               :include-macros true]
   [mateuszmazurczak.fe.router :as mateuszmazurczak-fe-router]
   [reitit.core                :as reitit]))

;; Use this to show expanded routes
(reitit/routes (:router @mateuszmazurczak-fe-router/router))
;[["" {:name :mateuszmazurczak.routes/root, :panel-id :panels/home}]
; ["/" {:name :mateuszmazurczak.routes/home, :panel-id :panels/home}]
; ["/legal/disclaimer"
;  {:name :mateuszmazurczak.routes/disclaimer, :panel-id :panels/disclaimer}]
; ["/legal/privacy" {:name :mateuszmazurczak.routes/privacy, :panel-id
; :panels/privacy}]]

(deftest routes-test
  (testing "non matching routes"
    (is (= :panels/not-found
           (-> "/non-existing-path"
               mateuszmazurczak-fe-router/match-from-url
               mateuszmazurczak-fe-router/panel-id))))
  (testing "homepage found"
    (is (= :panels/home
           (-> "/"
               mateuszmazurczak-fe-router/match-from-url
               mateuszmazurczak-fe-router/panel-id)
           (-> ""
               mateuszmazurczak-fe-router/match-from-url
               mateuszmazurczak-fe-router/panel-id)
           (-> nil
               mateuszmazurczak-fe-router/match-from-url
               mateuszmazurczak-fe-router/panel-id)
           (-> (:be-page "/")
               mateuszmazurczak-fe-router/match-from-url
               mateuszmazurczak-fe-router/panel-id)
           (-> "http://localhost:3000/"
               mateuszmazurczak-fe-router/match-from-url
               mateuszmazurczak-fe-router/panel-id))))
  (testing "sub page"
    (is (= :panels/disclaimer
           (-> "http://localhost:3000/legal/disclaimer"
               mateuszmazurczak-fe-router/match-from-url
               mateuszmazurczak-fe-router/panel-id))))
  (testing "Parameters are compatible with spa pagees"
    (is (= {:par "foobar"}
           (->> "?par=foobar#"
                (:be-page "/")
                mateuszmazurczak-fe-router/match-from-url
                mateuszmazurczak-fe-router/url-params)))
    (is (= {:par "foobar"}
           (->> (str "/legal/disclaimer?par=foobar#fe##fe2")
                mateuszmazurczak-fe-router/match-from-url
                mateuszmazurczak-fe-router/url-params)))
    (is (= {:par "foobar"}
           (-> (str "/legal/disclaimer?par=foobar#fe##fe2")
               mateuszmazurczak-fe-router/match-from-url
               mateuszmazurczak-fe-router/url-params)))))
