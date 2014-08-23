(ns clojuroids.roid
  (:require [clojuroids.util :as u]))

(def size-lg 40)
(def size-md 20)
(def size-sm 6)

(def lg-roid {:sides 9 :size size-lg :vel 2 :rot 2})
(def md-roid {:sides 7 :size size-md :vel 5 :rot 8})
(def sm-roid {:sides 5 :size size-sm :vel 8 :rot 15})

(defn make-model [sides size]
  (for [i (range sides)]
    [(-> i (* u/degree-max) (/ sides))
     (+ size (rand-int size))]))

(defn roid-seq [xfn yfn params]
  (let [{:keys [sides size vel rot]} params
        angle (rand-int u/degree-max)
        model (make-model sides size)]
    (cons {:model model 
           :rect (u/rect model) 
           :size size
           :x (xfn) 
           :y (yfn) 
           :vel (u/velocity vel angle) 
           :angle angle 
           :rot (u/roid-rnd rot)}
          (lazy-seq (roid-seq xfn yfn params)))))

(def update-xform (comp (map u/translate) (map u/model-to-points)))

(defn update [roids] (sequence update-xform roids))

(defn break-roid [roid]
  (let [{:keys [size posx posy]} roid]
    (condp = size
      size-lg (take (inc (rand-int 3)) (roid-seq (fn [] posx) (fn [] posy) md-roid))
      size-md (take (rand-int 3) (roid-seq  (fn [] posx) (fn [] posy) sm-roid))
      [])))

(def create-xform (comp (take 4) (map u/model-to-points)))

(defn create-roids []
  (sequence create-xform (roid-seq #(rand-int u/width) #(rand-int u/height) lg-roid)))

