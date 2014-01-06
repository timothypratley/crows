(ns crows.wamp
  (:require [crows.world :refer [update-player-command]]
            [clj-wamp.server]
            [taoensso.timbre :refer [log  trace  debug  info  warn  error  fatal  report spy]]))


;; Topic BaseUrls
(def base-url "crows")
(def rpc-base-url (str base-url "/rpc#"))
(def evt-base-url (str base-url "/event#"))

(defn rpc-url [path] (str rpc-base-url path))
(defn evt-url [path] (str evt-base-url path))

;; HTTP Kit/WAMP WebSocket handler

(defn- on-open [sess-id]
  (info "WAMP client connected [" sess-id "]"))

(defn- on-close [sess-id status]
  (info "WAMP client disconnected [" sess-id "] " status))

(defn- on-publish [sess-id topic event exclude include]
  (info "WAMP publish:" sess-id topic event exclude include))

(defn- on-before-call [sess-id topic call-id call-params]
  (info "WAMP call:" sess-id topic call-id call-params)
  [sess-id topic call-id call-params])

(defn- auth-secret
  "Returns the auth key's secret (ie. password), typically retrieved from a database."
  [sess-id auth-key extra]
  "secret-password")

(defn- auth-permissions
  "Returns the permissions for a client session by auth key."
  [sess-id auth-key]
  {:rpc       {(rpc-url "update") true}
   :subscribe {(evt-url "chat")   true}
   :publish   {(evt-url "chat")   true
               (evt-url "update") true}})

(defn- username [sess-id]
  (get-in @clj-wamp.server/client-auth [sess-id :key]))

(defn wamp-handler
  "Returns a http-kit websocket handler with wamp subprotocol"
  [req]
  (clj-wamp.server/with-channel-validation req channel #"https?://localhost:8080"
    (clj-wamp.server/http-kit-handler channel
      {:on-open        on-open
       :on-close       on-close
       :on-call        {(rpc-url "update") (fn wamp-update
                                             [sess-id [position heading]]
                                             (update-player-command (username sess-id) position heading))
                        (rpc-url "ping")  (fn [] "pong")
                        :on-before        on-before-call}
       :on-subscribe   {(evt-url "chat")  true}
       :on-publish     {(evt-url "chat")  true
                        (evt-url "update") (fn update-player
                                             [sess-id topic [position heading] exclude eligible]
                                             (update-player-command (username sess-id) position heading))
                        :on-after         on-publish}
       :on-auth        {:secret           auth-secret
                        :permissions      auth-permissions}})))
