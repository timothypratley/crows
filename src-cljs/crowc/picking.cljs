(ns crowc.picking)

(let [over-material (js/THREE.MeshLambertMaterial. (js-obj "color" 0xFF0000))]
  (defn emphasize [obj]
    (aset obj "materialBackup" (aget obj "material"))
    (aset obj "material" over-material)
    (aset obj "scaleBackup" (aget obj "scale"))
    (aset obj "scale" (.multiplyScalar (.clone (aget obj "scale")) 1.1)))
  (defn restore [obj]
    (aset obj "material" (aget obj "materialBackup"))
    (aset obj "scale" (aget obj "scaleBackup"))))

(defn pick
  [camera scene x y intersected]
  (let [projector (js/THREE.Projector.)
        v (js/THREE.Vector3. x y 1)
        v (.unprojectVector projector v camera)
        from (aget camera "position")
        direction (.normalize (.sub v from))
        ray (js/THREE.Raycaster. from direction)
        intersects (when scene
                     (.intersectObject ray scene true))]
    (if (seq intersects)
      (let [new-intersect (aget (first intersects) "object")]
        (when (not= @intersected new-intersect)
          (when @intersected
            (restore @intersected))
          (reset! intersected new-intersect)
          (emphasize @intersected)
          [:picked @intersected]))
      (when @intersected
        (restore @intersected)
        (reset! intersected nil)
        [:unpicked]))))

(defn select
  [intersected selected]
  (if @intersected
    (do
      (if (= @intersected @selected)
        [:reselected @intersected]
        (do
          (reset! selected @intersected)
          [:selected @intersected])))
    (when @selected
      (reset! selected nil)
      [:unselecteded])))
