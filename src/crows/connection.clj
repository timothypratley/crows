(ns crows.connection
  (:require [crows.domain :refer [command domain actions actions-by-name]]
            [crows.publisher :refer [init]]
            [clj-wamp.server :refer :all]
            [taoensso.timbre :refer [log  trace  debug  info  warn  error  fatal  report spy]]))


;; Topic BaseUrls
(def base-url "crows")
(def rpc-base-url (str base-url "/rpc#"))
(def evt-base-url (str base-url "/event#"))

(defn rpc-url [path] (str rpc-base-url path))
(defn evt-url [path] (str evt-base-url path))

;; HTTP Kit/WAMP WebSocket handler

(defn- on-open [sess-id]
  (info "WAMP client connected [" sess-id "]")
  (init @domain [0] sess-id))

(defn- on-close [sess-id status]
  (info "WAMP client disconnected [" sess-id "] " status))

(defn- auth-secret
  "Returns the auth key's secret (ie. password), typically retrieved from a database."
  [sess-id auth-key extra]
  "secret-password")

(defn- username
  [sess-id]
  (get-in @clj-wamp.server/client-auth [sess-id :key]))

(defn- subscribe-world
  [sess-id topic]
  (println "SUB" topic)
  #_(doseq [t (client-topics sess-id)]
    (topic-unsubscribe t sess-id))
  (init @domain topic sess-id))

(defn- on-subscribe [sess-id topic]
  (when (re-matches  #"crows/event#world.*"topic)
    (subscribe-world sess-id topic)))

(defn- command-for
  [action]
  (fn call-command [sess-id topic args exclude eligible]
    (try
      (apply command action (username sess-id) args)
      (catch Exception e
        (warn (.getMessage e))
        (close-channel sess-id)))))

(defn- event-actions []
  (into {}
        (for [[action-name action] @actions-by-name]
          [(evt-url action-name) (command-for action)])))

(defn- auth-permissions
  "Returns the permissions for a client session by auth key."
  [sess-id auth-key]
  {:rpc       {(rpc-url "ping")   true}
   :subscribe {(evt-url "chat")   true
               (evt-url "world")  true
               (evt-url "world*") true}
   :publish   {(evt-url "chat")   true
               (evt-url "pose")   true}})

(def callbacks
  {:on-open        on-open
   :on-close       on-close
   :on-call        (event-actions)
   :on-subscribe   {(evt-url "chat")   true
                    (evt-url "world")  true
                    (evt-url "world*") true
                    :on-after          on-subscribe}
   :on-publish     (merge {(evt-url "chat")   true}
                          (event-actions))
   :on-auth        {:secret            auth-secret
                    :permissions       auth-permissions}})

(defn wamp-handler
  "A http-kit websocket handler with wamp subprotocol"
  [req]
  (with-channel-validation req channel #"https?://localhost:8080"
    (http-kit-handler channel callbacks)))
