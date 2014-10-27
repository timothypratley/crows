(ns crows.connection
  (:require
   [clojure.core.match :refer [match]]
   [crows.domain :refer [command domain actions-by-name]]
   [taoensso.sente :as sente]
   [taoensso.timbre :refer [debug info warn]]))

(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn connected-uids]} (sente/make-channel-socket! {})]
  (defonce ajax-post ajax-post-fn)
  (defonce ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (defonce recv ch-recv)
  (defonce send! send-fn)
  (defonce connected-uids connected-uids))

(defn login! [ring-request]
  (let [{:keys [session params]} ring-request
        {:keys [user-id]} params]
    (info "Login request: " params)
    {:status 200 :session (assoc session :uid user-id)}))

(defn broadcast-event! [msg except]
  (doseq [uid (:any @connected-uids)
          :when (not= uid except)]
    (send! uid msg)))

(defn publish [{:keys [event uid] :as event-data} before after]
  (let [data (if (= :pose event)
               (map event-data [:id :location :heading])
               (dissoc event-data :event))]
    (broadcast-event! [event data] uid)))

(defn init [world path uid]
  (send! uid [:crows/world path
              (get-in world [:root path])]))

(defn- on-open [uid]
  (info "Client connected [" uid "]")
  (init @domain [0] uid))

(defn- on-close [uid status]
  (info "Client disconnected [" uid "] " status))

(defn- subscribe-world
  [uid topic]
  (info "SUB" topic)
  #_(doseq [t (client-topics uid)]
      (topic-unsubscribe t uid))
  (init @domain topic uid))

(defn- on-subscribe [uid topic]
  (when (re-matches  #"crows/event#world.*"topic)
    (subscribe-world uid topic)))

(defn- event-action [uid action-name args]
  (when-let [action (@actions-by-name action-name)]
    (try
      (apply command action uid args)
      (catch Exception e
        (warn (.getMessage e))))))

(def user-states (ref nil))

(defn update [uid app-state]
  (dosync
   (alter user-states assoc uid app-state)))

(defn on-pose [p])

(defn- event-msg-handler
  [{:keys [ring-req event]} _]
  (let [session (:session ring-req)
        uid (:uid session)
        [id data :as ev] event]

    (debug "Event:" ev)

    (match
     [id data]
     [:chsk/ws-ping _]
     :ignore

     [:crows/app-state app-state]
     (if uid
       (update uid app-state)
       (warn "Received app-state but no uid."))

     [:crows/pose p]
     (on-pose p)

     [:chsk/uidport-open _]
     (on-open uid)

     [:chsk/uidport-close _]
     (on-close uid nil)

     :else
     (or (event-action uid id data)
         (warn "Unmatched event:" ev)))))

(defonce chsk-router
  (sente/start-chsk-router-loop! recv #'event-msg-handler))
