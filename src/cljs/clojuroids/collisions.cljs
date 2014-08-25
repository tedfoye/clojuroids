(ns clojuroids.collisions
  (:require
   [clojure.set :as set]
   [clojuroids.shot :as shot]
   [clojuroids.roid :as roid]
   [clojuroids.flames :as flames]
   [clojuroids.explode :as explode]
   [clojuroids.render :as render]))

(defn collision [shot roid]
  (if (seq shot)
    (let [{:keys [x y] [w h hw hh] :rect} roid
          [l r] [(- x hw) (+ x hw)]
          [t b] [(- y hh) (+ y hh)]
          [x1 y1] [(:x shot) (:y shot)]]
      (if (and (> x1 l) (< x1 r) (> y1 t) (< y1 b))
        [shot roid]))))

(defn detect-collisions [shots roids]
  (loop [shots shots shots-non-hit [] roids-hit []]
    (if (seq shots)
      (let [[shot roid] (some #(collision (first shots) %) roids)
            shots-non-hit (if (seq shot)
                            shots-non-hit
                            (conj shots-non-hit (first shots)))
            roids-hit (if (seq roid)
                        (conj roids-hit roid)
                        roids-hit)]
        (recur (rest shots) shots-non-hit roids-hit))
      [shots-non-hit roids-hit])))

(defn shot-roid [shots roids flames explosions]
  (let [[shots roids-hit] (detect-collisions shots roids)
        flames (if (seq roids-hit)
                 (flames/create-flames roids-hit flames)
                 flames)
        roids (if (seq roids-hit)
                (remove #(= (first roids-hit) %) roids)
                roids)
        roids (if (seq roids-hit)
                (concat roids (roid/break-roid (first roids-hit)))
                roids)
        explosions (if (seq roids-hit)
                     (explode/create-explosion (first roids-hit) explosions)
                     explosions)]
    [shots roids flames explosions]))


