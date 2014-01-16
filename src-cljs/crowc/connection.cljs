(ns crowc.connection
  (:require [crowc.wamp :refer [wamp-handler auth! subscribe! publish! rpc!]]))


(defn connect
  [url]
  (wamp-handler url
                {:on-open (fn on-open
                            [ws sess-id]
                            (.log js/console "OPENED " sess-id)
                            (auth! ws "guest" "secret-password"
                                   (fn on-auth [_ success? result]
                                     (if success?
                                       (do
                                         (set! (.-authenticated ws) true)
                                         (.log js/console "Authenticated:" (pr-str result))
                                         (subscribe! ws "crows/event#world")
                                         (subscribe! ws "crows/event#chat")
                                         (publish! ws "crows/event#chat" "Hi hi" "hi"))
                                       (.log js/console "Failed to authenticate")))))
                 :on-close (fn on-close
                             [ws]
                             (set! (.-authenticated ws) false)
                             (.log js/console "closed"))
                 :on-error (fn on-error
                             [ws]
                             (set! (.-authenticated ws) false)
                             (.log js/console "ERROR"))
                 :on-event (fn on-event
                             [ws topic event]
                             (.log js/console "EVENT " event))}))

(defn pose
  [connection location heading]
  (when (.-authenticated connection)
    (publish! connection "crows/event#pose" [location heading])))
