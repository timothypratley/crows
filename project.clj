(defproject crows "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main crows.main
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2030"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [domina "1.0.2"]
                 [ring "1.2.1"]
                 [compojure "1.1.6"]
                 [http-kit "2.1.13"]
                 [clj-wamp "1.0.0"]
                 [com.taoensso/timbre "3.0.0-RC2"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.4"]
                                  [gntp "0.6.0"]
                                  [lein-kibit "0.0.8"]
                                  [jonase/eastwood "0.0.2"]
                                  [lein-bikeshed "0.1.3"]]}}
  :source-paths ["src"]
  :cljsbuild {:builds [{:source-paths ["src-cljs"]
                        :compiler {:output-to "resources/public/js/main.js"
                                   :cljs-source-map "resources/public/js/main.js.map"}}]}
  :hooks [leiningen.cljsbuild]
  :plugins [[lein-ring "0.8.2"]
            [lein-cljsbuild "0.3.0"]
            [lein-ancient "0.5.4"]
            [jonase/eastwood "0.0.2"]
            [lein-kibit "0.0.8"]])
