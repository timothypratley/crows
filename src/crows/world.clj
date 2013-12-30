(ns crows.world)


(def terrains [:earth :water :road :forrest :dessert :grass])

(def world
  "The domain, the state of everything."
  (atom {:players {}
         :x [0 :terrain :earth []]}))


(defmulti accept (fn [_ event] (:event event)))

(let [o (Object.)
      event-count (atom 0)]
  (defn raise!
    "Raising an event stores it, publishes it, and updates the world model.
    Publishing allows for external read models to be denormalized, providing CQRS.
    http://martinfowler.com/bliki/CQRS.html"
    [event-type event]
    (locking o
      (let [event (assoc event
                    :event event-type
                    :when (java.util.Date.)
                    :seq (swap! event-count inc))]
        ;(store event)
        ;(publish event)
        (swap! world accept event)))))



(defn rand-terrain
  [x]
  (assoc x 3
         (vec (repeatedly 64 (fn []
                               [(rand) :terrain (rand-nth terrains)])))))

(reduce (fn [acc x]
          (update-in acc [3 x] rand-terrain))
        (rand-terrain @world) (range 64))

(defn update-player-command
  [id location heading]
  (when id
    (raise! :player-update
            {:id id
             :location location
             :heading heading})))

(defmethod accept :player-update
  [world {:keys [id location heading]}]
  (update-in world [:players id]
             #(-> %
               (assoc :location location)
               (assoc :heading heading))))
