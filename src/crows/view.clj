(ns crows.view)

(def user-views (ref {}))
(def user-states (ref {}))

(defn update [uid app-state]
  (dosync
   (alter user-states assoc uid app-state)))
