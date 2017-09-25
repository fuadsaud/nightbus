(ns nightbus.boundaries.http.server
  (:require [nightbus.boundaries.http.server.producer :as producer]
            [nightbus.components :as components]
            [bidi.ring :refer [make-handler]]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as json]
            [ring.middleware.reload :as reload]
            [ring.logger.timbre :as logger.timbre]
            [ring.util.response :as response]
            [taoensso.timbre :as log]))

(def from-wire identity)

(defn produce-message-handler
  [{:keys [kafka-producer]} request]
  (->> request
       :body
       from-wire
       (producer/produce! kafka-producer))
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

(defn start! [{:keys [http-server kafka-producer]}]
  (let [components {:kafka-producer (components/kafka-producer kafka-producer)}]
    (jetty/run-jetty (app components) (select-keys http-server [:port]))))
