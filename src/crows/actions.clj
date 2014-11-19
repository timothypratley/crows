(ns crows.actions
  (:require [crows.domain :refer [accept]]
            [crows.world :refer :all]))

; TODO: schema might be a nice library to use for this
(defn v3? [x]
  (and (vector? x)
       (= 3 (count x))
       (every? number? x)))

(defn v4? [x]
  (and (vector? x)
       (= 4 (count x))
       (every? number? x)))


(defn pose
  [world id location heading velocity]
  {:pre [(get-in world [:entity id])
         (v3? location)
         (v4? heading)
         (v3? velocity)]}
  {:event :pose
   :id id
   :pose {:location location
          :heading heading
          :velocity velocity}})

(defmethod accept :pose
  [world {:keys [id pose]}]
  (update-in world [:entity id :pose] merge pose))


(defn create-landmark
  [world by model location heading]
  {:pre [(get-in world [:entity by])
         (models model)
         (v3? location)
         (v4? heading)]}
  {:event :create-entity
   :entity {:model model
            :location location
            :heading heading}})

(defn create-mobile
  [world by model location heading race specialization characteristics behavior]
  {:pre [(get-in world [:entity by])
         (models model)
         (v3? location)
         (v4? heading)
         (races race)
         (specializations specialization)
         (v4? characteristics)
         (behaviors behavior)]}
  {:event :create-entity
   :entity {:model model
            :location location
            :heading heading
            :race race
            :specialization specialization
            :characteristics characteristics
            :behavior behavior}})

(defmethod accept :create-entity
  [world {:keys [entity]}]
  (-> world
      (assoc-in [:entity (world :next-id)] entity)
      (update-in [:next-id] inc)))
