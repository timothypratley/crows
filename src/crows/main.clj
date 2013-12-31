(ns crows.main
  (:require [crows.handler :refer [app]]
            [org.httpkit.server :refer [run-server]]))


(defn new-system
  "Returns a new instance of the whole application"
  []
  {:config {:port 8080}})

(defn start
  [system]
  {:pre [(not (contains? system :stop))]}
  (assoc system :stop (run-server app (system :config))))

(defn stop
  [system]
  {:pre [(contains? system :stop)]}
  ((system :stop))
  (dissoc system :stop))

(defn -main
  [& args]
  (println "Server started")
  (start (new-system)))
