(ns clojuroids.ship
  (:require
   [clojuroids.util :as u]
   [clojuroids.flames :as flames]
   [clojuroids.explode :as explode]))

(def max-forward 10)
(def max-reverse -10)

(defn create []
  (let [model [[0 12] [192 12] [256 3] [320 12] [0 12]]
        ship {:model model 
              :rect (u/rect model)
              :x (/ u/width 2) 
              :y (/ u/height 2)
              :vel [0 0]
              :angle 128 
              :rot 0
              :thrust 0}]
   [(u/model-to-points ship)]))

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
    (flames/create-ship-flames (first ship))))

(defn handle-input [ship input]
  (condp = input 
    [74 :key-down] (sequence (map #(assoc % :rot 10)) ship) 
    [74 :key-up]   (sequence (map #(assoc % :rot 0)) ship)
    [76 :key-down] (sequence (map #(assoc % :rot -10)) ship)
    [76 :key-up]   (sequence (map #(assoc % :rot 0)) ship)
    [73 :key-down] (sequence (map #(assoc % :thrust 0.5)) ship) 
    [73 :key-up]   (sequence (map #(assoc % :thrust 0)) ship)
    [75 :key-down] (sequence (map #(assoc % :thrust -0.5)) ship)
    [75 :key-up]   (sequence (map #(assoc % :thrust 0)) ship)
    ship))

