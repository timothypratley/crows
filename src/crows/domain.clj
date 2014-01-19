(ns crows.domain)


(defmulti accept (fn [domain event] (:event event)))

(defonce domain (atom {}))
(defonce publish (atom (constantly true)))
(defonce store (atom (constantly true)))

(let [o (Object.)
      event-count (atom 0)]

  (defn raise!
    "Raising an event stores it, publishes it, and updates the domain model."
    [event]
    (locking o
      (let [before @domain
            event (assoc event
                    :when (java.util.Date.)
                    :seq (swap! event-count inc))
            after (swap! domain accept event)]
        (@store event after)
        (@publish event before after)
        (event :seq)))))


(defonce actions (atom #{}))
(defonce actions-by-name (atom {}))

(defn functions
  "Get the public functions of a namespace and create a map keyed off the name of the function"
  [name-space]
  (into {} (map (fn [[sym f]]
                  [(name sym) f])
                (filter ifn? (ns-publics name-space)))))

(defn attach-actions
  "Attach actions to public functions in an event namespace"
  [events-ns]
  (reset! actions-by-name (functions events-ns))
  (reset! actions (set (vals @actions-by-name))))

(defn command
  "Calling a command is attempting to perform an action on the domain.
  Commands are invoked by an action name, as they are sent from a client.
  Returns the sequence id of the new domain if successful.
  An exception or nil indicates failure."
  [action & args]
  {:pre (@actions action)}
  (when-let [event (apply action @domain args)]
    (raise! event)))


(defn command-by-name
  "Call a command by string name."
  [action-name & args]
  (if-let [action (@actions action-name)]
    (when-let [event (apply action @domain args)]
      (raise! event))
    (throw (java.lang.IllegalArgumentException.
            (str "Command " action-name " not found")))))
