(ns crowc.main
  (:require [crowc.connection :as connection]
            [crowc.render :as render]
            [crowc.world :as world]
            [crowc.picking :as picking]
            [domina :refer [by-id]]
            [domina.events :refer [listen]]))


(defn init []
  ;(connection/connect-wamp)
  ;(connection/connect-ws)
  (let [canvas (by-id "render-canvas")]
    (when (render/create canvas)
      (nav/attach canvas)
      (render/add-mesh)
      (let [w (world/create)]
        (.add (render/get-scene) w)
        (world/createChild w "foo" 44)
        (world/createChild w "foo" 77)))))

(set! (.-onload js/window) init)
