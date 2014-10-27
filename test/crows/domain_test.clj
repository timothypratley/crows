(ns crows.domain-test
  (:require [crows.domain :refer :all]
            [midje.sweet :refer :all]))


(deftest about-domain
  (fact (do
          (reset! actions #{})
          (reset! actions-by-name {})
          (attach-actions crows.domain-test)
          actions-by-name)
        => {:about-domain about-domain}))
