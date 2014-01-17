(ns crows.main
  (:require [crows.handler :refer [app-routes]]
            [crows.world :refer [new-world]]
            [crows.publisher :refer [publish]]
            [crows.storage :refer [store]]
            [crows.domain]
            [org.httpkit.server :refer [run-server]]
            [taoensso.timbre :refer [log  trace  debug  info  warn  error  fatal  report spy]]))


(defn new-system
  "Returns a new instance of the whole application"
  []
  ;TODO: other storage related things to reset like seq
  (reset! crows.domain/domain (new-world))
  (reset! crows.domain/publish publish)
  (reset! crows.domain/store store)
  {:httpkit-config {:port 8080}
   :world #'crows.domain/domain})

(defn start
  [system]
  {:pre [(not (contains? system :stop))]}
  (info "Server starting")
  (assoc system :stop
    (run-server app-routes (system :httpkit-config))))

(defn stop
  [system]
  {:pre [(contains? system :stop)]}
  (info "Server stopping")
  ((system :stop) :timeout 500)
  (dissoc system :stop))


(defn -main
  [& args]
  (start (new-system)))
