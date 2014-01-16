(ns crows.world
  (:require [crows.eventing :refer [accept raise!]]))


(def terrains [:earth :water :road :forrest :dessert :grass])

(defn- rand-terrain
  [x]
  (assoc x 3
         (mapv vector (repeat :terrain) (repeatedly 64 #(rand-nth terrains)))))

#_(reduce (fn [acc x]
          (update-in acc [3 x] rand-terrain))
        (rand-terrain @world) (range 64))

(defn new-world
  "The domain, the state of everything."
  []
  {:root (rand-terrain [:terrain :earth []])
   :players {}})

(let [max-id (atom 0)]
  (defn next-id []
    (swap! max-id inc)))

(defn update-player-command
  [system id location heading sess-id]
  (when id
    (raise! system :player-update
            {:id id
             :location location
             :heading heading}
            sess-id)))

(defmethod accept :player-update
  [world {:keys [id location heading]}]
  (update-in world [:players id]
             #(-> %
               (assoc :location location)
               (assoc :heading heading))))

(defn add-landmark-command
  [system player-id model-id location heading]
  (raise! system :add-landmark
          {:model-id model-id
           :location location
           :heading heading}))

(defmethod accept :add-landmark
  [world {:keys [model-id location heading]}]
  (assoc-in world [:landmarks (next-id)]
            {:model-id model-id
             :location location
             :heading heading}))
