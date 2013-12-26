(ns crowc.connection
  (:require [crowc.wamp :refer [wamp-handler auth! subscribe! publish! rpc!]]))


(let [gws (atom nil)]

  (defn connect
    [url]
    (wamp-handler
     url
     {:on-open (fn on-open
                 [ws sess-id]
                 (.log js/console "OPENED " sess-id)
                 (auth! ws "guest" "secret-password"
                        (fn on-auth [_ success? result]
                          (if success?
                            (do
                              (reset! gws ws)
                              (.log js/console "Authenticated:" (pr-str result))
                              (subscribe! ws "crows/event#chat")
                              (publish! ws "crows/event#chat" "Hi hi" "hi"))
                            (.log js/console "Failed to authenticate")))))
      :on-close (fn on-close
                  []
                  (reset! gws nil)
                  (.log js/console "closed"))
      :on-event (fn on-event
                  [ws topic event]
                  (.log js/console "EVENT " event))}))

  (defn update [location heading]
    (when @gws
      (rpc! @gws "crows/rpc#update" [location heading] (fn [_ _ _])))))
