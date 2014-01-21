(ns scratch
  (:require [crows.dev :refer [reset stop start-new]]
            [crows.domain]
            [crows.actions]
            [crows.ticker]
            [clojure.tools.namespace.track]
            [clojure.tools.namespace.repl]))


(start-new)
(stop)
(crows.domain/command #'crows.actions/create-mobile "raven" [0 0 0] [1 0 0 0])
@crows.domain/actions
(crows.ticker/start)
(crows.ticker/stop)

(reset)

(use 'crows.handler)
(use 'ring.mock.request)
((crows.handler/app-routes nil) (request :get "/"))
(build-cljs)

;TODO: look at the code for checkall
;TODO: use hooke  https://github.com/marick/Midje/issues/221
;TODO: cljs build creates out directory
