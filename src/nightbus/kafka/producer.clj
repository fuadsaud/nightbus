(ns nightbus.kafka.producer
  (:require [clj-kafka.producer :as producer]
            [taoensso.timbre :as log]
            [clojure.data.json :as json]))

(defn to-wire [payload] (.getBytes (json/write-str payload)))

(defn produce! [producer {:keys [topic payload]}]
  (nightbus.utils/tap {::producing {:topic topic :payload payload}})
  (->> payload
       (producer/message topic)
       (producer/send-message producer)))
