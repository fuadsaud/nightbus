(ns nightbus.boundaries.mqtt.publisher
  (:require [clojurewerkz.machine-head.client :as mh]
            [taoensso.timbre :as log]
            [nightbus.components :as components]
            [nightbus.kafka.consumer :as kafka.consumer]))

(def publish mh/publish)

(defn kafka->mqtt! [mqtt-broker kafka-consumer]
  (kafka.consumer/consume! kafka-consumer
                           "test"
                           kafka.consumer/kafka->mqtt
                           #(log/debug %)))

(defn start! [{mqtt-broker-config :mqtt-broker
               kafka-consumer-config :kafka-consumer}]
  (let [mqtt-broker (components/mqtt-broker mqtt-broker-config)
        kafka-consumer (components/kafka-consumer kafka-consumer-config)]
    (kafka->mqtt! mqtt-broker kafka-consumer)))
