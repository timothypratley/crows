(ns crowc.nav
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [>! <! chan put! close! timeout]]
            [crowc.three :refer [Vector3 Projector]]
            [domina.events :refer [listen!]]))


(defn pick [x y]
  (let [projector (Projector.)
        v (Vector3. x y 1)
        _ (.unprojectVector projector v camera)
        from (.-position camera)
        direction (.normalize (.subSelf v from))
        ray (Ray. from direction)
        intersects (.intersectScene ray scene)]
    (if (seq intersects)
      (let [new-intersect (.object (first intersects))]
        (when (not= intersected new-intersect)
          ;(when intersected
;            (restore intersected))
          (set! intersected new-intersect)
          ;(store intersected)
          ;(emphasize intersected)
;          (picked intersected)
          ))
      (when intersected
;        (restore intersected)
;        (unpicked)
        (set! intersected nil)))))


(defn attach [canvas]
  (listen! canvas :mousedown
           (fn on-mouse-down [e]
             (let [be (.getBrowserEvent (domina.events/raw-event e))]
               (pick 0.5 0.5)
             ))))

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
                )))))

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
                                          false)))
