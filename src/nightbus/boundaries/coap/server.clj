(ns nightbus.boundaries.coap.server
  (:require [nightbus.kafka.producer :as kafka.producer]
            [nightbus.components :as components]
            [taoensso.timbre :as log]
            [clojure.data.json :as json])
  (:import (org.eclipse.californium.core CoapServer CoapResource)
           (org.eclipse.californium.core.coap CoAP$ResponseCode)
           (org.eclipse.californium.core.network CoapEndpoint)
           (java.net InetAddress InetSocketAddress)
           (nightbus.boundaries.coap.server.resources MessagesResource)))

(defn messages-resource
  [{:keys [kafka-producer]}]
  (proxy [CoapResource] ["messages"]
    (handlePOST [exchange]
      (let [payload (nightbus.utils/tap (.getRequestPayload exchange))
            topic (nightbus.utils/tap (.getQueryParameter exchange "topic"))]
        (kafka.producer/produce! kafka-producer {:topic topic :payload payload})
        (.respond exchange CoAP$ResponseCode/CHANGED)))

    #_(getChild [resource-name]
      (println resource-name)
      (proxy-super getChild resource-name))))

(defn start!
  [{coap-server-config :coap-server
    kafka-producer-config :kafka-broker}]
  (let [components {:kafka-producer (components/kafka-producer kafka-producer-config)}
        inet-address (InetAddress/getByName (:host coap-server-config))
        inet-socket-address (InetSocketAddress. inet-address (:port coap-server-config))
        coap-server (CoapServer.)
        coap-endpoint (CoapEndpoint. inet-socket-address)]
    ; (.add coap-server (into-array CoapResource [(MessagesResource. "messages")]))
    (.add coap-server (into-array CoapResource [(messages-resource components)]))
    (.addEndpoint coap-server coap-endpoint)
    (.start coap-server)))
