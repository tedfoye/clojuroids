(ns clojuroids.input
  (:require [cljs.core.async :refer [chan >!]]
            [goog.dom :as gd]
            [goog.events :as ge])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:import [goog.events EventType]))

(def canvas (gd/getElement "roids"))

(defn user-input []
  (let [c (chan)
        f1 (fn [e] (go (>! c [(aget e "keyCode") :key-down])))
        f2 (fn [e] (go (>! c [(aget e "keyCode") :key-up])))]
    (ge/listen canvas EventType.KEYDOWN f1)
    (ge/listen canvas EventType.KEYUP f2)
    c))


