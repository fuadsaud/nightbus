(ns nightbus.boundaries.http.server
  (:require [bidi.ring :refer [make-handler]]
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
  ["/topics"
   {"/"
    {:post {"" subscribe-handler}}}] )

(def app
  (-> routes
      make-handler
      json/wrap-json-response
      (json/wrap-json-body {:keywords? true})))

(defn start! [{:keys [server]}]
  (jetty/run-jetty (reload/wrap-reload #'app) (select-keys server [:port])))
