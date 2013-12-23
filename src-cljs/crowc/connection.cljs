(ns crowc.connection
  (:require [crowc.wamp-client :as wamp]))


(defn connect
  []
  (wamp/wamp-handler
   "ws://localhost:8080/wamp"
   {:on-open (fn on-open
               [ws sess-id]
               (.log js/console "OPENED " sess-id)
               (wamp/auth! ws "guest" "secret-password"
                           (fn on-auth [_ success? result]
                             (if success?
                               (do
                                 (.log js/console "Authenticated:" (pr-str result))
                                 (wamp/subscribe! ws "crows/event#chat")
                                 (wamp/publish! ws "crows/event#chat" "Hi hi" "hi")
                                 (wamp/rpc! ws "crows/rpc#echo" ["hi"]
                                            (fn [_ success? result]
                                              (.log js/console "RPC:" success? (pr-str result))))
                                 #_(wamp/send! ws "custom message"))
                               (.log js/console "Failed to authenticate")))))
    :on-close (fn on-close
                []
                (.log js/console "closed"))
    :on-event (fn on-event
                [ws topic event]
                (.log js/console "EVENT " event))}))
