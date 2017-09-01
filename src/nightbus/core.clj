(ns nightbus.core
  (:gen-class)
  (:require [nightbus.boundaries.mqtt.subscriber :as subscriber]
            [nightbus.boundaries.mqtt.publisher :as publisher]
            [clojure.tools.logging :as log]))

(def broker-config {:host "127.0.0.1" :port "1883"})

(defn -main
  [boundary & _]
  (condp = boundary
    "mqtt-subscriber" (subscriber/start! {:broker broker-config})
    "mqtt-publisher" (publisher/start! {:broker broker-config})
    (log/error (str "unknwown boundary "
                    boundary
                    "; available boundaries are: "
                    ["mqtt-publisher" "mqtt-subscriber"]))))
