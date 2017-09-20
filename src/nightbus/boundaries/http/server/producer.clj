(ns nightbus.boundaries.http.server.producer
  (:require [clj-kafka.producer :as producer]
            [taoensso.timbre :as log]))

(defn to-wire [payload] (.getBytes (str payload)))

(defn produce! [producer {:keys [topic payload]}]
  (log/debug topic)
  (log/debug payload)
  (->> payload
       to-wire
       (producer/message topic)
       (producer/send-message producer)))
