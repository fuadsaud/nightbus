(defproject nightbus "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :injections [(require 'nightbus.utils)]
  :dependencies [[org.clojure/clojure "1.9.0-alpha16"]

                 ; MQTT
                 [clojurewerkz/machine_head "1.0.0"]

                 ; HTTP
                 [bidi "2.1.2"]
                 [ring/ring-core "1.5.0"]
                 [ring/ring-jetty-adapter "1.5.0"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-devel "1.6.2"]
                 [clj-http "3.7.0"]

                 ; CoAP
                 [org.eclipse.californium/californium-core "2.0.0-M5"]

                 ; Kafka
                 [clj-kafka "0.3.4"]

                 ; Misc
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/algo.generic "0.1.2"]
                 [mvxcvi/puget "1.0.1"]
                 [commons-io/commons-io "2.5"]

                 [com.taoensso/timbre "4.10.0"]
                 [ring-logger-timbre "0.7.5"]]
  :main nightbus.core)
