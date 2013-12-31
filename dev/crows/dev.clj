(ns crows.dev
  (:require [crows.main :refer [new-system start stop]]
            [cljs.closure :as cljsc]
            [clojure.tools.namespace.track]
            [clojure.tools.namespace.repl]
            [gntp :refer [make-growler]]
            [clojure.java.io :refer [as-url input-stream]]))


(defonce system nil)

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


(def growler (make-growler "Crows"
                           ;http://clojure.org/space/showimage/clojure-icon.gif
                           :icon (input-stream "resources/public/img/favicon.ico")))

(def notifiers
  (growler :success {:icon (as-url "http://icons.iconarchive.com/icons/gakuseisean/ivista-2/128/Alarm-Tick-icon.png")}
           :failure {:icon (as-url "http://icons.iconarchive.com/icons/oxygen-icons.org/oxygen/128/Actions-window-close-icon.png")}))

(def failure (:failure notifiers))
(def success (:success notifiers))

(defn build-cljs []
  (try
    (let [result (cljsc/build "src-cljs/crowc/main.cljs" {:output-to "resources/public/js/main.js"
                                                          :cljs-source-map "resources/public/js/main.js.map"})]
      (success "Success" :text "cljs build completed"))
    (catch Exception e
      (failure "Failed" :text (.getMessage e)))))
