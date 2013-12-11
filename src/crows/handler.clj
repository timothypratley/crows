(ns crows.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :refer [site api]]
            [compojure.route :refer [resources not-found]]))


(defroutes app-routes
  (GET "/" [] "<p>Hello World</p>")
  (resources "/")
  (not-found "Not Found"))

(def app
  (site app-routes))
