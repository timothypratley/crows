(ns crowc.nav
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [>! <! chan put! close! timeout]]
            [crowc.three :refer [v3]]
            [domina.events :refer [listen!]]))


(let [x (atom 0)
      y 0
      left 0
      top 0]

  #_(defn pick []
    (v3. x y 1)

    (.unprojectVector projector v camera)
    (ray. from, v.subSelf(from).normalize())
    (.intersectScene r scene)
    (if (seq intersects)
      (when (not= intersected intersects[0].object)
        (when intersected
              (restore intersected))
        (set! intersected intersects[0].object)
        (store intersected)
        (emphasize intersected)
        (picked intersected))
      (when intersected
        (restore intersected)
        (unpicked)
        (set! intersected nil))))


  (defn attach [canvas]
    #_(let [c (chan)]
      (doseq [et [:mousemove :mousedown :mouseout]]
        (listen! canvas et
                 (fn on-event [e]
                     (put! c e))))
      (go (while true
            (let [e (<! c)]
              (do
                (reset! x (.-pageX (.getBrowserEvent (domina.events/raw-event e))))
                (.log js/console @x)
))))))

#_(doto dom-element
      (.addEventListener "mouseout"
                         (fn on-mouse-out []
                           (set! (.-over mouse) false))
                         false)
      (.addEventListener "mousemove"
                         (fn on-mouse-move [event]
                           (.preventDefault event)
                           (set! mouseleft (- (.-pageX event) (.-offsetLeft dom-element)))
                           (set! mousetop (- (.-pageY event) (.-offsetTop dom-element)))
                           (set! mousex (dec (* 2.0 (/ mouseleft width))))
                           (set! mousey (- (dec (* 2.0 (/ mousetop height)))))
                         false)
      (.addEventListener "mousedown"
                         (fn on-mouse-down [event]
                           (.preventDefault event)
                           (pick)
                           (if intersected
                             (if (= intersected target)
                               (on-reselect target)
                               (do (set! target intersected)
                                 (on-select target)))
                             (if target
                               (free))))
                         false))))
