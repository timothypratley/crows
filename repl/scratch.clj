(ns scratch
  (:require [crows.dev :refer [reset success failure build-cljs]]
            [clojure.tools.namespace.track]
            [clojure.tools.namespace.repl]))


(reset)

(build-cljs)

;TODO: look at the code for checkall
;TODO: use hooke  https://github.com/marick/Midje/issues/221
;TODO: cljs build creates out directory
