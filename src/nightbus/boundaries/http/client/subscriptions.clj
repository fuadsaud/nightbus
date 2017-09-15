(ns nightbus.boundaries.http.client.subscriptions
  (:require [nightbus.boundaries.http.client.subscriptions.store :as store]
            [clojure.tools.logging :as log]))

(def subscriptions (atom {}))

(defn subscribe! [{:keys [url method body] :as subscription}]
  (let [topic (:topic body) subscriber (select-keys body [:url :method])]
    (log/info (str "[HTTP CLIENT]: "
                   {:subscribe {:topic topic :subscriber subscriber}}))
    (swap! subscriptions update topic conj subscriber)))
