(ns clojuroids.explode
  (:require [clojuroids.util :as u]))

(def color-1 "#aaaaaa")
(def color-2 "#808080")
(def color-change-ttl 2)
(def default-ttl 5)
(def rand-ttl 10)
(def angle-spread 50)
(def rand-rot 5)

(defn create [obj model ttl]
  (let [{:keys [angle x y rot vel]} obj
        expl {:model model 
              :x x
              :y y 
              :vel vel 
              :angle (+ angle (u/roid-rnd angle-spread)) 
              :rot (+ rot (u/roid-rnd rand-rot))
              :ttl (+ ttl (rand-int rand-ttl))}]
    (u/model-to-points expl)))

(defn color [{ttl :ttl :as obj}]
  (let [c (if (< ttl color-change-ttl)
            color-2
            color-1)]
    (assoc obj :color c)))

(defn dec-ttl [{ttl :ttl :as obj}]
  (assoc obj :ttl (dec ttl)))

(defn alive? [{ttl :ttl}] (> ttl 0))

(def xform-2d (comp (map dec-ttl)
                 (filter alive?)
                 (map color)
                 (map u/translate)
                 (map u/model-to-points)))

(defn update [state]
  (let [explosions (get-in state [:objects :explosions])
        explosions (sequence xform-2d explosions)]
    (assoc-in state [:objects :explosions] explosions)))

(defn points [obj]
  (let [model (:model obj)]
    (partition 2 1 (concat model [(first model)]))))

(defn create-explosion
  ([state objs]
     (create-explosion state objs default-ttl))
  ([state objs ttl]
     (if (seq objs)
       (let [obj (first objs)
             explosions (get-in state [:objects :explosions])
             xform (map (fn [p] (create obj p ttl)))
             data (points obj)
             explosions (concat explosions (sequence xform data))]
         (assoc-in state [:objects :explosions] explosions))
       state)))


