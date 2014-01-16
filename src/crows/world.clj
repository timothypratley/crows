(ns crows.world)


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
  {:next-id 1
   :players {}
   :x (rand-terrain [0 :terrain :earth []])})
