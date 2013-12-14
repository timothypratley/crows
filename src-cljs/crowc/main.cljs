(ns crowc.main
  (:require [crowc.connection :as connection]
            [crowc.render :as render]))


(defn init
  []
  (connection/connect-wamp)
  (connection/connect-ws)
  (render/create)
  (render/add-mesh))

(set! (.-onload js/window) init)
