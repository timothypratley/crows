(ns crows.ticker
  (:require [crows.domain :refer [domain command]]
            [crows.actions :refer :all]
            [taoensso.timbre :refer [info debug]]))


(defn walk-forward [id {:keys [location heading] :as mobile}]
  [#'pose id (update-in location [0] inc) heading])

(defn act [id mobile]
  (walk-forward id mobile))

(defn tick
  "Given the current state of the world, produces all the commands that should be executed by the server."
  [world]
  (for [[id mobile] (world :mobiles)]
    (act id mobile)))

(def running (atom false))

(let [o (Object.)]

  (defn start []
    (locking o
     (when-not @running
       (reset! running true)
       (.start (Thread.
                (fn run []
                  (info "Ticker running")
                  (while @running
                    (doseq [[f & args] (tick @domain)]
                      (debug f args)
                      (apply command f args))
                    (Thread/sleep 300))
                  (info "Ticker finished"))
                "ticker")))))

  (defn stop[]
    (locking o
      (when @running
        (reset! running false)))))
