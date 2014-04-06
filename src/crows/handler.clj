(ns crows.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :refer [site api]]
            [compojure.route :refer [resources not-found]]
            [ring.util.response :refer [redirect]]
            [ring.middleware.reload :refer [wrap-reload]]
            [org.httpkit.server :refer :all]
            [crows.connection :refer [wamp-handler]]))


(defroutes app-routes
  (GET "/" req (redirect "index.html"))
  (GET "/wamp" req (wamp-handler req))
  (resources "/")
  (not-found "Not Found"))))

(def handler
  (if :dev
    (wrap-reload (site #'app-routes))
    (site app-routes)))
