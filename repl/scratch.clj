(ns crows.scratch
  (:require [crows.handler :refer [app]]
            [org.httpkit.server :refer [run-server]]))


(run-server app {:port 8080})