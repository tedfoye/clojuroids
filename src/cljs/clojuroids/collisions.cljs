(ns clojuroids.collisions
  (:require
   [clojuroids.shot :as shot]
   [clojuroids.roid :as roid]
   [clojuroids.flames :as flames]
   [clojuroids.explode :as explode]))

(defn pt-in-rect [obj roid]
  (if (and (seq obj) (seq roid))
    (let [{:keys [x y] [_ _ hw hh] :rect} roid
          [l r] [(- x hw) (+ x hw)]
          [t b] [(+ y hh) (- y hh)]
          [x1 y1] [(:x obj) (:y obj)]]
      (if (and (< l x1 r) (< b y1 t))
        [obj roid]))))

(defn collisions [objs roids]
  (loop [hits [] objs objs]
    (if (not (seq objs))
      hits
      (recur (concat hits
                     (some #(pt-in-rect (first objs) %) roids))
             (rest objs)))))

(defn shot-roid [{:keys [shots roids flames explosions]}]
  (let [hits (collisions shots roids)
        shots-hit (sequence (take-nth 2) hits)
        roids-hit (sequence (take-nth 2) (rest hits))
        shots (sequence (remove #(= % (first shots-hit))) shots)
        flames (concat flames (flames/create-flames (first shots-hit))) 
        roids (sequence (remove #(= % (first roids-hit))) roids)
        roids (concat roids (roid/break-roid (first roids-hit)))
        explosions (concat explosions (explode/create-explosion (first roids-hit)))]
    {:shots shots :roids roids :flames flames :explosions explosions}))

(defn ship-roid [{:keys [ship roids flames explosions]}]
  (let [ship-collisions (collisions ship roids)
        ship-hits (sequence (take-nth 2) ship-collisions)
        flames (concat flames (flames/create-flames (first ship-hits)))
        explosions (concat explosions (explode/create-explosion (first ship-hits) 20))
        ship (if (seq ship-hits) nil ship)]
    {:ship ship :roids roids :flames flames :explosions explosions}))

