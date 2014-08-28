(ns clojuroids.collisions
  (:require
   [clojuroids.shot :as shot]
   [clojuroids.roid :as roid]
   [clojuroids.flames :as flames]
   [clojuroids.explode :as explode]))

(defn collision [obj roid]
  (if (and (seq obj) (seq roid))
    (let [{:keys [x y] [_ _ hw hh] :rect} roid
          [l r] [(- x hw) (+ x hw)]
          [t b] [(- y hh) (+ y hh)]
          [x1 y1] [(:x obj) (:y obj)]]
      (if (= 0 (:x roid))
        (.log js/console roid))
      (if (and (< l x1 r) (< t y1 b))
        [obj roid]))))

(defn collisions [obj roids]
  (some #(collision (first obj) %) roids))

(defn shot-roid [ship shots roids flames explosions]
  (let [[shot roid] (collisions shots roids)
        shots (sequence (remove #(= % shot)) shots)
        flames (concat flames (flames/create-flames shot)) 
        roids (sequence (remove #(= % roid)) roids)
        roids (concat roids (roid/break-roid roid))
        explosions (concat explosions (explode/create-explosion roid))

        ;[ship-hit roid] (collisions ship roids)
        ;explosions (concat explosions (explode/create-explosion ship-hit 20))
        ;ship (if (seq ship-hit) nil ship)
        ] 
    [ship shots roids flames explosions]))

