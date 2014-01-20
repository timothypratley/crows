(ns crows.publisher
  (:require [clj-wamp.server :refer [emit-event! broadcast-event! client-auth]]))


(defn get-sess-id [username]
  (for [[sess-id client] @client-auth
        :when (= (client :key) username)]
    sess-id))

(defn publish [event before after]
  (let [msg (if (= :pose (event :event))
              [(event :id) (event :location) (event :heading)]
              event)]
    (broadcast-event! "crows/event#world"
                      msg
                      (get-sess-id (event :id)))))

(defn init [world path sess-id]
  (emit-event! (str "crows/event#world" path)
               (get-in world [:root path])
               sess-id))
