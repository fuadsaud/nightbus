(ns nightbus.boundaries.mqtt.publisher
  (:require [clojurewerkz.machine-head.client :as mh]
            [clojure.tools.logging :as log]))

(defn- broker-address-from-config [config]
  (str "tcp://" (:host config) ":" (:port config)))

(defn start! [{:keys [broker]}]
  (let [broker-address (broker-address-from-config broker)
        conn (mh/connect broker-address)]
    (mh/publish conn "beth" "aqui tambem é beth")))
