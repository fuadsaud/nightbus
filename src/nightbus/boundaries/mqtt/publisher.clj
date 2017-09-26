(ns nightbus.boundaries.mqtt.publisher
  (:require [clojurewerkz.machine-head.client :as mh]
            [taoensso.timbre :as log]
            [nightbus.components :as components]
            [nightbus.kafka.consumer :as kafka.consumer]))

(def consumer-config {:group "mqtt-publisher"})

(defn- publish! [mqtt-broker {:keys [topic value]}]
  (nightbus.utils/tap {::publishing {:topic topic :value value}})
  (mh/publish mqtt-broker topic value))

(defn- kafka->mqtt! [kafka-consumer mqtt-broker]
  (kafka.consumer/consume! kafka-consumer "test" identity (partial publish! mqtt-broker)))

(defn start!
  [{zk-connect :zk-connect mqtt-broker-config :mqtt-broker}]
  (let [mqtt-broker (components/mqtt-broker mqtt-broker-config)
        kafka-consumer (components/kafka-consumer (merge consumer-config zk-connect))]
    (kafka->mqtt! kafka-consumer mqtt-broker)))
