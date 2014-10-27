(defproject crows "0.1.0-SNAPSHOT"
  :description "Crows vs Ravens"
  :url "http://github.com/timothypratley/crows"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main crows.main
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring "1.3.1"]
                 [compojure "1.2.1"]
                 [http-kit "2.1.19"]
                 #_[com.taoensso/timbre "3.3.1"]
                 #_[com.taoensso/sente "1.2.0"]
                 #_[com.taoensso/encore "1.14.0"]
                 #_[com.facebook/react "0.11.2"]
                 #_[om "0.7.3"]
                 #_[sablono "0.2.22"]]
  :profiles {:dev {
                   ;:source-paths ["dev"]
                   ;:hooks [leiningen.cljsbuild]
                   :plugins [[lein-cljsbuild "1.0.3"]]
                   :dependencies [#_[midje "1.6.3"]
                                  [org.clojure/clojurescript "0.0-2371"]
                                  #_[org.clojure/core.async "0.1.303.0-886421-alpha"]
                                  #_[domina "1.0.2"]]}}
  :cljsbuild {:builds
              {:dev
               {:compiler { :output-to "resources/public/js/main_dev.js"
                           :output-dir "resources/public/js/out"
                           :optimizations :none
                           ;;TODO: this line blows up badly
                           ;;:cljs-source-map "resources/public/js/main.js.map"
                           :warnings true
                           :source-map true }}
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
