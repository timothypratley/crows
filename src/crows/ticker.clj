(ns crows.ticker
  (:require [clj-wamp.server :refer [send-event!]]))


(defn tick
  []
  (send-event! "crows/event#world" "tick"))