(ns crows.publisher
  (:require [clj-wamp.server :refer [send-event! broadcast-event! *call-sess-id*]]))


(defn publish [event sess-id]
  (println "SES" sess-id)
  (broadcast-event! "crows/event#world" "hi" sess-id))
