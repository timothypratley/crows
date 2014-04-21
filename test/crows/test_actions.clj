(ns crows.test-actions
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [crows.actions :refer :all]))


(def world {:entity {1 {}}})

(deftest about-actions
  (fact (pose world 1 [1 2 3] [1 2 3 4] [1 2 3]) => map?))
