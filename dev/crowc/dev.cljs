(ns crowc.dev
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [domina :refer [by-id]]
            [domina.events :refer [listen!]]

            [cljs.core.async :refer [>! <! chan put! close! timeout]]))


(def running true)

(def c (chan))
(def elem (by-id "render-canvas"))

(doseq [et [:mousemove :mousedown :mouseout]]
  (listen! elem et (fn on-event [e]
                     (put! c e))))

(go (while running
      (let [e (<! c)]
        (.log js/console e))))
