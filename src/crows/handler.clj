(ns crows.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :refer [site api]]
            [compojure.route :refer [resources not-found]]
            [org.httpkit.server :refer :all]
            [crows.websocket :refer [wamp-handler]]))

(defn handler [request]
  (with-channel request channel
    (on-close channel (fn [status] (println "channel closed: " status)))
    (on-receive channel (fn [data] ;; echo it back
                          (send! channel data)))))

(defroutes app-routes
  (GET "/" [] "<p>Hello World</p>")
  (GET "/ws" [:as req] (handler req))
  (resources "/")
  (not-found "Not Found"))

(def app
  (site app-routes))
