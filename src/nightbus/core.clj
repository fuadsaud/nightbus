(ns nightbus.core
  (:gen-class)
  (:require [nightbus.boundaries.mqtt.subscriber :as mqtt.sub]
            [nightbus.boundaries.mqtt.publisher :as mqtt.pub]
            [nightbus.boundaries.http.client :as http.client]
            [nightbus.boundaries.http.server :as http.server]
            [taoensso.timbre :as log]))

(def mqtt-broker-config {:host "127.0.0.1" :port "1883"})
(def http-client-config {:host "127.0.0.1" :port 8080})
(def http-server-config {:host "127.0.0.1" :port 8081})
(def kafka-broker-config {:host "127.0.0.1" :port 9092})

(def boundaries
  {"mqtt-subscriber" #(mqtt.sub/start! {:broker mqtt-broker-config})
   "mqtt-publisher" #(mqtt.pub/start! {:broker mqtt-broker-config})
   "http-client" #(http.client/start! {:server http-client-config})
   "http-server" #(http.server/start! {:http-server http-server-config
                                       :kafka-producer kafka-broker-config})})

(defn -main
  [boundary-name & _]
  (if-let [boundary-fn (get boundaries boundary-name)]
    (boundary-fn)
    (log/error (str "unknwown boundary \""
                    boundary-name
                    "\"; available boundaries are: "
                    (keys boundaries)))))
