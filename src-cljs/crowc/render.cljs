(ns crowc.render)


(def scene (js/THREE.Scene.))
(def width (.-innerWidth js/window))
(def height (.-innerHeight js/window))
(def camera (js/THREE.PerspectiveCamera. 75 (/ width height) 0.1 1000 ))
(def controls (js/THREE.FlyControls. camera))

(defn create
  []
  (let [renderer (js/THREE.CanvasRenderer.)
        clock (js/THREE.Clock.)
        render (fn cb []
                 (let [delta (.getDelta clock)]
                   (.update controls delta))
                 (js/requestAnimationFrame cb)
                 (.render renderer scene camera))]
    (.setSize renderer width height)
    (.appendChild js/document.body (.-domElement renderer) )
    (set! (.-domElement controls) renderer)
    (set! (.-movementSpeed controls) 0.1)
    (set!	(.-rollSpeed	controls) 0.1)
    (set!	(.-autoForward	controls) true)
    (set!	(.-dragToLook	controls) true)
    (set! (.-z (.-position camera))  10)
    (render)))

(defn add-mesh
  []
  (let [geometry (js/THREE.PlaneGeometry. 8 8 8 8)
        material (js/THREE.MeshPhongMaterial. (clj->js {:color 0x00ff00
                                                        :wireframe true}))
        mesh (js/THREE.Mesh. geometry material)]
    (dotimes [i (.-length (.-vertices geometry))]
      (set! (.-z (aget (.-vertices geometry) i)) (rand)))
    (.add scene mesh)))

(defn set-camera
  [z]
  (set! (.-z (.-position camera)) z))

;;;(set-camera 20)