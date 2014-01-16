(ns crows.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :refer [site api]]
            [compojure.route :refer [resources not-found]]
            [org.httpkit.server :refer :all]
            [crows.connection :refer [new-wamp-handler]]))


(defn handler [request]
  (with-channel request channel
    (on-close channel (fn [status] (println "channel closed: " status)))
    (on-receive channel (fn [data] ;; echo it back
                          (send! channel data)))))

(defn app-routes
  [system]
  (let [wamp-handler (new-wamp-handler system)]
    (site
     (routes
      (GET "/" req "<p>Hello World</p>")
      (GET "/ws" req (handler req))
      (GET "/wamp" req (wamp-handler req))
      (resources "/")
      (not-found "Not Found")))))
