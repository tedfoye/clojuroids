(ns clojuroids.explode
  (:require [clojuroids.util :as u]))

(def color-1 "#aaaaaa")
(def color-2 "#808080")
(def default-ttl 5)
(def color-change-ttl 2)

(defn create [obj model ttl]
  (let [{:keys [angle x y rot vel]} obj
        expl {:model model 
              :x x
              :y y 
              :vel vel 
              :angle (+ angle (u/roid-rnd 50)) 
              :rot (+ rot (u/roid-rnd 5))
              :ttl (+ ttl (rand-int 10))}]
    (u/model-to-points expl)))

(defn color [{ttl :ttl :as obj}]
  (let [c (if (< ttl color-change-ttl)
            color-2
            color-1)]
    (assoc obj :color c)))

(defn dec-ttl [{ttl :ttl :as obj}]
  (assoc obj :ttl (dec ttl)))

(defn alive? [{ttl :ttl}]
  (> ttl 0))

(def xform (comp (map dec-ttl)
                 (filter alive?)
                 (map color)
                 (map u/translate)
                 (map u/model-to-points)))

(defn update [explosions] (sequence xform explosions))

(defn points [obj]
  (let [model (:model obj)]
    (partition 2 1 (concat model [(first model)]))))

(defn create-explosion
  ([obj]     (create-explosion obj default-ttl))
  ([obj ttl] (sequence (map #(create obj % ttl)) (points obj))))


