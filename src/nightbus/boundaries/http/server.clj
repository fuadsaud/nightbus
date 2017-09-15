(ns nightbus.boundaries.http.server
  (:require [bidi.ring :refer [make-handler]]
            [ring.util.response :as response]
            [ring.middleware.json :as json]
            [ring.middleware.reload :as reload]
            [ring.adapter.jetty :as jetty]))

(def from-wire identity)

(defn produce-message-handler
  [request]
  (-> request
      from-wire
      )
  (response/response {:status 202 :body "Produced!"}))

(def routes
  ["/"
   {"messages"
    {:post {"" produce-message-handler}}}] )

(def app
  (-> routes
      make-handler
      json/wrap-json-response
      (json/wrap-json-body {:keywords? true})))

(defn start! [{:keys [server]}]
  (jetty/run-jetty (reload/wrap-reload #'app) (select-keys server [:port])))
