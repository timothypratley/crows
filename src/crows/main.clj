(ns crows.main
  (:require [crows.handler :refer [app]]
            [org.httpkit.server :refer [run-server]]
            [taoensso.timbre :refer [log  trace  debug  info  warn  error  fatal  report spy]]))


(defn new-system
  "Returns a new instance of the whole application"
  []
  {:config {:port 8080}})

(defn start
  [system]
  {:pre [(not (contains? system :stop))]}
  (info "Server starting")
  (assoc system :stop (run-server app (system :config))))

(defn stop
  [system]
  {:pre [(contains? system :stop)]}
  (info "Server stopping")
  ((system :stop))
  (dissoc system :stop))

(defn -main
  [& args]
  (start (new-system)))
