(ns crowc.models)


(def models (atom {}))

(let [loader (js/THREE.JSONLoader.)]
  (.load loader "/models/animals/raven.js"
         (fn [geometry materials]
           (.log js/console "loaded raven")
           (swap! models assoc :raven geometry))))
