(ns nightbus.kafka.consumer
  (:require [clojure.data.json :as json]
            [clj-kafka.consumer.zk :as kafka.consumer]
            [clj-kafka.core :as kafka.core]))

(defn- from-wire [message] (-> message
                               kafka.core/to-clojure
                               (update :value (comp (partial apply str)
                                                    (partial map char)))))

(defn- parse-payload [message] (update message :value json/read-str))

(defn- to-mqtt-wire [message] message)

(def kafka->mqtt
  (comp (map from-wire)
        (map parse-payload)
        (map to-mqtt-wire)))

(def kafka->internal
  (comp (map from-wire)
        (map parse-payload)))

(defn consume! [kafka-consumer topic xform f]
  (let [kafka-stream (kafka.consumer/create-message-stream kafka-consumer topic)]
    (kafka.core/with-resource [c kafka-consumer]
      kafka.consumer/shutdown
      (run! f (eduction xform kafka-stream)))))
