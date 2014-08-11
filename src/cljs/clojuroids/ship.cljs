(ns clojuroids.ship
  (:require [clojuroids.util :refer [degree-mask width height cos sin]]))

(def max-forward 10)
(def max-reverse -10)


(defn create []
  {:type :ship
   :points [[0 12] [192 12] [256 3] [320 12]]
   :posx (/ width 2) 
   :posy (/ height 2)
   :vel [0 0]
   :angle 128 
   :rot 0
   :thrust 0
   })

(defn update-velocity [ship]
  (let [{:keys [vel angle thrust]} ship
        x (+ (vel 0) (* thrust (cos angle))) 
        y (+ (vel 1) (* thrust (sin angle)))
        x (min x max-forward)
        x (max x max-reverse)
        y (min y max-forward)
        y (max y max-reverse)] 
    (assoc ship :vel [x y])))

(defn update [ship]
  (let [ship (update-velocity ship)
        {:keys [posx posy angle rot] [velx vely] :vel} ship
        x (- (mod (+ posx 50 velx) (+ width 100)) 50) 
        y (- (mod (+ posy 50 vely) (+ height 100)) 50)
        rot (mod (+  angle rot) degree-mask)]
    (assoc ship :posx x :posy y :angle rot)))

(defn handle-input [ship input]
  (condp = input 
    ;[70 :key-down] (create-shot ship)
    [74 :key-down] (assoc ship :rot 10)
    [74 :key-up]   (assoc ship :rot 0)
    [76 :key-down] (assoc ship :rot -10)
    [76 :key-up]   (assoc ship :rot 0)
    [73 :key-down] (assoc ship :thrust 0.5) 
    [73 :key-up]   (assoc ship :thrust 0) 
    [75 :key-down] (assoc ship :thrust -0.5) 
    [75 :key-up]   (assoc ship :thrust 0) 
    ship))

