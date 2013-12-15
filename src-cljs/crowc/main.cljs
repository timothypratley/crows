(ns crowc.main
  (:require [crowc.connection :as connection]
            [crowc.render :as render]
            [crowc.world :as world]))


(defn init []
  (connection/connect-wamp)
  (connection/connect-ws)
  (when (render/create (.getElementById document "render-canvas"))
    (render/add-mesh)
    (.add render/scene world/world)
    (world/createChild world/world "foo" 44)
    (world/createChild world/world "foo" 77)))

(set! (.-onload js/window) init)
