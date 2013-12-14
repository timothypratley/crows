(ns crowc.connection
  (:require [crowc.wamp-client :as wamp]
            [crowc.websocket-client :as websocket]))

(defn connect-wamp
  []
  (let [on-event (fn [ws topic event] (.log js/console "EVENT " event))
        on-open (fn [ws sess-id]
                  (.log js/console "OPENED " sess-id)
                  (wamp/subscribe! ws "http://wamptutorial/event#chat")
                  (wamp/publish! ws "http://wamptutorial/event#chat" "Hi hi" "hi")
                  (wamp/rpc! ws "http://wamptutorial/rpc#echo" "hi" identity))
        on-close (fn [] (.log js/console "closed"))
        ws (wamp/wamp-handler "ws://localhost:8080/wamp"
                                {:websocket-client websocket/client
                                 :on-open  on-open
                                 :on-close on-close
                                 :on-event on-event})]))

(defn connect-ws
  []
  (let [conn (js/WebSocket. "ws://localhost:8080/ws")]
    (set! (.-onopen conn)
          (fn [e]
            (.log js/console "connected")
            (.send conn "hello")))

    (set! (.-onerror conn)
          (fn []
            (js/alert "error")
            (.log js/console js/arguments)))

    (set! (.-onmessage conn)
          (fn [e]
            (.log js/console e)))))
