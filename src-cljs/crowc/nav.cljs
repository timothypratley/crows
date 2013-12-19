(ns crowc.nav
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [>! <! chan put! close! timeout]]
            [crowc.three :refer [Vector3 Projector]]
            [domina.events :refer [listen!]]))

(def [pressed (atom #{})
      key-codes {:shift 16
                 :ctrl 17
                 :up 38
                 :down 40
                 :left 37
                 :right 39}
      mult-action {:shift [:speed 0.1]
                   :ctrl [:speed 10]}
      add-action {\w [:forward 1]
                  \s [:forward -1]
                  \a [:left 1]
                  \d [:left -1]
                  \r [:pitch 1]
                  \f [:pitch -1]
                  :up [:pitch 1]
                  :down [:pitch -1]
                  :left [:yaw 1]
                  :right [:yaw -1]
                  \q [:yaw 1]
                  \e [:yaw -1]}])

(defn attach [canvas]
  (listen! canvas :mousedown
           (fn on-mouse-down [e]
             (let [be (.getBrowserEvent (domina.events/raw-event e))]
               (pick 0.5 0.5)))))

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
