(ns crowc.wamp-client
  (:require [crowc.websocket :as websocket]
            [clojure.string :as string :refer [trim blank?]]
            [goog.crypt :as crypt]
            [goog.crypt.Hmac :as hmac]
            [goog.crypt.Sha256 :as sha256]
            [goog.crypt.base64 :as base64]))


(def ^:const WELCOME     0) ; Server-to-client (Aux)
(def ^:const PREFIX      1) ; Client-to-server (Aux)
(def ^:const CALL        2) ; Client-to-server (RPC)
(def ^:const CALLRESULT  3) ; Server-to-client (RPC)
(def ^:const CALLERROR   4) ; Server-to-client (RPC)
(def ^:const SUBSCRIBE   5) ; Client-to-server (PubSub)
(def ^:const UNSUBSCRIBE 6) ; Client-to-server (PubSub)
(def ^:const PUBLISH     7) ; Client-to-server (PubSub)
(def ^:const EVENT       8) ; Server-to-client (PubSub)

(def rpc-callbacks (atom {}))

(defn- send!
  [ws msg]
  (let [m (JSON/stringify (clj->js msg))]
    (websocket/send! ws m)))

(defn prefix!
  [ws prefix curi]
  (send! ws [PREFIX prefix curi]))

(defn rpc!
  [ws curi params callback]
  (let [call-id (.toString (.random js/Math) 36) ; TODO
        msg (into [CALL call-id curi] params)]
    (swap! rpc-callbacks assoc call-id callback)
    (send! ws msg)))

(defn subscribe!
  [ws curi]
  (send! ws [SUBSCRIBE curi]))

(defn unsubscribe!
  [ws curi]
  (send! ws [UNSUBSCRIBE curi]))

(defn publish!
  [ws curi event & more]
  (let [msg [PUBLISH curi event]]
    (send! ws (if more (into msg more) msg))))

(defn close!
  [ws]
  (websocket/close! ws))

(defn- on-message
  [ws data on-open on-event]
  (let [msg (js->clj (JSON/parse data))]
    ;(.log js/console "WAMP message" (pr-str msg))
    (condp = (first msg)
      WELCOME
      ; don't start until Welcome message received
      (on-open ws (second msg))

      CALLRESULT
      (let [call-id (second msg)
            result  (last msg)]
        (when-let [rpc-cb (get @rpc-callbacks call-id)]
          (rpc-cb ws true result)
          (swap! rpc-callbacks dissoc call-id)))

      CALLERROR
      (let [call-id  (second msg)
            err-info (drop 2 msg)]
        (when-let [rpc-cb (get @rpc-callbacks call-id)]
          (rpc-cb ws false err-info)
          (swap! rpc-callbacks dissoc call-id)))

      EVENT
      (let [topic (second msg)
            event (last msg)]
        (on-event ws topic event))

      (.log js/console "Unknown message" data))))

(defn wamp-handler
  [uri & [{:keys [on-open on-close on-event
                  reconnect? next-reconnect]
           :or {reconnect? true}}]]
  (websocket/connect! uri
    {:reconnect? reconnect?
     :next-reconnect next-reconnect
     :on-message (fn [ws data]
                   (on-message ws data on-open on-event))
     :on-close on-close
     :protocol "wamp"}))

;; CR-Authentication

(defn authsign
  [password challenge]
  (let [challbytes (.stringToByteArray goog.crypt challenge)
        passbytes  (.stringToByteArray goog.crypt password)
        hasher     (goog.crypt.Sha256.)]
    (.encodeByteArray goog.crypt.base64
      (.getHmac (goog.crypt.Hmac. hasher challbytes 64) passbytes))))

;; CryptoJS example
;(defn authsign [password challenge]
;  (let [hmaced (.HmacSHA256 js/CryptoJS password challenge)]
;    (.toString hmaced js/CryptoJS.enc.Base64)))

(defn auth!
  ([ws userkey password callback]
    (auth! ws userkey password authsign callback))
  ([ws userkey password sign-fn callback]
    (rpc! ws "http://api.wamp.ws/procedure#authreq" [userkey]
      (fn [ws success? challenge]
        (if success?
          (let [signature (sign-fn password challenge)]
            (rpc! ws "http://api.wamp.ws/procedure#auth" [signature] callback))
          (callback ws false challenge))))))
