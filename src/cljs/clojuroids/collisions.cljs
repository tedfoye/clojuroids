(ns clojuroids.collisions
  (:require
   [clojuroids.shot :as shot]
   [clojuroids.roid :as roid]
   [clojuroids.flames :as flames]
   [clojuroids.explode :as explode]))

(defn collision [shot roid]
  (if (seq shot)
    (let [{:keys [x y] [w h hw hh] :rect} roid
          [l r] [(- x hw) (+ x hw)]
          [t b] [(- y hh) (+ y hh)]
          [x1 y1] [(:x shot) (:y shot)]]
      (if (and (> x1 l) (< x1 r) (> y1 t) (< y1 b))
        [shot roid]))))

(defn collisions [shots roids]
  (some #(collision (first shots) %) roids))

(defn shot-roid [shots roids flames explosions]
  (let [[shot roid] (collisions shots roids)
        shots (sequence (remove #(= % shot)) shots)
        flames (concat flames (flames/create-flames roid)) 
        roids (sequence (remove #(= % roid)) roids)
        roids (concat roids (roid/break-roid roid))
        explosions (concat explosions (explode/create-explosion roid))] 
    [shots roids flames explosions]))

