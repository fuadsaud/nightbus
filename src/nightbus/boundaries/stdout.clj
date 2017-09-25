(ns nightbus.boundaries.stdout
  (:require [clojure.data.json :as json]
            [puget.printer :as puget]
            [nightbus.components :as components]
            [nightbus.kafka.consumer :as kafka.consumer]))

(def ^:private consumer-config {:group "stdout"})

(defn- payload-byte-array->str [message]
  (update message :value (comp (partial apply str)
                               (partial map char))))

(defn- parse-json-payload [message]
  (update message :value json/read-str))

(def ^:private kafka->internal
  (comp (map payload-byte-array->str)
        (map parse-json-payload)))

(def ^:private print puget/cprint)

(defn- kafka->stdout! [kafka-consumer]
  (kafka.consumer/consume! kafka-consumer "test" kafka->internal print))

(defn start! [{zk-connect :zk-connect}]
  (let [kafka-consumer (components/kafka-consumer (merge consumer-config zk-connect))]
    (kafka->stdout! kafka-consumer)))
