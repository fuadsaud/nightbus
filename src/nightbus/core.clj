(ns nightbus.core
  (:gen-class)
  (:require [nightbus.boundaries.mqtt.subscriber :as mqtt.sub]
            [nightbus.boundaries.mqtt.publisher :as mqtt.pub]
            [nightbus.boundaries.http.client :as http.client]
            [nightbus.boundaries.http.server :as http.server]
            [nightbus.boundaries.coap.client :as coap.client]
            [nightbus.boundaries.coap.server :as coap.server]
            [nightbus.boundaries.stdout :as stdout]
            [taoensso.timbre :as log]))

(def config
  {:mqtt-broker  {:host "127.0.0.1" :port "1883"}
   :http-server  {:host "127.0.0.1" :port 8080}
   :http-client  {:host "127.0.0.1" :port 8081}
   :coap-server  {:host "127.0.0.1" :port 5683}
   :coap-client  {:host "127.0.0.1" :port 5684}
   :kafka-broker {:host "127.0.0.1" :port 9092}})

(def boundaries
  {"mqtt-subscriber" mqtt.sub/start!
   "mqtt-publisher"  mqtt.pub/start!
   "http-client"     http.client/start!
   "http-server"     http.server/start!
   "stdout"          stdout/start!
   "coap-server"     coap.server/start!
   "coap-client"     coap.client/start!})

(defn -main
  [boundary-name & _]
  (if-let [boundary-fn (get boundaries boundary-name)]
    (boundary-fn config)
    (log/error (str "unknwown boundary \""
                    boundary-name
                    "\"; available boundaries are: "
                    (keys boundaries)))))
