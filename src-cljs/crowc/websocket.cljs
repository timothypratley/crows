(ns crowc.websocket
  (:require [goog.events :as events]
            [goog.net.WebSocket.EventType :as et]))


(defn connect!
  "Creates a websocket"
  [uri & [{:keys [protocol on-open on-close on-message on-error
                  reconnect? next-reconnect]
           :or {reconnect? true}}]]
  (let [ws (goog.net.WebSocket. reconnect? next-reconnect)]
    ; Set up callback listeners
    (when on-open
      (events/listen ws et/OPENED #(on-open ws)))
    (when on-message
      (events/listen ws et/MESSAGE #(on-message ws (.-message %))))
    (when on-error
      (events/listen ws et/ERROR #(on-error ws %)))
    (when on-close
      (events/listen ws et/CLOSED #(on-close ws)))
    ; Connect to websocket server
    (.log js/console (str "OPEN " uri))
    (.open ws uri protocol)
    ws))

(defn close!
  "Closes WebSocket"
  [ws]
  (.close ws))

(defn send!
  "Sends a message to server"
  [ws msg]
  ;(.log js/console "SND" msg)
  (.send ws msg))
