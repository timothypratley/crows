(ns crowc.render
  (:require [domina]
            [domina.events :refer [listen!]]
            [crowc.nav :as nav]
            [crowc.audio :as audio]
            [crowc.connection :as connection]
            [crowc.picking :as picking]))


(defn create
  [canvas scene conn]
  (if (not js/Detector.webgl)
    (domina/append! js/document.body (.getWebGLErrorMessage js/Detector))
    (let [renderer (js/THREE.WebGLRenderer. (clj->js {:canvas canvas}))
          clock (js/THREE.Clock.)
          fov 35
          near 0.1
          far 10000
          camera (js/THREE.PerspectiveCamera. fov (/ (.-offsetWidth canvas) (.-offsetHeight canvas)) near far)
          intersected (atom nil)
          selected (atom nil)
          resize (fn on-window-resize []
                   (let [width (.-clientWidth canvas)
                         height (.-clientHeight canvas)]
                     (set! (.-aspect camera) (/ width height))
                     (.updateProjectionMatrix camera)
                     (.setSize renderer width height)
                     (.render render scene camera)))]
      (listen! js/window :resize resize)
      (resize) ; set to the current canvas size immeadiately, as initial values

      (set! (.-z (.-position camera))  10)
      (nav/attach canvas intersected selected)
      (.focus canvas)

      (listen! canvas :click (fn on-click [evt]
                               (.log js/console "clicked")
                               (audio/update
                                (picking/select intersected selected))))

      ((fn render-callback []
         (let [t (min 1.0 (.getDelta clock))
               [[position heading :as movement]
                pick-event] (nav/update canvas camera camera scene t intersected)]
           (when movement
             (.log js/console "MOVEMENT" movement)
             (connection/pose conn position heading))
           (when pick-event
             (audio/update pick-event)))
         (.render renderer scene camera)
         (js/requestAnimationFrame render-callback)))
      renderer)))
