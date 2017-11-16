(ns nightbus.boundaries.coap.server
  (:require [nightbus.kafka.producer :as kafka.producer]
            [nightbus.components :as components]
            [nightbus.instrument :as instrument]
            [taoensso.timbre :as log])
  (:import (org.eclipse.californium.core CoapServer CoapResource)
           (org.eclipse.californium.core.coap CoAP$ResponseCode)
           (org.eclipse.californium.core.network CoapEndpoint)
           (java.net InetAddress InetSocketAddress)))

(defn make-messages-resource
  [{:keys [kafka-producer]}]
  (proxy [CoapResource] ["messages"]
    (handlePOST [exchange]
      (instrument/log-metric! :coap-server-in-request)
      (let [payload (nightbus.utils/tap (.getRequestPayload exchange))
            topic (nightbus.utils/tap (.getQueryParameter exchange "topic"))]
        (log/info (str "[COAP SERVER] " {:post-message {:topic topic}}))
        (kafka.producer/produce! kafka-producer {:topic topic :payload payload})
        (instrument/log-metric! :coap-server-out-request)
        (.respond exchange CoAP$ResponseCode/CHANGED)))))

(defn start!
  [{coap-server-config :coap-server
    kafka-producer-config :kafka-broker}]
  (let [components {:kafka-producer (components/kafka-producer kafka-producer-config)}
        inet-address (InetAddress/getByName (:host coap-server-config))
        inet-socket-address (InetSocketAddress. inet-address (:port coap-server-config))
        coap-server (CoapServer.)
        coap-endpoint (CoapEndpoint. inet-socket-address)
        messages-resource (make-messages-resource components)]
    (.add coap-server (into-array CoapResource [messages-resource]))
    (.addEndpoint coap-server coap-endpoint)
    (.start coap-server)))
