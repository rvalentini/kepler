(ns kepler.core-test
    (:require
     [cljs.test :refer-macros [deftest is testing]]))

(deftest multiply-test
  (is (= (* 1 2) 2)))

(deftest multiply-test-2
  (is (= (* 75 10) 750)))
