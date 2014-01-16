(ns crows.events
  (:require [crows.domain :refer [accept]]))


(defn pose
  [world id location heading]
  (when id
    {:event :pose
     :id id
     :location location
     :heading heading}))

(defmethod accept :pose
  [world {:keys [id location heading]}]
  (update-in world [:players id]
             (fn set-pose [player]
               (-> player
                   (assoc :location location)
                   (assoc :heading heading)))))


(defn create-landmark
  [world player-id model-id location heading]
  (when player-id
    {:event :create-landmark
     :model-id model-id
     :location location
     :heading heading}))

(defmethod accept :create-landmark
  [world {:keys [model-id location heading]}]
  (-> world
      (assoc-in [:landmarks (world :next-id)]
                {:model-id model-id
                 :location location
                 :heading heading})
      (update-in [:next-id] inc)))
