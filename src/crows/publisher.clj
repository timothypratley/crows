(ns crows.publisher
  (:require [clj-wamp.server :refer [emit-event! broadcast-event! *call-sess-id*]]))


(defn publish [event before after]
  (broadcast-event! "crows/event#world"
                    event
                    1
                    #_exclude-sess-id))

(defn init [world path sess-id]
  (emit-event! (str "crows/event#world" path)
               (get-in world [:root path])
               sess-id))
