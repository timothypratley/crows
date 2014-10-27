(ns crows.connection-test
  (:require [crows.connection :refer :all]
            [midje.sweet :refer :all]))


(deftest about-connection
  (with-redefs [#'crows.connection/event-action
                (fn [uid action-name args]
                  action-name)]
    (fact (event-msg-handler {:event {:event :foo
                                      :field 1}}
                             nil)
          => :foo)))
