(ns clojuroids.explode
  (:require [clojuroids.util :refer [degree-mask degree-max cos sin width height]]))

(defn create [{:keys [angle posx posy] [velx vely] :vel} points pt-angle ttl]
  (let [rvel (inc (rand-int 3))
        rangle (rand-int degree-max)
        [[x1 y1] [x2 y2]] points
        xlen (- x2 x1)
        ylen (- y2 y1)
        ]
    {:points [[(- x1 xlen) (- y1 ylen)] [(- x2 xlen) (- y2 ylen)]]
     :posx posx
     :posy posy 
     :vel [(* 1 (cos (+ pt-angle (- (rand-int 100) 50))))
           (* 1 (sin pt-angle))] 
     
                                        ; :vel [(* (inc (rand-int 3)) (cos (rand-int degree-max))) (* (inc (rand-int 3)) (sin (rand-int degree-max)))]
     :angle angle 
     :rot (- (rand-int 4) 2) 
     :ttl (+ ttl (rand-int 5))
     :color "#aaaaaa"}))

(defn translate [explosion]
  (let [{:keys [posx posy angle rot] [velx vely] :vel} explosion
        x (- (mod (+ posx 50 velx) (+ width 100)) 50) 
        y (- (mod (+ posy 50 vely) (+ height 100)) 50)
        angle (mod (+ angle rot) degree-mask)]
    (assoc explosion :posx x :posy y :angle angle)))

(def xform (comp (map #(assoc % :ttl (dec (:ttl %))))
                 (filter #(> (:ttl %) 0))
                 (map #(assoc % :color (if (< (:ttl %) 3) "#808080" "#aaaaaa")))
                 (map translate)))

(defn update [explosions]
  (sequence xform explosions))

(defn create-explosion [obj explosions]
  (let [pts (partition 2 1 (:points obj))
        angles (range 0 512 (/ 512 (count pts)))
        new-explosions (map #(create obj %1 %2 20) pts angles)]
    (concat explosions new-explosions)))


