(ns crows.world
  (:require [[clojure.core.matrix :refer :all]]))


(def world
  "The domain, the state of everything."
  (atom nil))

(defn update-drone-command
  [drone-id location heading]
  (raise! :drone-update
          {:drone-id drone-id
           :heading heading
           :location location}))

(defmethod accept :drone-update
  [world {:keys [drone-id location heading]}]
  (update-in world [:drones drone-id]
             #(-> %
               (assoc :location location)
               (assoc :heading heading))))



(defn can-complete-mission
  [drone]
  (and (drone :cargo)
       (= (drone :location)
          (go-to @world drone))))

(defn complete-mission-command
  [drone]
  (if (can-complete-mission drone)
    (raise! :mission-complete
            {:drone-id (drone :id)})))

(defmethod accept :mission-complete
  [world e]
  (-> world
      (update-in [:missions] disj (get-in world [:drone (e :drone-id) :mission]))
      (update-in [:drones (e :drone-id)] dissoc :mission :cargo)))



; TODO: using @world is cheezy
(defn can-pickup
  [drone]
  (and (not (drone :cargo))
       (if (medicine (get-in drone [:mission :cargo]))
         (some #(= (drone :location) %)
               (map :location (@world :hospitals)))
         (= (drone :location)
            (get-in drone [:mission :remote :location])))))

(defn pickup-command
  [drone]
  (if (can-pickup drone)
    (raise! :pickup
            {:drone-id (drone :id)})))

;TODO: flag the cargo as taken in the mission??
(defmethod accept :pickup
  [world e]
  (assoc-in world [:drones (e :drone-id) :cargo]
            (get-in world [:drones (e :drone-id) :mission :cargo])))



(defn assign-mission-command
  [drone mission]
  (raise! :mission-assigned
          {:drone-id (drone :id)
           :mission mission}))

(defmethod accept :mission-assigned
  [world e]
  (assoc-in world [:drones (e :drone-id) :mission]
            (e :mission)))



(defn create-mission-command
  [mission]
  (raise! :mission-created
          {:mission mission}))

(defmethod accept :mission-created
  [world e]
  (update-in world [:missions]
             (fnil conj #{}) (e :mission)))

