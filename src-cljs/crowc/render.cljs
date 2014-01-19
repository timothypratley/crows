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
                     (.render renderer scene camera)))
          last-pose-at (atom (.getTime (js/Date.)))
          last-pose (atom nil)
          send-pose (fn send-pose [[position heading :as pose]]
                      (when (not= pose @last-pose)
                        (let [now (.getTime (js/Date.))]
                          (when (> (- now @last-pose-at) 300)
                            (connection/pose conn position heading)
                            (reset! last-pose-at now)
                            (reset! last-pose pose)
                            (js/setTimeout send-pose 300)))))]
      (listen! js/window :resize resize)
      (resize)
      (set! (.-z (.-position camera))  10)
      (nav/attach canvas intersected selected)
      (.focus canvas)

      (listen! canvas :click (fn on-click [evt]
                               (.log js/console "clicked")
                               (audio/update
                                (picking/select intersected selected))))

      ((fn render-callback []
         (let [t (min 1.0 (.getDelta clock))
               [pose pick-event] (nav/update canvas camera camera scene t intersected)]
           (when pose
             (send-pose pose))
           (when pick-event
             (audio/update pick-event)))
         (.render renderer scene camera)
         (js/requestAnimationFrame render-callback)))
      renderer)))
