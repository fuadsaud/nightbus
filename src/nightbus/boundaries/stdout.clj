(ns nightbus.boundaries.stdout
  (:require [puget.printer :as puget]
            [nightbus.components :as components]
            [nightbus.kafka.consumer :as kafka.consumer]))

(def print puget/cprint)

(defn- kafka->stdout! [kafka-consumer]
  (kafka.consumer/consume! kafka-consumer
                           "test"
                           kafka.consumer/kafka->internal
                           print))

(defn start! [{kafka-consumer-config :kafka-consumer}]
  (let [kafka-consumer (components/kafka-consumer kafka-consumer-config)]
    (kafka->stdout! kafka-consumer)))
