(ns clojuroids.ship
  (:require [clojuroids.util :refer [degree-mask width height cos sin]]))

(def max-forward 10)
(def max-reverse -10)


(defn create-ship [posx posy vel angle rot]
  {:type :ship
   :points [[0 12] [192 12] [256 3] [320 12]]
   :posx posx
   :posy posy
   :vel [(* vel (cos angle)) (* vel (sin angle))]
   :angle angle 
   :rot rot
   :thrust 0
   })

(defn create-shot [ship]
  (let [posx (:posx ship)
        posy (:posy ship)
        vel 20 
        angle (:angle ship)]
    {:points [[0 1] [128 1] [256 1] [384 1]]
     :posx (+ posx (* 12 (cos angle)))
     :posy (+ posy (* 12 (sin angle)))
     :vel [(* vel (cos angle)) (* vel (sin angle))]
     :angle angle
     :rot 0}))

(defn update-velocity [ship]
  (let [[vel-x vel-y] (:vel ship)
        angle (:angle ship)
        thrust (:thrust ship)
        [x y] [(+ vel-x (* thrust (cos angle))) (+ vel-y (* thrust (sin angle)))]
        x (min x max-forward)
        x (max x max-reverse)
        y (min y max-forward)
        y (max y max-reverse)] 
    (assoc ship :vel [x y])))

(defn handle-input [ship input]
  (condp = input 
   [70 :key-down] (create-shot ship)
    [74 :key-down] (assoc ship :rot 10)
    [74 :key-up]   (assoc ship :rot 0)
    [76 :key-down] (assoc ship :rot -10)
    [76 :key-up]   (assoc ship :rot 0)
    [73 :key-down] (assoc ship :thrust 0.5) 
    [73 :key-up]   (assoc ship :thrust 0) 
    [75 :key-down] (assoc ship :thrust -0.5) 
    [75 :key-up]   (assoc ship :thrust 0) 
    ship))

