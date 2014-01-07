(ns crows.ticker
  (:require [wamp :refer [send-event!]]))


(defn tick
  []


  (send-event! topic event))