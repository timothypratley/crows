(ns crows.test-actions
  (:require [midje.sweet :refer :all]
            [crows.actions :refer :all]))


(def world {})
(facts "About actions"
       (fact (pose world :id [1 2 3] [1 2 3 4] [1 2 3]) => world))
