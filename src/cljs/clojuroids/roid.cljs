(ns clojuroids.roid
  (:require
   [clojuroids.util :refer [degree-mask degree-max cos sin width height]]))

(defn create [sides size posx posy velocity direction rot]
  {:points (for [i (range sides)]
              [(-> i (* degree-max) (/ sides) (+ (rand-int 30)))
               (+ size (rand-int (int (/ size 2))))])
   :posx posx
   :posy posy
   :vel [(* velocity (cos direction)) (* velocity (sin direction))] 
   :dir direction
   :angle 0
   :rot rot})

(defn translate [roid]
  (let [{:keys [posx posy angle rot] [velx vely] :vel} roid
        x (- (mod (+ posx 50 velx) (+ width 100)) 50) 
        y (- (mod (+ posy 50 vely) (+ height 100)) 50)
        rot (mod (+  angle rot) degree-mask)]
    (assoc roid :posx x :posy y :angle rot)))

(defn update[roids]
  (map translate roids))

(defn rotation [min max]
  (let [r1 (range (bit-not (dec max)) (bit-not (dec min)))
        r2 (range min (inc max))]
    (rand-nth (into r1 r2))))

(defn create-roids []
  [(create 9 50 (rand-int width) (rand-int height) (+ 1 (rand-int 3)) (rand-int 512) (rotation 1 2))
   (create 9 50 (rand-int width) (rand-int height) (+ 1 (rand-int 3)) (rand-int 512) (rotation 1 2))
   (create 9 50 (rand-int width) (rand-int height) (+ 1 (rand-int 3)) (rand-int 512) (rotation 1 2))
   (create 9 50 (rand-int width) (rand-int height) (+ 1 (rand-int 3)) (rand-int 512) (rotation 1 2))
   
   (create 7 20 (rand-int width) (rand-int height) (+ 3 (rand-int 3)) (rand-int 512) (rotation 3 4))
   (create 7 20 (rand-int width) (rand-int height) (+ 3 (rand-int 3)) (rand-int 512) (rotation 3 4))
   (create 7 20 (rand-int width) (rand-int height) (+ 3 (rand-int 3)) (rand-int 512) (rotation 3 4))
   (create 7 20 (rand-int width) (rand-int height) (+ 3 (rand-int 3)) (rand-int 512) (rotation 3 4))
 
   (create 5 6 (rand-int width) (rand-int height) (+ 5 (rand-int 3)) (rand-int 512) (rotation 11 15))
   (create 5 6 (rand-int width) (rand-int height) (+ 5 (rand-int 3)) (rand-int 512) (rotation 11 15))
   (create 5 6 (rand-int width) (rand-int height) (+ 5 (rand-int 3)) (rand-int 512) (rotation 11 15))
   (create 5 6 (rand-int width) (rand-int height) (+ 5 (rand-int 3)) (rand-int 512) (rotation 11 15))])

     
