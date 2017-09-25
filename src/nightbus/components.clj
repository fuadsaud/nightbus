(ns nightbus.components
  (:require [clj-kafka.producer :as kafka.producer]
            [clj-kafka.consumer.zk :as kafka.consumer]
            [clojurewerkz.machine-head.client :as mh]))

(defn- address-from-config [config]
  (str (:host config) ":" (:port config)))

(defn- tcp-url-from-config [config]
  (str "tcp://" (address-from-config config)))

(defn kafka-producer [config]
  (kafka.producer/producer {"metadata.broker.list" (address-from-config config)
                            "serializer.class" "kafka.serializer.DefaultEncoder"
                            "partitioner.class" "kafka.producer.DefaultPartitioner"}))

(defn kafka-consumer [config]
  (kafka.consumer/consumer {"zookeeper.connect" (address-from-config config)
                            "group.id" "nightbus.consumer"
                            "auto.offset.reset" "smallest"
                            "auto.commit.enable" "false"}))

(defn mqtt-broker [config]
  (let [broker-address (tcp-url-from-config config)]
    (mh/connect broker-address)))
