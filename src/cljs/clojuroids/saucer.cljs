(ns clojuroids.saucer
  (:require [clojuroids.util :as u]))

(def max-shots 1)
(def velocity 4)
(def model [[256 20] [0 20] [448 8] [320 8] [256 20]
            [192 10] [64 10] [0 20]
            [64 10] [112 13] [144 13] [192 10]])

(defn create [x y angle]
  (let [cos-angle (u/cos angle) sin-angle (u/sin angle)
        shot {:model model
              :rect (u/rect model)
              :x x 
              :y y 
              :vel [(* velocity cos-angle) (* velocity sin-angle)]
              :angle angle
              :rot 0}]
    [(u/model-to-points shot)]))

(defn off-screen? [{x :x y :y}]
  (or (< x 0) (> x u/width)
      (< y 0) (> y u/height)))

(def xform (comp (remove off-screen?)
            (map u/translate)
            (map u/model-to-points)))

(defn update [state]
  (let [saucer (get-in state [:objects :saucer])
        saucer (sequence xform saucer)]
    (assoc-in state [:objects :saucer] saucer)))
