(ns crows.dev
  (:require [crows.main]
            [cljs.closure :as cljsc]
            [clojure.tools.namespace.track]
            [clojure.tools.namespace.repl]
            [gntp :refer [make-growler]]
            [clojure.java.io :refer [as-url resource input-stream]]))


(defonce system nil)

(defn start-new []
  {:pre [(nil? system)]}
  (alter-var-root #'system (fn startit [s]
                             (when s (crows.main/stop s))
                             (crows.main/start (crows.main/new-system)))))

(defn stop []
  (alter-var-root #'system (fn stopit [s]
                             (when s (crows.main/stop s))
                             nil)))
(defn reset []
  (stop)
  (clojure.tools.namespace.repl/refresh :after 'crows.dev/start-new)
  :ok)


(def growler (make-growler "Crows"
                           ;http://clojure.org/space/showimage/clojure-icon.gif
                           :icon (input-stream (resource "public/img/favicon.ico"))))

(def notifiers
  (growler :info {:icon (as-url "http://icons.iconarchive.com/icons/gakuseisean/ivista-2/128/Alarm-Tick-icon.png")}
           :warn {:icon (as-url "http://icons.iconarchive.com/icons/3dlb/3d-vol2/128/warning-icon.png")}
           :error {:icon (as-url "http://icons.iconarchive.com/icons/oxygen-icons.org/oxygen/128/Actions-window-close-icon.png")}))

(defn info [text]
  ((:info notifiers) "Information" :text text))

(defn warn [text]
  ((:warn notifiers) "Warning" :text text :priority 1))

(defn error [text]
  ((:error notifiers)"Error" :text text :priority 2))


(defn build-cljs []
  (try
    (let [result (cljsc/build "src-cljs" {:optimizations :advanced
                                          :output-to "resources/public/js/main.js"
                                          :cljs-source-map "resources/public/js/main.js.map"})]
      (if (.success result)
        (info "cljs build completed")
        (error "cljs build failed")))
    (catch Exception e
      (error (str e)))))
