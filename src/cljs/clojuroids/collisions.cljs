(ns clojuroids.collisions
  (:require
   [clojure.set :as set]
   [clojuroids.shot :as shot]
   [clojuroids.roid :as roid]
   [clojuroids.render :as render]
   [clojuroids.util :refer [height]]))

(defn make-rect [object]
  (let [points (render/draw-points object)
        left (get (apply min-key first points) 0)
        right (get (apply max-key first points) 0)
        top (get (apply min-key second points) 1)
        bottom (get (apply max-key second points) 1)]
    [left right top bottom]))

(defn in-rect [x y rect]
  (and (< (rect 0) x)
       (> (rect 1) x)
       (< (rect 2) y)
       (> (rect 3) y)))

(defn collision? [shot roid]
  (let [x (:posx shot)
        y (- height (:posy shot))
        rect (make-rect roid)]
    (when (in-rect x y rect)
      shot)))

(defn winnow [pred coll]
  (let [pvs (map #(vector (pred %) %) coll)]
    [(for [[p v] pvs :when p] [p v])
     (for [[p v] pvs :when (not p)] v)]))

(defn shot-roid [shots roids]
  (let [[hits roids] (winnow (fn [roid] (some (fn [shot] (collision? shot roid)) shots)) roids)
        [shots-hit hit-roids] [(for [obj hits] (first obj)) (for [obj hits] (second obj))]
        shots (filter #(not= (first shots-hit) %) shots)
        roids (if-let [hit-roid (first hit-roids)] (concat roids (roid/break-roid hit-roid)) roids)]
    [shots roids]))

