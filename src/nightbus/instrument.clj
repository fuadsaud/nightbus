(ns nightbus.instrument
  (:require [taoensso.timbre.appenders.core :as appenders]
            [clojure.algo.generic.functor :refer [fmap]]
            [clojure.java.io :as io]))

(def metric->filename
  {:http-server-in-request  (io/file "/Users/fuad/Code/fuadsaud/nightbus/benchmark/http-coap/t1")
   :coap-client-in-message  (io/file "/Users/fuad/Code/fuadsaud/nightbus/benchmark/http-coap/t2")})

(def metric->file (fmap io/file metric->filename))

(def metric->writer (fmap #(io/writer % :append true) metric->file))

(defn log-metric! [metric]
  (let [w (metric metric->writer)]
    (doto w
      (.write (str (System/currentTimeMillis) "\n"))
      (.flush))))

#_(defn log-config [log-type]
    {:appenders {:spit (appenders/spit-appender {:fname (log-type log-type->file)})}
     :output-fn identity})
