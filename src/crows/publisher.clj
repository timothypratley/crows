(ns crows.publisher
  (:require [wamp :refer [send-event!]]))


(defn publish
  [event]
  (send-event! (:event event) (dissoc event :event)))