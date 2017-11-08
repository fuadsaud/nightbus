(ns nightbus.config)

(def config
  {:mqtt-broker  {:host "127.0.0.1" :port "1883"}
   :http-client  {:host "127.0.0.1" :port 8080}
   :http-server  {:host "127.0.0.1" :port 8081}
   :kafka-broker {:host "127.0.0.1" :port 9092}})

