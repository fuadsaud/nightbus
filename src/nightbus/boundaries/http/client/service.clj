(ns nightbus.boundaries.http.client.service
  (:require [bidi.ring :refer [make-handler]]
            [nightbus.boundaries.http.client.subscriptions :as subscriptions]
            [ring.util.response :as response]
            [ring.middleware.json :as json]
            [ring.middleware.reload :as reload]
            [ring.adapter.jetty :as jetty]))

(def from-wire identity)

(defn subscribe-handler
  [request]
  (-> request
      from-wire
      subscriptions/subscribe!)
  (response/response "Subscribed!"))

(def routes
  ["/"
   {"ops/subscriptions"
    {:post {"" subscribe-handler}}}] )

(def app
  (-> routes
      make-handler
      json/wrap-json-response
      (json/wrap-json-body {:keywords? true})))

(defn start! [http-config]
  (jetty/run-jetty (reload/wrap-reload #'app) (select-keys http-config [:port])))
