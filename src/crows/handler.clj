(ns crows.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :refer [site api]]
            [compojure.route :refer [resources not-found]]
            [crows.websocket :refer [wamp-handler]]))


(defroutes app-routes
  (GET "/" [] "<p>Hello World</p>")
  (GET "/ws" req (wamp-handler req))
  (resources "/")
  (not-found "Not Found"))

(def app
  (site app-routes))
