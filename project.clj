(defproject crows "0.1.0-SNAPSHOT"
  :description "Crows vs Ravens"
  :url "http://github.com/timothypratley/crows"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main crows.main
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [ring "1.2.2"]
                 [compojure "1.1.6"]
                 [http-kit "2.1.18"]
                 [clj-wamp "1.0.2"]
                 [com.taoensso/timbre "3.1.6"]]
  :profiles {:dev {:source-paths ["dev"]
                   :hooks [leiningen.cljsbuild]
                   :plugins [[lein-cljsbuild "0.3.0"]]
                   :dependencies [[midje "1.6.3"]
                                  [org.clojure/clojurescript "0.0-2202"]
                                  [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                                  [domina "1.0.2"]]}}
  :cljsbuild {:builds [{:compiler {:output-to "resources/public/js/main.js"
                                   :optimizations :simple
                                   :warnings true
                                   :cljs-source-map "resources/public/js/main.js.map"}}]})
