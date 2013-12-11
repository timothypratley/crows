(ns crows.stuff)

(defn init []
  (let [scene (js/THREE.Scene.)
        width (.-innerWidth js/window)
        height (.-innerHeight js/window)
        camera (js/THREE.PerspectiveCamera. 75 (/ width height) 0.1 1000 )
        renderer (js/THREE.CanvasRenderer.)
        geometry (js/THREE.PlaneGeometry. 8 8 8 8)
        material (js/THREE.MeshPhongMaterial. (clj->js {:color 0x00ff00
                                                        :wireframe true}))
        cube (js/THREE.Mesh. geometry material)
        render (fn cb []
                 (js/requestAnimationFrame cb)
                 (.render renderer scene camera))]
    (dotimes [i (.-length (.-vertices geometry))]
      (set! (.-z (aget (.-vertices geometry) i)) (rand)))
    (.setSize renderer width height)
    (.appendChild js/document.body (.-domElement renderer) )
    (.add scene cube)
    (set! (.-z (.-position camera))  5)
    (render)))

(set! (.-onload js/window) init)
