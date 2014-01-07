(ns crows.publisher
  (:require [clj-wamp.server :refer [send-event!]]))


(defn publish [event]
  (send-event! "crows/event#world" event))
