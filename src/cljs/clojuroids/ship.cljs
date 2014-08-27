(ns clojuroids.ship
  (:require
   [clojuroids.util :as u]
   [clojuroids.flames :as flames]
   [clojuroids.explode :as explode]))

(def max-forward 10)
(def max-reverse -10)
(def model [[0 12] [192 12] [256 3] [320 12] [0 12]])

(defn create []
  (let [ship {:model model 
             :rect (u/rect model)
             :x (/ u/width 2) 
             :y (/ u/height 2)
             :vel [0 0]
             :angle 128 
             :rot 0
             :thrust 0}]
   [ship]))

(defn update-velocity [ship]
  (let [{:keys [vel angle thrust]} ship
        x (+ (vel 0) (* thrust (u/cos angle))) 
        y (+ (vel 1) (* thrust (u/sin angle)))
        x (min x max-forward)
        x (max x max-reverse)
        y (min y max-forward)
        y (max y max-reverse)] 
    (assoc ship :vel [x y])))

(def update-xform (comp (map update-velocity)
                        (map u/translate)
                        (map u/model-to-points)))

(defn update [ship] (sequence update-xform ship))

(defn flames [ship]
  (if (not= 0 (:thrust (first ship)))
    (flames/create-ship-flames ship)))

(defn control [input]
  (condp = input 
    [74 :key-down] (fn [i](assoc i :rot 10)) 
    [74 :key-up]   (fn [i](assoc i :rot 0)) 
    [76 :key-down] (fn [i](assoc i :rot -10)) 
    [76 :key-up]   (fn [i](assoc i :rot 0)) 
    [73 :key-down] (fn [i](assoc i :thrust 0.5)) 
    [73 :key-up]   (fn [i](assoc i :thrust 0)) 
    [75 :key-down] (fn [i](assoc i :thrust -0.5))
    [75 :key-up]   (fn [i](assoc i :thrust 0))
    (fn [i] i)))
