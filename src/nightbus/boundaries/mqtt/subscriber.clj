(ns nightbus.boundaries.mqtt.subscriber
  (:require [clojurewerkz.machine-head.client :as mh]
            [taoensso.timbre :as log]
            [nightbus.components :as components]
            [nightbus.kafka.producer :as kafka.producer]))

(def ^:private format-payload (comp (partial apply str) (partial map char)))

(defn- produce! [kafka-producer topic payload]
  (kafka.producer/produce! kafka-producer {:topic topic :payload payload}))

(defn- catch-all-handler [{:keys [kafka-producer]} topic _ payload]
  (nightbus.utils/tap {::handler {:topic topic :payload (format-payload payload)}})
  (produce! kafka-producer topic payload))

(defn- exit-handler
  [{:keys [mqtt-broker]} _ _ _]
  (mh/disconnect mqtt-broker)
  (System/exit 0))

(def subscriptions
  {"#"     {:qos 0 :fn catch-all-handler}
   "$exit" {:qos 0 :fn exit-handler}})

(defn- subscribe-all!
  [{:keys [mqtt-broker] :as components} subscriptions]
  (doseq [[topic {:keys [qos fn]}] subscriptions]
    (mh/subscribe mqtt-broker {topic qos} (partial fn components))))

(defn start! [{mqtt-broker-config :mqtt-broker
               kafka-producer-config :kafka-broker}]
  (let [components {:mqtt-broker (components/mqtt-broker mqtt-broker-config)
                    :kafka-producer (components/kafka-producer kafka-producer-config)}]
    (subscribe-all! components subscriptions)))
