(ns crows.dev
  (:require [crows.main :refer [new-system start stop]]
            [clojure.tools.namespace.track]
            [clojure.tools.namespace.repl]
            [gntp :refer [make-growler]]
            [clojure.java.io :refer [input-stream]]
            [org.httpkit.server :refer [run-server]]))


(def system nil)

(defn start-new []
  {:pre [(nil? system)]}
  (alter-var-root #'system (fn startit [s]
                             (when s (stop s))
                             (start (new-system)))))

(defn reset []
  (alter-var-root #'system (fn stopit [s]
                             (when s (stop s))
                             nil))
  (clojure.tools.namespace.repl/refresh :after 'crows.dev/start-new)
  :ok)

;(reset)

(def growler (make-growler "Clojure"
                           ;http://clojure.org/space/showimage/clojure-icon.gif
                           :icon (input-stream "resources/public/img/favicon.ico")))
(def notifiers
  (growler :success {:icon (input-stream "http://icons.iconarchive.com/icons/gakuseisean/ivista-2/128/Alarm-Tick-icon.png")}
           :failure {:icon (input-stream "http://icons.iconarchive.com/icons/oxygen-icons.org/oxygen/128/Actions-window-close-icon.png")}))

;((:failure notifiers) "Failed")
;((:success notifiers) "Success!" :text "The thing completed successfully")

;TODO: look at the code for checkall
;TODO: use hooke  https://github.com/marick/Midje/issues/221
;TODO: I don't actually want to execute interactive code except in lighttable, bring back my repl directory (so it won't be loaded)??