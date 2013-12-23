(ns crowc.render
  (:require [domina]
            [domina.events :refer [listen!]]
            [crowc.nav :as nav]
            [crowc.audio :as audio]))


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
      (domina/append! js/document.body (.getWebGLErrorMessage js/Detector))
      (let [renderer (js/THREE.WebGLRenderer. (clj->js {:canvas canvas}))
            resize (fn on-window-resize []
                (let [width canvas/offsetWidth
                      height canvas/offsetHeight]
                  (.setSize renderer width height)
                  (set! (.-aspect camera) (/ width height))
                  (set! (.-radius camera) (/ (+ width height) 4))
                  (.updateProjectionMatrix camera)))]
        (listen! js/window :resize resize)
        (resize)

        (set! (.-z (.-position camera))  10)
        (nav/attach canvas)
        (.focus canvas)

        ((fn render-callback []
           (let [t (min 1.0 (.getDelta clock))]
             (audio/update (nav/update canvas camera scene t zoom)))
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