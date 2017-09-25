(ns nightbus.utils
  (:require [taoensso.timbre :as log]
            [puget.printer :as puget]))

(defn tap [x]
  (doto x puget/cprint))
