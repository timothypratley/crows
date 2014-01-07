(ns crows.world
  (:require [crows.eventing :refer [accept raise!]]))


(def terrains [:earth :water :road :forrest :dessert :grass])

(defn- rand-terrain
  [x]
  (assoc x 3
         (vec (repeatedly 64 (fn []
                               [(rand) :terrain (rand-nth terrains)])))))

#_(reduce (fn [acc x]
          (update-in acc [3 x] rand-terrain))
        (rand-terrain @world) (range 64))

(defn new-world
  "The domain, the state of everything."
  []
  {:players {}
   :x (rand-terrain [0 :terrain :earth []])})

(defn update-player-command
  [system id location heading]
  (when id
    (raise! system :player-update
            {:id id
             :location location
             :heading heading})))

(defmethod accept :player-update
  [world {:keys [id location heading]}]
  (update-in world [:players id]
             #(-> %
               (assoc :location location)
               (assoc :heading heading))))
