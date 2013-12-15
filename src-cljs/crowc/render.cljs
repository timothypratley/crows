(ns crowc.render)


(let [scene (js/THREE.Scene.)
      width (.-innerWidth js/window)
      height (.-innerHeight js/window)
      fov 75
      near 0.1
      far 10000
      camera (js/THREE.PerspectiveCamera. fov (/ width height) near far)
      controls (js/THREE.FlyControls. camera)
      clock (js/THREE.Clock.)
      container document.body]

  (defn create [canvas]
    (if (.-webgl js/Detector)
      (.append dom-element (.getWebGLErrorMessage js/Detector))
      (let [renderer (js/THREE.WebGLRenderer. (clj->js {:canvas canvas}))
            resize (fn on-window-resize []
                (let [width canvas.offsetWidth
                      height canvas.offsetHeight]
                  (.setSize renderer width height)
                  (set! (.-aspect camera) (/ width height))
                  (set! (.-radius camera) (/ (+ width height) 4))
                  (.updateProjectionMatrix camera)))]
        (.addEventListener window "resize" resize false)
        (resize)
        ((fn render-callback []
           (let [delta (.getDelta clock)]
             (.update controls delta))
           (.render renderer scene camera)
           (js/requestAnimationFrame render-callback)))
        renderer)))

  (defn controls []
    (doto controls
      (aset "domElement" renderer)
      (aset "movementSpeed" 0.1)
      (aset "rollSpeed" 0.1)
      (aset "autoForward" true)
      (aset "dragToLook" true))
    ; TODO: better syntax for nested properties
    (set! (.-z (.-position camera))  10))

  (defn add-mesh []
    (let [geometry (js/THREE.PlaneGeometry. 8 8 8 8)
          material (js/THREE.MeshPhongMaterial. (clj->js {:color 0x00ff00
                                                          :wireframe true}))
          mesh (js/THREE.Mesh. geometry material)]
      (dotimes [i (.-length (.-vertices geometry))]
        (set! (.-z (aget (.-vertices geometry) i)) (rand)))
      (.add scene mesh)))

  (defn set-camera
    [z]
    (set! (.-z (.-position camera)) z)))

;;;(set-camera 20)