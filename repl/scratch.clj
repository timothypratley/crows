(ns scratch
  (:require [crows.dev :refer [reset stop]]
            [clojure.tools.namespace.track]
            [clojure.tools.namespace.repl]))


(reset)
(stop)
(use 'crows.handler)
(use 'ring.mock.request)
((crows.handler/app-routes nil) (request :get "/"))
(build-cljs)

;TODO: look at the code for checkall
;TODO: use hooke  https://github.com/marick/Midje/issues/221
;TODO: cljs build creates out directory
