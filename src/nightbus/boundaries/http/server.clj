(ns nightbus.boundaries.http.server
  (:require [nightbus.kafka.producer :as kafka.producer]
            [nightbus.components :as components]
            [nightbus.instrument :as instrument]
            [bidi.ring :refer [make-handler]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.params :as params]
            [ring.logger.timbre :as logger.timbre]
            [ring.util.response :as response]
            [ring.util.request :as request]
            [taoensso.timbre :as log])
  (:import (org.apache.commons.io IOUtils)))

(defn produce-message-handler
  [{:keys [kafka-producer]} request]
  (instrument/log-metric! :http-server-in-request)
  (let [topic (get (:query-params request) "topic")
        payload (-> request :body IOUtils/toByteArray)]
    (kafka.producer/produce! kafka-producer {:topic topic :payload payload}))
  {:status 202})

(defn routes [components]
  ["/"
   {"messages"
    {:post {"" (partial produce-message-handler components)}}}] )

(defn app [components]
  (-> components
      routes
      make-handler
      params/wrap-params
      logger.timbre/wrap-with-logger))

(defn start!
  [{http-server-config :http-server
    kafka-producer-config :kafka-broker}]
  (let [components {:kafka-producer (components/kafka-producer kafka-producer-config)}]
    (jetty/run-jetty (app components) (select-keys http-server-config [:port]))))
