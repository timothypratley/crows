(defproject crows "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2080"]
                 [compojure "1.1.6"]
                 [enfocus "2.0.2"]]
  :source-paths ["src"]
  :cljsbuild {:builds [{:source-paths ["src-cljs"]
                        :compiler {:output-to "resources/public/js/main.js"}}]}
  :hooks [leiningen.cljsbuild]
  :ring {:handler crows.handler/app}
  :plugins [[lein-ring "0.8.2"]
            [lein-cljsbuild "0.3.0"]
            [lein-ancient "0.5.4"]])
