(ns clj-crawler.core-test
  (:require [clojure.test :refer :all]
            [clj-crawler.core :refer :all]))

;;(deftest a-test
;;  (testing "FIXME, I fail."
;;    (is (= 0 1))))

;; TODO is there a way to create a list of empty URL or invalid URL and run the test agains all values
;; in that list
(deftest validate-start-url-test
  (let [missing-error ["Missing Start URL."]
        invalid-error ["Invalid Start URL"]
        no-error []]
      (testing "validate-start-url"
          (testing "for empty URL"
            (is (= (validate-start-url nil) missing-error))
            (is (= (validate-start-url "") missing-error))
            (is (= (validate-start-url "  ") missing-error)))
          (testing "for invalid URL"
            (is (= (validate-start-url "a") invalid-error))
            (is (= (validate-start-url "url://www.google.com") invalid-error))
            (is (= (validate-start-url "https://www.google.com") invalid-error)))
          (testing "for valid URL"
            (is (= (validate-start-url "http://www.google.com") no-error))))))

(deftest validate-max-urls-test
  (let [invalid-error ["Invalid Max URLs value."]
        no-error []]
    (testing "validate-max-urls"
      (testing "for invalid value"
        (is (= (validate-max-urls "abc") invalid-error)))
      (testing "for valid value"
        (is (= (validate-max-urls "1") no-error))
        (is (= (validate-max-urls "10") no-error))
        (is (= (validate-max-urls "500") no-error))))))
