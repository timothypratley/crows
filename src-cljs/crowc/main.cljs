(ns crowc.main
  (:require
   [crowc.connection :as connection]
   [crowc.render :as render]
   [crowc.world :as world]
   [crowc.nav :as nav]
   [crowc.picking :as picking]
   [domina]))

(defn init []
  (let [canvas (domina/by-id "render-canvas")
        conn (connection/connect)]
    (when (render/create canvas world/scene conn)
      (js/setTimeout (fn [] (world/create-player 1)) 200)
      (world/create-child world/scene 51 1 :terrain)
      (world/create-child world/scene 44 1 :portal)
      (world/create-child world/scene 77 2 :landmark))))

(set! (.-onload js/window) init)
