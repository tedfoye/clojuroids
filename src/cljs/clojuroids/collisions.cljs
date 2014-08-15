(ns clojuroids.collisions
  (:require
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
  (and (< (get rect 0) x)
       (> (get rect 1) x)
       (< (get rect 2) y)
       (> (get rect 3) y)))

(defn collision? [obj1 obj2]
  (let [x (:posx obj1)
        y (- height (:posy obj1))
        rect (make-rect obj2)]
    (in-rect x y rect)))

(defn winnow [pred coll]
  (let [pvs (map #(vector (pred %) %) coll)]
    [(for [[p v] pvs :when p] v)
     (for [[p v] pvs :when (not p)] v)]))

(defn shot-roid [shots roids]
  (let [[hit-roids roids] (winnow (fn [roid] (some (fn [shot] (collision? shot roid)) shots)) roids)]
    [shots roids]))
