(ns nightbus.kafka.consumer
  (:require [clj-kafka.consumer.zk :as kafka.consumer]
            [clj-kafka.core :as kafka.core]))

(def ^:private internalize kafka.core/to-clojure)

(defn consume! [kafka-consumer topic xform f]
  (let [kafka-stream (kafka.consumer/create-message-stream kafka-consumer topic)]
    (kafka.core/with-resource [c kafka-consumer]
      kafka.consumer/shutdown
      (run! f (eduction (comp (map internalize) xform) kafka-stream)))))
