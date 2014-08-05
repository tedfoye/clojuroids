(ns clojuroids.input
  (:require [cljs.core.async :refer [chan >!]]
            [goog.dom :as gd]
            [goog.events :as ge])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn user-input []
  (let [canvas (gd/getElement "roids") c (chan)]
    (ge/listen canvas
               ge/EventType.KEYDOWN
               (fn [e] (go (>! c [(aget e "keyCode") :key-down]))))
    (ge/listen canvas
               ge/EventType.KEYUP
               (fn [e] (go (>! c [(aget e "keyCode") :key-up]))))
    c))
