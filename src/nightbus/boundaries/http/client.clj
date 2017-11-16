(ns nightbus.boundaries.http.client
  (:require [nightbus.boundaries.http.client.subscriptions :as subscriptions]
            [nightbus.components :as components]
            [nightbus.instrument :as instrument]
            [nightbus.kafka.consumer :as kafka.consumer]
            [bidi.ring :refer [make-handler]]
            [ring.middleware.json :as json]
            [ring.middleware.reload :as reload]
            [ring.adapter.jetty :as jetty]
            [taoensso.timbre :as log]
            [clj-http.client :as http]))

(def ^:private consumer-config {:group "http-client"})

(def ^:private request-method-fns
  {"POST"   http/post
   "PUT"    http/put
   "DELETE" http/delete})

(defn- make-requests! [{:keys [topic value]}]
  (instrument/log-metric! :http-client-in-message)
  (log/info (str "[HTTP CLIENT] "{::making-requests {:topic topic :value value}}))
  (let [subscriptions (subscriptions/subscribers-of topic)]
    (doseq [{:keys [url method]} subscriptions]
      (let [req-fn (get request-method-fns method)]
        (log/info (str "[HTTP CLIENT] " {::req {:method method :url url :value value}}))
        (req-fn url {:body value
                     :content-type "text/plain"
                     :async? true}
                (fn [response] (log/info {::req-success response}))
                (fn [exception] (log/info {::req-failure exception})))))))

(defn- kafka->http! [kafka-consumer]
  (kafka.consumer/consume! kafka-consumer "test" identity make-requests!))

(defn- subscribe-handler
  [request]
  (-> request
      :body
      subscriptions/subscribe!)
  {:status 201 :body "Subscribed!"})

(def ^:private routes
  ["/"
   {"subscriptions"
    {:post {"" subscribe-handler}}}] )

(def app
  (-> routes
      make-handler
      (json/wrap-json-body {:keywords? true})))

(defn start! [{zk-connect :zk-connect http-client-config :http-client}]
  (let [kafka-consumer (components/kafka-consumer (merge consumer-config zk-connect))
        consumer-fn #(kafka->http! kafka-consumer)
        service-fn #(jetty/run-jetty (reload/wrap-reload #'app) (select-keys http-client-config [:port]))]
    (.start (Thread. consumer-fn))
    (.start (Thread. service-fn))))
