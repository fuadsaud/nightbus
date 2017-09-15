(ns nightbus.core
  (:gen-class)
  (:require [nightbus.boundaries.mqtt.subscriber :as mqtt.sub]
            [nightbus.boundaries.mqtt.publisher :as mqtt.pub]
            [nightbus.boundaries.http.client :as http.client]
            [clojure.tools.logging :as log]))

(def broker-config {:host "127.0.0.1" :port "1883"})
(def http-client-config {:host "127.0.0.1" :port 8080})
(def http-server-config {:host "127.0.0.1" :port "8081"})

(defn -main
  [boundary & _]
  (condp = boundary
    "mqtt-subscriber" (mqtt.sub/start! {:broker broker-config})
    "mqtt-publisher" (mqtt.pub/start! {:broker broker-config})
    "http-client" (http.client/start! {:server http-client-config})
    ; "http-client" (http.client/start! {:server http-client-config})
    (log/error (str "unknwown boundary "
                    boundary
                    "; available boundaries are: "
                    ["mqtt-publisher" "mqtt-subscriber"]))))
