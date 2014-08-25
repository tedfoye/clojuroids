(ns clojuroids.explode
  (:require [clojuroids.util :as u]))

(defn create [{:keys [angle x y rot vel] [vx vy] :vel} model ttl]
  (let [obj {:model model 
             :x x
             :y y 
             :vel vel 
             :angle (+ angle (u/roid-rnd 50)) 
             :rot (+ rot (u/roid-rnd 5))
             :ttl (+ ttl (rand-int 10))}]
    (u/model-to-points obj)))

(def xform (comp (map #(assoc % :ttl (dec (:ttl %))))
                 (filter #(> (:ttl %) 0))
                 (map #(assoc % :color (if (< (:ttl %) 2) "#808080" "#aaaaaa")))
                 (map u/translate)
                 (map u/model-to-points)))

(defn update [explosions] (sequence xform explosions))

(defn create-explosion [obj explosions]
  (let [model (:model obj)
        pts (partition 2 1 (concat model [(first model)]))
        new-explosions (map #(create obj %1 5) pts)]
    (concat explosions new-explosions)))


