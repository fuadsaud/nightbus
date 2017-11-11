(ns nightbus.boundaries.http.client.subscriptions
  (:require [taoensso.timbre :as log]))

(def subscriptions (atom {}))

(defn subscribe! [{:keys [topic] :as subscription}]
  (let [subscriber (select-keys subscription [:url :method])]
    (log/info (str "[HTTP SUBS]: "
                   {:subscribe {:topic topic :subscriber subscriber}}))
    (swap! subscriptions update topic conj subscriber)))

(defn subscribers-of [topic]
  (get @subscriptions topic))
