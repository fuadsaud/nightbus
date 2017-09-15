(defproject nightbus "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha16"]

                 ; MQTT
                 [clojurewerkz/machine_head "1.0.0"]

                 ; HTTP
                 [bidi "2.1.2"]
                 [ring/ring-core "1.5.0"]
                 [ring/ring-jetty-adapter "1.5.0"]
                 [ring/ring-json "0.4.0"]
                 [ring/ring-devel "1.6.2"]

                 ; Misc
                 [org.clojure/tools.logging "0.4.0"]
                 [org.clojure/data.json "0.2.6"]]
  :main nightbus.core)
