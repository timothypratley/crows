(defproject crows "0.1.0-SNAPSHOT"
  :description "Crows vs Ravens"
  :url "http://github.com/timothypratley/crows"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main crows.main
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [org.clojure/core.match "0.2.2"]
                 [compojure "1.2.1"]
                 [com.taoensso/timbre "3.3.1"]
                 [com.taoensso/sente "1.2.0"]
                 [com.taoensso/encore "1.14.0"]
                 [com.facebook/react "0.11.2"]
                 [http-kit "2.1.19"]
                 [om "0.7.3"]
                 [ring "1.3.1"]
                 [sablono "0.2.22"]]
  :profiles {:dev {:source-paths ["dev"]
                   ;; for heroku
                   ;; :hooks [leiningen.cljsbuild]
                   :plugins [[lein-cljsbuild "1.0.3"]]
                   :dependencies [[org.clojure/clojurescript "0.0-2371"]
                                  [org.clojure/core.async "0.1.303.0-886421-alpha"]
                                  [domina "1.0.2"]]}}
  :cljsbuild {:builds
              {:dev
               {:compiler {:output-to "resources/public/js/main_dev.js"
                           :output-dir "resources/public/js/out"
                           :optimizations :none
                           :cljs-source-map "resources/public/js/main.js.map"
                           :warnings true
                           :source-map true}}
               :release
               {:compiler {:output-to "resources/public/js/main.js"
                           :optimizations :advanced
                           :externs ["public/js/three.js"
                                     "public/js/detector.js"]
                           :preamble ["public/js/three.js"
                                      "public/js/detector.js"]
                           :warnings true
                           :closure-warnings {:externs-validation :off
                                              :non-standard-jsdoc :off}}}}})
