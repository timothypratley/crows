(ns crowc.main
  (:require [crowc.connection :as connection]
            [crowc.render :as render]
            [crowc.world :as world]
            [crowc.nav :as nav]
            [crowc.picking :as picking]
            [domina]))


(defn init []
  ;(connection/connect-wamp)
  ;(connection/connect-ws)
  (let [canvas (domina/by-id "render-canvas")]
    (when (render/create canvas)
      (render/add-mesh)
      (let [w (world/create)]
        (.add (render/get-scene) w)
        (world/createChild w "foo" 44)
        (world/createChild w "foo" 77)))))

(set! (.-onload js/window) init)
