(ns roids.ship
  (:require [roids.util :refer [degree-mask width height cos sin]]))

(def forward-thrust 0.5)
(def reverse-thrust 0.5)
(def max-forward 20)
(def max-reverse -20)

(defn create-ship [posx posy vel angle rot]
  {:type :ship
   :points [[0 12] [192 12] [256 3] [320 12]]
   :posx posx
   :posy posy
   :vel [(* vel (cos angle)) (* vel (sin angle))]
   :angle angle 
   :rot rot
   :thrust 0
   :thrust-angle 0
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
        angle (:thrust-angle ship)
        thrust (:thrust ship)
        [x y] [(+ vel-x (* thrust (cos angle))) (+ vel-y (* thrust (sin angle)))]
        x (if (> x max-forward) max-forward x)
        x (if (< x max-reverse) max-reverse x)
        y (if (> y max-forward) max-forward y)
        y (if (< y max-reverse) max-reverse y)]
    (assoc ship :vel [x y])))

(defn update-position [ship]
  (let [ship (update-velocity ship)
        [vel-x vel-y] (:vel ship)
        posx (+ (:posx ship) vel-x)
        posy (+ (:posy ship) vel-y)
        posx (if (< posx -50) (+ width 50) posx)
        posx (if (> posx (+ width 50)) -50 posx)
        posy (if (< posy -50) (+ height 50) posy)
        posy (if (> posy (+ height 50)) -50 posy)
        rot-angle (bit-and (+ (:angle ship) (:rot ship)) degree-mask)]
    (assoc ship :posx posx :posy posy :angle rot-angle)))

(defn rot-ship [ship input rot]
  (condp = (:event input)
    :keydown (assoc ship :rot rot)
    :keyup (assoc ship :rot 0)
    ship))

(defn thrust [ship input vel angle-offset]
  (if (= :keydown (:event input))
    (assoc ship :thrust vel :thrust-angle (+ angle-offset (:angle ship)))
    (assoc ship :thrust 0)))

(defn handle-input [ship input]
  (condp = (:keycode input) 
    70 (create-shot ship)
    74 (rot-ship ship input 10) 
    76 (rot-ship ship input -10)
    73 (thrust ship input forward-thrust 0) 
    75 (thrust ship input reverse-thrust 256) 
    (do (if (not= nil (:event input)) (. js/console (log (:keycode input)))) ship)))

(defn update [ship input]
  (let [ship (update-position ship)
        ship (handle-input ship input)]
    ship))


