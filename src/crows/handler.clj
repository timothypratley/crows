(ns crows.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :refer [site api]]
            [compojure.route :refer [resources not-found]]
            [org.httpkit.server :refer :all]
            [crows.wamp :refer [wamp-handler]]))


(defn app-routes
  [system]
  (site
   (routes
    (GET "/" req "<p>Hello World</p>")
    (GET "/wamp" req (wamp-handler system req))
    (resources "/")
    (not-found "Not Found"))))
