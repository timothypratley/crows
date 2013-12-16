(ns crowc.world)



(defn createChild
  [parent t index]
  (let [geometry (js/THREE.CubeGeometry. 3 3 3)
        material (js/THREE.MeshPhongMaterial. (clj->js {:color 0x00ff00}))
        e (THREE.Mesh. geometry)
        x (mod index 8)
        y (quot index 8)]
    (set! (.-name e) index)
    (set! (.-type e) t)
    (.add parent e)
    (set! (.-position e) (js/THREE.Vector3. x y 0))
    e))

#_(defn expand [object zoom ]
  (replace-closest object)
  (ensure-children object)
  (request object)
  (colapse-below zoom))

(defn create
  []
  (let [world (js/THREE.Object3D.)]
    world))
