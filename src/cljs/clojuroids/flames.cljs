(ns clojuroids.flames
  (:require [clojuroids.util :refer [degree-mask degree-max cos sin width height]]))

(defn create [obj angle ttl]
  (let [{:keys [posx posy]} obj]
    {:points [[0 1] [128 1] [256 0] [384 0]]
     :posx posx
     :posy posy 
     :vel [(* (+ 5 (- (rand-int 2) 2)) (cos angle)) (* (+ 5 (- (rand-int 2) 2)) (sin angle))] 
     :angle (mod angle degree-mask)
     :rot 0
     :ttl (+ ttl (rand-int 5))
     :color "#ffff99"}))

(defn translate [flame]
  (let [{:keys [posx posy] [velx vely] :vel} flame
        x (- (mod (+ posx 50 velx) (+ width 100)) 50) 
        y (- (mod (+ posy 50 vely) (+ height 100)) 50)]
    (assoc flame :posx x :posy y)))

(def xform (comp (map #(assoc % :ttl (dec (:ttl %))))
                 (map #(assoc % :color (if (< (:ttl %) 3) "#DC143C" "#ffff99")))
                 (filter #(> (:ttl %) 0))
                 (map translate)))

(defn update [flames]
  (sequence xform flames))

(defn create-flames [obj flames]
  (let [new-flames (for [i (range 6)] (create obj (rand-int degree-max) 5))]
    (concat flames new-flames)))

(defn create-ship-flames [ship flames]
(let [new-flames (for [i (range 3)] (create ship (+ (:angle ship) 256 (- (rand-int 100) 50)) 2))]
    (concat flames new-flames)))
