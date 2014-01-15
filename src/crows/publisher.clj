(ns crows.publisher
  (:require [clj-wamp.server :refer [emit-event!]]))


(defn publish [event before after]
  (emit-event! "crows/event#world" event nil))
