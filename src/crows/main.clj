(ns crows.main
  (:require [crows.handler :refer [app]]
            [org.httpkit.server :refer [run-server]]))


(defn -main
  [& args]
  (println "Server started")
  (run-server app {:port 8080}))
