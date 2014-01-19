(ns crows.ticker
  (:require [crows.domain :refer [domain command]]
            [crows.actions :refer :all]))


(defn walk-forward [{:keys [location heading] :as mobile}]
  [pose (update-in location [0] inc) heading])

(defn act [mobile]
  (walk-forward mobile))

(defn tick
  "Given the current state of the world, produces all the commands that should be executed by the server."
  [world]
  (for [m (world :mobiles)]
    (act m)))


(let [running (atom false)
      thread (Thread.
              (fn run []
                (println "Ticker running")
                (while @running
                  (doseq [[f & args] (tick @domain)]
                    ; TODO: name is not what I want here
                    (apply command f args))
                  (Thread/sleep 300))
                (println "Ticker finished"))
              "ticker")]

  (defn start []
    (when-not @running
      (reset! running true)
      (.start thread)))

  (defn stop[]
    (when @running
      (reset! running false))))
