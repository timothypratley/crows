(ns crows.stuff
  (:require [clj-wamp.client :as client]
            [clj-wamp.websocket :as websocket]))

(defn init []
  (let [on-event (fn [ws topic event] (.log js/console "EVENT " event))
        on-open (fn [ws sess-id]
                  (.log js/console "OPENED " sess-id)
                  (client/subscribe! ws "http://wamptutorial/event#chat")
                  (client/publish! ws "http://wamptutorial/event#chat" "Hi hi" "hi")
                  (client/rpc! ws "http://wamptutorial/rpc#echo"))
        on-close (fn [] (.log js/console "closed"))
        ws (client/wamp-handler "ws://localhost:8080/wamp"
                                {:websocket-client websocket/client
                                 :on-open  on-open
                                 :on-close on-close
                                 :on-event on-event})]
    )
  (let [conn (js/WebSocket. "ws://localhost:8080/ws")]
    (set! (.-onopen conn)
          (fn [e]
            (.log js/console "connected")
            (.send conn "hello")))

    (set! (.-onerror conn)
          (fn []
            (js/alert "error")
            (.log js/console js/arguments)))

    (set! (.-onmessage conn)
          (fn [e]
            (.log js/console e))))

  (let [scene (js/THREE.Scene.)
        width (.-innerWidth js/window)
        height (.-innerHeight js/window)
        camera (js/THREE.PerspectiveCamera. 75 (/ width height) 0.1 1000 )
        controls (js/THREE.FlyControls. camera)
        renderer (js/THREE.CanvasRenderer.)
        geometry (js/THREE.PlaneGeometry. 8 8 8 8)
        material (js/THREE.MeshPhongMaterial. (clj->js {:color 0x00ff00
                                                        :wireframe true}))
        cube (js/THREE.Mesh. geometry material)
        clock (js/THREE.Clock.)
        render (fn cb []
                 (let [delta (.getDelta clock)]
                   (.update controls delta))
                 (js/requestAnimationFrame cb)
                 (.render renderer scene camera))]
    (dotimes [i (.-length (.-vertices geometry))]
      (set! (.-z (aget (.-vertices geometry) i)) (rand)))
    (.setSize renderer width height)
    (.appendChild js/document.body (.-domElement renderer) )
    (.add scene cube)
    (set! (.-domElement controls) renderer)
    (set! (.-movementSpeed controls) 0.1)
		(set!	(.-domElement controls) container)
		(set!	(.-rollSpeed	controls) 0.1)
		(set!	(.-autoForward	controls) true)
		(set!	(.-dragToLook	controls) true)
    (set! (.-z (.-position camera))  5)
    (render)))

(set! (.-onload js/window) init)
