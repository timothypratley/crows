(ns crowc.nav
  (:require [crowc.three :refer :all]))


(let [mousex 0
      mousey 0
      mouseleft 0
      mousetop 0]

  (defn pick []
    (v3. mousex mousey 1)

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


  (defn attach [dom-element]
    (doto dom-element
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
                         false)))))
