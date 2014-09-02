(ns clojuroids.saucer
  (:require [clojuroids.util :as u]))

(def max-shots 4)
(def velocity 2)
(def offset-to-tip-of-ship 12)
(def time-to-live 30)
(def color-change-ttl 20)
(def color-1 "#ffffff")
(def color-2 "#4682B4")
(def model [
            [256 20] [0 20] [448 8] [320 8] [256 20]
            [192 10] [64 10] [0 20]
            [64 10] [112 13] [144 13] [192 10]


            ])

(defn create [x y angle]
  (let [cos-angle (u/cos angle) sin-angle (u/sin angle)
        shot {;:model model
              :model (for [[x y] model] [x (/ y 1.5)])
              :rect (u/rect model)
              :x x 
              :y y 
              :vel [(* velocity cos-angle) (* velocity sin-angle)]
              :angle angle
              :rot 0 
              :ttl time-to-live}]
    [(u/model-to-points shot)]))

(defn color [{ttl :ttl :as obj}]
  (let [c (if (< ttl color-change-ttl)
            color-2
            color-1)]
    (assoc obj :color c)))

(defn dec-ttl [{ttl :ttl :as obj}]
  (assoc obj :ttl (dec ttl)))

(defn alive? [{ttl :ttl}] (> ttl 0))


(def xform (comp ;(map dec-ttl)
                 (map color)
                 ;(filter alive?)
                 (map u/translate)
                 (map u/model-to-points)))

(defn update [{saucer :saucer}]
  {:saucer (sequence xform saucer)})

