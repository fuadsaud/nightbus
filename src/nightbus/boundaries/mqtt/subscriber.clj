(ns nightbus.boundaries.mqtt.subscriber
  (:require [clojurewerkz.machine-head.client :as mh]
            [taoensso.timbre :as log]
            [nightbus.utils :as utils]))

(defn- broker-address-from-config [config]
  (str "tcp://" (:host config) ":" (:port config)))

(defn start! [{:keys [mqtt-broker]}]
  (let [broker-address (broker-address-from-config mqtt-broker)
        conn (mh/connect broker-address)]
    (mh/subscribe conn {"#" 0} (fn [^String topic _ ^bytes payload]
                                 (utils/tap (String. payload "UTF-8"))))
    (mh/subscribe conn {"$exit" 0} (fn [^String topic _ ^bytes payload]
                                     (mh/disconnect conn)
                                     (System/exit 0)))))
