(ns crows.eventing)


(defmulti accept (fn [world event] (:event event)))

(let [o (Object.)
      event-count (atom 0)]

  (defn raise!
    "Raising an event stores it, publishes it, and updates the world model."
    [system event-type event]
    (locking o
      (let [event (assoc event
                    :event event-type
                    :when (java.util.Date.)
                    :seq (swap! event-count inc))]
        ((system :store) event)
        ((system :publish) event)
        (swap! (system :world) accept event)))))
