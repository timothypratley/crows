(ns crows.world)


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
  {:next-id 1
   :root (rand-terrain [:terrain :earth []])
   :users {}})
