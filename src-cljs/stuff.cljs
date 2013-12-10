(ns crows.stuff)

(defn init []
  (.log js/console "hello tim")
  (let [scene (js/THREE.Scene.)
        width (.-innerWidth js/window)
        height (.-innerHeight js/window)
        camera (js/THREE.PerspectiveCamera. 75 (/ width height) 0.1 1000 )
        renderer (js/THREE.CanvasRenderer.)
        geometry (js/THREE.CubeGeometry. 1 1 1)
        material (js/THREE.MeshBasicMaterial. (clj->js {:color 0x00ff00}))
        cube (js/THREE.Mesh. geometry material)
        render (fn cb []
                 (js/requestAnimationFrame cb)
                 (set! (.-x (.-rotation cube))  (+ 0.1 (.-x (.-rotation cube))) )
                 (set! (.-y (.-rotation cube))  (+ 0.1 (.-y (.-rotation cube))) )
                 (.render renderer scene camera)
                 )
        ]
    (.setSize renderer width height)
    (.appendChild js/document.body (.-domElement renderer) )
    (.add scene cube)
    (set! (.-z (.-position camera))  5)
    (render)))

(set! (.-onload js/window) init)
