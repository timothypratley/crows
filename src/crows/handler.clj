(ns crows.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :refer [site api]]
            [compojure.route :refer [resources not-found]]
            [org.httpkit.server :refer :all]
            [crows.connection :refer [wamp-handler]]))



(def app-routes
  (site
   (routes
    (GET "/" req "<p>Hello World</p>")
    (GET "/wamp" req (wamp-handler req))
    (resources "/")
    (not-found "Not Found"))))
