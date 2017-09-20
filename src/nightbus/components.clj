(ns nightbus.components
  (:require [clj-kafka.producer :as kafka.producer]))

(defn kafka-producer [config]
  (kafka.producer/producer {"metadata.broker.list" (str (:host config) ":" (:port config))
                            "serializer.class" "kafka.serializer.DefaultEncoder"
                            "partitioner.class" "kafka.producer.DefaultPartitioner"}))
