(ns crowc.render
  (:require [domina :refer [append!]]))


(let [scene (js/THREE.Scene.)
      width (.-innerWidth js/window)
      height (.-innerHeight js/window)
      fov 75
      near 0.1
      far 10000
      camera (js/THREE.PerspectiveCamera. fov (/ width height) near far)
      clock (js/THREE.Clock.)
      container document.body]

  (defn create [canvas]
    (if (not js/Detector.webgl)
      (append! js/document.body (.getWebGLErrorMessage js/Detector))
      (let [renderer (js/THREE.WebGLRenderer. (clj->js {:canvas canvas}))
            controls (js/THREE.FlyControls. camera canvas)
            resize (fn on-window-resize []
                (let [width canvas/offsetWidth
                      height canvas/offsetHeight]
                  (.setSize renderer width height)
                  (set! (.-aspect camera) (/ width height))
                  (set! (.-radius camera) (/ (+ width height) 4))
                  (.updateProjectionMatrix camera)))]
        (.addEventListener js/window "resize" resize false)
        (resize)

        (doto controls
          (aset "movementSpeed" 0.1)
          (aset "rollSpeed" 0.1)
          (aset "autoForward" true)
          (aset "dragToLook" true))
        ; TODO: better syntax for nested properties
        (set! (.-z (.-position camera))  10)

        ((fn render-callback []
           (let [delta (.getDelta clock)]
             (.update controls delta))
           (.render renderer scene camera)
           (js/requestAnimationFrame render-callback)))
        renderer)))

  (defn get-scene []
    scene)

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