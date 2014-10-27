(ns crows.handler
  (:require [compojure.core :refer [GET POST]]
            [compojure.handler :refer [site]]
            [compojure.route :refer [resources not-found]]
            [ring.util.response :refer [redirect]]
            [ring.middleware.reload :refer [wrap-reload]]
            [crows.connection]))

(defroutes app-routes
  (GET "/" req (redirect "index.html"))
  (GET  "/chsk" req (connection/ajax-get-or-ws-handshake req))
  (POST "/chsk" req (connection/ajax-post req))
  (POST "/login" req (connection/login! req))
  (resources "/")
  (not-found "Not Found"))

(def handler
  (if :dev
    (wrap-reload (site #'app-routes))
    (site app-routes)))
