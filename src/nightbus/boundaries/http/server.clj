(ns nightbus.boundaries.http.server
  (:require [nightbus.kafka.producer :as kafka.producer]
            [nightbus.components :as components]
            [bidi.ring :refer [make-handler]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as json]
            [ring.middleware.reload :as reload]
            [ring.logger.timbre :as logger.timbre]
            [ring.util.response :as response]
            [taoensso.timbre :as log]))

(defn produce-message-handler
  [{:keys [kafka-producer]} request]
  (->> request
       :body
       (kafka.producer/produce! kafka-producer))
  (response/response {:status 202 :body "Produced!"}))

(defn routes [components]
  ["/"
   {"messages"
    {:post {"" (partial produce-message-handler components)}}}] )

(defn app [components]
  (-> components
      routes
      make-handler
      json/wrap-json-response
      (json/wrap-json-body {:keywords? true})
      logger.timbre/wrap-with-logger))

(defn start!
  [{http-server-config :http-server
    kafka-producer-config :kafka-broker}]
  (let [components {:kafka-producer (components/kafka-producer kafka-producer-config)}]
    (jetty/run-jetty (app components) (select-keys http-server-config [:port]))))
