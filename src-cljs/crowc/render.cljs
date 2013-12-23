(ns crowc.render
  (:require [domina]
            [domina.events :refer [listen!]]
            [crowc.nav :as nav]
            [crowc.audio :as audio]))


(defn create [canvas scene]
  (if (not js/Detector.webgl)
    (domina/append! js/document.body (.getWebGLErrorMessage js/Detector))
    (let [renderer (js/THREE.WebGLRenderer. (clj->js {:canvas canvas}))
          clock (js/THREE.Clock.)
          fov 75
          near 0.1
          far 10000
          camera (js/THREE.PerspectiveCamera. fov (/ (.-innerWidth js/window) (.-innerHeight js/window)) near far)
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
