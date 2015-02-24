(ns clojuroids.input
  (:require [goog.dom :as gd]
            [goog.events :as ge])
  (:import [goog.events EventType]))

(def canvas (gd/getElement "roids"))

(def state (atom {}))

(defn key-down [e] (swap! state assoc (.-keyCode e) :key-down))

(defn key-up [e] (swap! state assoc (.-keyCode e) :key-up))

(ge/listen canvas EventType.KEYDOWN key-down)
(ge/listen canvas EventType.KEYUP key-up)

