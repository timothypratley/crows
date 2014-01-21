(ns crows.world)


(def terrains [:earth :water :road :forrest :dessert :grass])

(def races {:bird {:characteristics [10 1 1 1]}
            :quadruped {:characteristics [5 5 5 5]}
            :human {:characteristics [3 7 7 7]}})

(def specializations {:melee {:abilities [:a :b :c]}
                      :healer {:abilities [:d :e :f]}
                      :caster {:abilities [:g :h :i]}})

(def behaviors {:hunter identity
                :gatherer identity})

(def models {:rock :tree})


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
   :entity {}})
