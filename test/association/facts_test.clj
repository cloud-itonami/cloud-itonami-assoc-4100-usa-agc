(ns association.facts-test
  (:require [clojure.test :refer [deftest is]]
            [association.facts :as facts]))

(deftest agc-has-spec-basis
  (let [sb (facts/spec-basis "agc")]
    (is (= 2 (count sb)))
    (is (every? #(= "4100" (:association-rule/isic %)) sb))
    (is (every? #(= "USA" (:association-rule/country %)) sb))))

(deftest unknown-association-has-no-spec-basis
  (is (nil? (facts/spec-basis "abc")))
  (is (nil? (facts/spec-basis "zzz"))))

(deftest coverage-is-honest
  (let [c (facts/coverage ["agc" "abc"])]
    (is (= 2 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["abc"] (:missing-associations c)))))

(deftest by-topic-filters
  (is (= ["agc.safety-management-best-practices-2018"]
         (mapv :association-rule/id (facts/by-topic "agc" :worker-safety))))
  (is (empty? (facts/by-topic "agc" :labor)))
  (is (empty? (facts/by-topic "abc" :governance))))
