(ns crows.connection
  (:require [crows.world :refer [update-player-command]]
            [crows.publisher :refer [init]]
            [clj-wamp.server :refer [client-topics topic-unsubscribe with-channel-validation http-kit-handler]]
            [taoensso.timbre :refer [log  trace  debug  info  warn  error  fatal  report spy]]))


;; Topic BaseUrls
(def base-url "crows")
(def rpc-base-url (str base-url "/rpc#"))
(def evt-base-url (str base-url "/event#"))

(defn rpc-url [path] (str rpc-base-url path))
(defn evt-url [path] (str evt-base-url path))

;; HTTP Kit/WAMP WebSocket handler

(defn- on-open [system sess-id]
  (info "WAMP client connected [" sess-id "]")
  (init system [0] sess-id))

(defn- on-close [system sess-id status]
  (info "WAMP client disconnected [" sess-id "] " status))

(defn- auth-secret
  "Returns the auth key's secret (ie. password), typically retrieved from a database."
  [sess-id auth-key extra]
  "secret-password")

(defn- auth-permissions
  "Returns the permissions for a client session by auth key."
  [sess-id auth-key]
  {:rpc       {(rpc-url "ping")   true}
   :subscribe {(evt-url "chat")   true
               (evt-url "world")  true
               (evt-url "world*") true}
   :publish   {(evt-url "chat")   true
               (evt-url "pose")   true}})

(defn- username
  [sess-id]
  (get-in @clj-wamp.server/client-auth [sess-id :key]))

(defn- subscribe-world
  [system sess-id topic]
  (println "SUB" topic)
  #_(doseq [t (client-topics sess-id)]
    (topic-unsubscribe t sess-id))
  (init @(system :world) topic sess-id))

(defn- pub-pose
  [system sess-id topic [position heading] exclude eligible]
  (update-player-command system
                         (username sess-id) position heading sess-id))

(defn- on-subscribe [system sess-id topic]
  (when (re-matches  #"crows/event#world.*"topic)
    (subscribe-world system sess-id topic)))

(defn new-event-map
  [system]
  {:on-open        (partial on-open system)
   :on-close       (partial on-close system)
   :on-call        {(rpc-url "ping")   (fn [] "pong")}
   :on-subscribe   {(evt-url "chat")   true
                    (evt-url "world")  true
                    (evt-url "world*") true
                    :on-after          (partial on-subscribe system)}
   :on-publish     {(evt-url "chat")   true
                    (evt-url "pose")   (partial pub-pose system)}
   :on-auth        {:secret            auth-secret
                    :permissions       auth-permissions}})

(defn new-wamp-handler
  "Returns a http-kit websocket handler with wamp subprotocol"
  [system]
  (let [event-map (new-event-map system)]
    (fn wamp-handler [req]
      (with-channel-validation req channel #"https?://localhost:8080"
        (http-kit-handler channel event-map)))))
