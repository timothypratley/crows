(ns crows.eventing
  (:require [crows.publisher :refer [publish]]
            [crows.storage :refer [store]]))


(defmulti accept (fn [domain event] (:event event)))

; TODO: does this namespace own the domain? store/publish should be pluggable
(def domain (atom {}))

(let [o (Object.)
      event-count (atom 0)]

  (defn raise!
    "Raising an event stores it, publishes it, and updates the domain model."
    [event-type event]
    (locking o
      (let [before @domain
            event (assoc event
                    :event event-type
                    :when (java.util.Date.)
                    :seq (swap! event-count inc))
            after (swap! domain accept event)]
        (store event after)
        (publish event before after)))))
