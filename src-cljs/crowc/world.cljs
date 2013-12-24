(ns crowc.world)


(def scene (doto (js/THREE.Scene.)
             (.add (js/THREE.AmbientLight 0x888888))
             (.add (let [d (js/THREE.DirectionalLight. 0xffffff 0.5)]
                     (.set (.-position d) 500 -250 1000)
                     d))))

(def types {:terrain [#(js/THREE.PlaneGeometry. 8 8 8 8)
                      #(js/THREE.MeshLambertMaterial. (js-obj "color" 0x008800
                                                              "wireframe" true))]
            :portal [#(js/THREE.SphereGeometry. 3)
                     #(js/THREE.MeshLambertMaterial. (js-obj "color" 0x000088
                                                             "wireframe" true))]
            :landmark [#(js/THREE.CubeGeometry. 3 3 3)
                       #(js/THREE.MeshLambertMaterial. (js-obj "color" 0x880000))]})

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
