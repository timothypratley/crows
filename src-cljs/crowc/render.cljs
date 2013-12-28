(ns crowc.render
  (:require [domina]
            [domina.events :refer [listen!]]
            [crowc.nav :as nav]
            [crowc.audio :as audio]
            [crowc.connection :as connection]))


(defn create [canvas scene]
  (if (not js/Detector.webgl)
    (domina/append! js/document.body (.getWebGLErrorMessage js/Detector))
    (let [renderer (js/THREE.WebGLRenderer. (clj->js {:canvas canvas}))
          clock (js/THREE.Clock.)
          fov 35
          near 0.1
          far 10000
          camera (js/THREE.PerspectiveCamera. fov (/ canvas/offsetWidth canvas/offsetHeight) near far)
          intersected (atom nil)
          selected (atom nil)
          resize (fn on-window-resize []
                   (let [width canvas/offsetWidth
                         height canvas/offsetHeight]
                     (.setSize renderer width height)
                     (set! (.-aspect camera) (/ width height))
                     (.updateProjectionMatrix camera)))]
      (listen! js/window :resize resize)
      (resize) ; set to the current canvas size immeadiately, as initial values

      (set! (.-z (.-position camera))  10)
      (nav/attach canvas intersected selected)
      (.focus canvas)

      ((fn render-callback []
         (let [t (min 1.0 (.getDelta clock))
               [[position heading :as movement]
                pick-event] (nav/update canvas camera camera scene t intersected)]
           (when movement
             ;; TODO: there should only be one connection, pass it in
             (connection/update position heading))
           (when pick-event
             (audio/update pick-event)))
         (.render renderer scene camera)
         (js/requestAnimationFrame render-callback)))
      renderer)))
