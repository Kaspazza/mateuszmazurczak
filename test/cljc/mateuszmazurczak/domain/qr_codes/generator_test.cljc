(ns mateuszmazurczak.domain.qr-codes.generator-test
  (:require
   [clojure.string                             :as str]
   [clojure.test                               :refer [deftest is testing]]
   [mateuszmazurczak.domain.qr-codes.generator :as gen]))

(deftest parse-input-test
  (testing "parses newline-separated values"
    (is (= ["foo" "bar" "baz"] (gen/parse-input "foo\nbar\nbaz"))))
  (testing "trims whitespace" (is (= ["foo" "bar"] (gen/parse-input "  foo  \n  bar  "))))
  (testing "removes empty lines" (is (= ["foo" "bar"] (gen/parse-input "foo\n\n\nbar\n\n"))))
  (testing "handles nil input" (is (nil? (gen/parse-input nil))))
  (testing "handles empty input" (is (= [] (gen/parse-input "")))))

(deftest valid-size-test
  (testing "valid sizes"
    (is (gen/valid-size? 100))
    (is (gen/valid-size? 300))
    (is (gen/valid-size? 1000))
    (is (gen/valid-size? 2000)))
  (testing "invalid sizes"
    (is (not (gen/valid-size? 49)))
    (is (not (gen/valid-size? 2001)))
    (is (not (gen/valid-size? -1)))
    (is (not (gen/valid-size? "300")))))

(deftest sanitize-filename-test
  (testing "sanitizes special characters"
    (is (= "qr_001_https_example_com.png" (gen/sanitize-filename "https://example.com" 0))))
  (testing "truncates long content"
    (let [long-content (apply str (repeat 100 "a"))
          filename (gen/sanitize-filename long-content 0)]
      (is (< (count filename) 60)))))

(deftest validate-request-test
  (testing "valid request"
    (let [result (gen/validate-request {:contents ["test"]
                                        :size 300
                                        :format :zip})]
      (is (:valid? result))
      (is (empty? (:errors result)))))
  (testing "empty contents"
    (let [result (gen/validate-request {:contents []
                                        :size 300
                                        :format :zip})]
      (is (not (:valid? result)))
      (is (some #(str/includes? % "No QR code") (:errors result)))))
  (testing "invalid size"
    (let [result (gen/validate-request {:contents ["test"]
                                        :size 10
                                        :format :zip})]
      (is (not (:valid? result)))))
  (testing "invalid format"
    (let [result (gen/validate-request {:contents ["test"]
                                        :size 300
                                        :format :invalid})]
      (is (not (:valid? result))))))

#?(:clj (deftest generate-qr-matrix-test
          (testing "generates valid matrix"
            (let [result (gen/generate-qr-matrix "test")]
              (is (map? result))
              (is (pos? (:size result)))
              (is (vector? (:matrix result)))
              (is (= (:size result) (count (:matrix result))))))))

#?(:clj (deftest generate-qr-svg-test
          (testing "generates valid SVG"
            (let [svg (gen/generate-qr-svg "https://example.com")]
              (is (string? svg))
              (is (str/includes? svg "<svg"))
              (is (str/includes? svg "</svg>"))
              (is (str/includes? svg "<path"))))))

#?(:clj (deftest generate-batch-test
          (testing "generates batch of QR codes"
            (let [result (gen/generate-batch ["one" "two" "three"])]
              (is (:success result))
              (is (= 3 (count (:codes result))))
              (is (every? :svg (:codes result)))
              (is (every? :filename (:codes result)))))
          (testing "fails with empty input"
            (let [result (gen/generate-batch [])]
              (is (not (:success result)))
              (is (seq (:errors result)))))))
