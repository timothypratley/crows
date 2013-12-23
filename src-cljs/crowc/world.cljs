(ns crowc.world)


(def scene (js/THREE.Scene.))

(def types {:terrain [#(js/THREE.PlaneGeometry. 8 8 8 8)
                      #(js/THREE.MeshPhongMaterial. (clj->js {:color 0x00ff00
                                                              :wireframe true}))]
            :portal [#(js/THREE.SphereGeometry. 3)
                     #(js/THREE.MeshPhongMaterial. (clj->js {:color 0x004444
                                                             :wireframe true}))]
            :landmark [#(js/THREE.CubeGeometry. 3 3 3)
                       #(js/THREE.MeshPhongMaterial. (clj->js {:color 0x112233}))]})

(defn create
  [t]
  (let [[newgeo newmat] (types t)
        geometry (newgeo)
        material (newmat)
        mesh (js/THREE.Mesh. geometry material)]
    (when (= t :terrain)
      (dotimes [i (.-length (.-vertices geometry))]
        (set! (.-z (aget (.-vertices geometry) i)) (rand))))
    mesh))

(defn create-child
  [parent index height t]
  (let [e (create t)
        x (mod index 8)
        y (quot index 8)]
    (set! (.-name e) index)
    (set! (.-type e) t)
    (.add parent e)
    (set! (.-position e) (js/THREE.Vector3. x y 0))
    e))

(defn ensure-children
  [parent size]
  (let [children (.-children parent)]
    (when (empty? children)
      (dotimes [i size]
        (create-child parent i 0 :unknown)))))

#_(defn expand [object zoom ]
    (replace-closest object)
    (ensure-children object)
    (request object)
    (colapse-below zoom))
