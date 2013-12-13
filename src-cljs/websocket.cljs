
(ns clj-wamp.websocket
  (:require [goog.events :as events]
            [goog.net.WebSocket.EventType :as websocket-event]))

; Based on code by @neotyk (Thanks!)
; https://github.com/neotyk/ws-cljs/blob/master/src/cljs/websocket.cljs

(defn client
  "CLJS WebSocket client wrapper.

  Example usage:
    (let [ws (websocket/client \"ws://host:port/path\"
            {:protocol \"optional\"
             :on-open (fn [ws]
                        (.log js/console \"connected\"))
             :on-close (fn []
                        (.log js/console \"closed\"))
             :on-message (fn [ws data]
                           (.log js/console \"got data:\" data))})]
       (websocket/send! ws \"my message\")
       (websocket/close! ws))"
  [uri & [{:keys [protocol on-open on-close on-message on-error
                  reconnect? next-reconnect]
           :or {reconnect? true}}]]
  (let [ws (goog.net.WebSocket. reconnect? next-reconnect)]
    ; Set up callback listeners
    (when on-open
      (events/listen ws websocket-event/OPENED #(on-open ws)))
    (when on-message
      (events/listen ws websocket-event/MESSAGE #(on-message ws (.-message %))))
    (when on-error
      (events/listen ws websocket-event/ERROR on-error))
    (when on-close
      (events/listen ws websocket-event/CLOSED #(on-close)))
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
