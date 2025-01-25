(ns mateuszmazurczak.navigation.routes-test
  (:require
   [cljs.test                          :refer          [deftest is testing]
                                       :include-macros true]
   [mateuszmazurczak.navigation.router :as mm-nav-router]
   [reitit.core                        :as reitit]))

;; Use this to show expanded routes
(reitit/routes (:router @mm-nav-router/router))
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
               mm-nav-router/match-from-url
               mm-nav-router/panel-id))))
  (testing "homepage found"
    (is (= :panels/home
           (-> "/"
               mm-nav-router/match-from-url
               mm-nav-router/panel-id)
           (-> ""
               mm-nav-router/match-from-url
               mm-nav-router/panel-id)
           (-> nil
               mm-nav-router/match-from-url
               mm-nav-router/panel-id)
           (-> (:be-page "/")
               mm-nav-router/match-from-url
               mm-nav-router/panel-id)
           (-> "http://localhost:3000/"
               mm-nav-router/match-from-url
               mm-nav-router/panel-id))))
  (testing "sub page"
    (is (= :panels/articles
           (-> "http://localhost:3000/articles"
               mm-nav-router/match-from-url
               mm-nav-router/panel-id))))
  (testing "Parameters are compatible with spa pagees"
    (is (= {:par "foobar"}
           (->> "?par=foobar#"
                (:be-page "/")
                mm-nav-router/match-from-url
                mm-nav-router/url-params)))
    (is (= {:par "foobar"}
           (->> (str "/articles?par=foobar#fe##fe2")
                mm-nav-router/match-from-url
                mm-nav-router/url-params)))
    (is (= {:par "foobar"}
           (-> (str "/articles?par=foobar#fe##fe2")
               mm-nav-router/match-from-url
               mm-nav-router/url-params)))))
