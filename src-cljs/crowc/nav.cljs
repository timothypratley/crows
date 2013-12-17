(ns crowc.nav
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [>! <! chan put! close! timeout]]
            [crowc.three :refer [Vector3 Projector]]
            [domina.events :refer [listen!]]))


(let [over-material (MeshLambertMaterial. (clj->js {:color 0xFF0000}))]
  (defn emphasize [obj]
    (aset obj "materialBackup" (aget obj "material"))
    (aset obj "material" over-material)
    (aset obj "scaleBackup" (aget obj "scale"))
    (aset obj "scale" (.multiplyScalar (.clone scale) 1.1)))
  (defn restore [object]
    (aset obj "material" (aget obj "materialBackup"))
    (aset obj "scale" (aget obj "scaleBackup"))))

(let [intersected (atom nil)]
  (defn pick [x y]
    (let [projector (Projector.)
          v (Vector3. x y 1)
          v (.unprojectVector projector v camera)
          from (.-position camera)
          direction (.normalize (.subSelf v from))
          ray (Ray. from direction)
          intersects (.intersectScene ray scene)]
      (if (seq intersects)
        (let [new-intersect (aget (first intersects) "object")]
          (when (not= @intersected new-intersect)
            (when @intersected
              (restore intersected))
            (reset! intersected new-intersect)
            (emphasize @intersected)
            (picked @intersected)))
        (when @intersected
          (restore intersected)
          (unpicked @inersected)
          (reset! intersected nil))))))


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
