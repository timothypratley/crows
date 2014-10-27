(ns crows.main
  (:require [crows.handler :refer [handler]]
            [crows.connection :as connection]
            [crows.world :as world]
            [crows.storage :refer [store]]
            [crows.domain]
            [crows.actions]
            [crows.ticker :as ticker]
            [org.httpkit.server :refer [run-server]]
            [taoensso.timbre :refer [info]]))

(defn new-system
  "Returns a new instance of the whole application"
  []
  ;TODO: other storage related things to reset like seq
  (reset! crows.domain/domain (world/new-world))
  (reset! crows.domain/publish #'connection/publish)
  (reset! crows.domain/store #'store)
  (crows.domain/attach-actions 'crows.actions)
  {:httpkit-config {:port 8080}
   :world #'crows.domain/domain})

(defn start
  [system]
  {:pre [(not (contains? system :stop))]}
  (info "Server starting")
  (ticker/start)
  (assoc system :stop
    (run-server handler (system :httpkit-config))))

(defn stop
  [system]
  {:pre [(contains? system :stop)]}
  (info "Server stopping")
  ((system :stop) :timeout 500)
  (ticker/stop)
  (dissoc system :stop))

(defn -main
  [& args]
  (start (new-system)))
