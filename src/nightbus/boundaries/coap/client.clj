(ns nightbus.boundaries.coap.client
  (:require [nightbus.boundaries.coap.client.subscriptions :as subscriptions]
            [nightbus.components :as components]
            [nightbus.instrument :as instrument]
            [nightbus.kafka.consumer :as kafka.consumer]
            [bidi.ring :refer [make-handler]]
            [taoensso.timbre :as log]
            [clojure.data.json :as json])
  (:import (org.eclipse.californium.core CoapServer CoapResource CoapClient CoapHandler CoapResponse)
           (org.eclipse.californium.core.coap CoAP$ResponseCode MediaTypeRegistry)
           (org.eclipse.californium.core.network CoapEndpoint)
           (java.net InetAddress InetSocketAddress)))

(def ^:private consumer-config {:group "coap-client"})

(def ^:private coap-response-handler
  (proxy [CoapHandler] []
    (onLoad [response]
      (let [body (.getResponseText response)
            status-code (.getCode response)]
        (log/info {::on-load {:status status-code :body body}})))

    (onError []
      (log/info {::on-error "CoAP request failed"}))))

(def ^:private request-method-fns
  {"POST"   #(.post %1 coap-response-handler %2 %3)
   "PUT"    #(.put %1 coap-response-handler %2 %3)})

(defn- make-requests! [{:keys [topic value]}]
  (instrument/log-metric! :coap-client-in-message)
  (log/debug (type value))
  (log/info (str "[COAP CLIENT] "{::making-requests {:topic topic :value value}}))
  (let [subscriptions (subscriptions/subscribers-of topic)]
    (doseq [{:keys [url method]} subscriptions]
      (let [coap-client (CoapClient. url)
            req-fn (get request-method-fns method (get request-method-fns "POST"))]
        (log/info {::req {:method method :url url :value value}})
        (req-fn coap-client value MediaTypeRegistry/UNDEFINED)))))

(defn- kafka->coap! [kafka-consumer]
  (kafka.consumer/consume! kafka-consumer "test" identity make-requests!))

(defn make-subscriptions-resource
  [components]
  (proxy [CoapResource] ["subscriptions"]
    (handlePOST [exchange]
      (let [subscription (-> exchange .getRequestPayload slurp nightbus.utils/tap (json/read-str :key-fn keyword))]
        (log/info {:post-subscription {:subscription subscription}})
        (subscriptions/subscribe! subscription)
        (.respond exchange CoAP$ResponseCode/CREATED)))))

(defn- start-server!
  [{:keys [host port]}]
  (let [inet-address (InetAddress/getByName host)
        inet-socket-address (InetSocketAddress. inet-address port)
        coap-server (CoapServer.)
        coap-endpoint (CoapEndpoint. inet-socket-address)
        subscriptions-resource (make-subscriptions-resource {})]
    (log/info ::listening {:host host :port port})
    (.add coap-server (into-array CoapResource [subscriptions-resource]))
    (.addEndpoint coap-server coap-endpoint)
    (.start coap-server)))

(defn start! [{zk-connect :zk-connect coap-client-config :coap-client}]
  (let [kafka-consumer (components/kafka-consumer (merge consumer-config zk-connect))
        consumer-fn #(kafka->coap! kafka-consumer)
        service-fn #(start-server! coap-client-config)]
    (.start (Thread. consumer-fn))
    (.start (Thread. service-fn))))
