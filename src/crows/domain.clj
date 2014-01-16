(ns crows.domain
  #_(:require [com.stuartsierra.component :refer [Lifecycle]]))


(defmulti accept (fn [domain event] (:event event)))

; TODO: does this namespace own the domain? store/publish should be pluggable
(def domain (atom {}))


#_(defrecord Domain [model store publish]
  Lifecycle
  (start [component])
  (stop [component])
  )

(let [domain (atom {})
      o (Object.)
      event-count (atom 0)]

  (defn raise!
    "Raising an event stores it, publishes it, and updates the domain model."
    [event]
    (locking o
      (let [before @domain
            event (assoc event
                    :when (java.util.Date.)
                    :seq (swap! event-count inc))
            after (swap! domain accept event)]
        #_(store event after)
        #_(publish event before after)
        (event :seq)))))
