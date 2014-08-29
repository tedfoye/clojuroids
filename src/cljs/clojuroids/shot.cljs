(ns clojuroids.shot
  (:require [clojuroids.util :as u]))

(def max-shots 2)
(def f-key 70)
(def shot-velocity 20)
(def offset-to-tip-of-ship 12)
(def time-to-live 40)
(def color-change-ttl 20)
(def color-1 "#ADD8E6")
(def color-2 "#4682B4")

(defn create [ship]
  (let [{:keys [x y angle]} ship
        model [[0 2] [128 2] [256 2] [384 2]]
        shot {:model model
              :rect (u/rect model)
              :x (+ x (* offset-to-tip-of-ship (u/cos angle)))
              :y (+ y (* offset-to-tip-of-ship (u/sin angle)))
              :vel [(* shot-velocity (u/cos angle)) (* shot-velocity (u/sin angle))]
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

(defn alive? [{ttl :ttl}]
  (> ttl 0))


(def xform (comp (map dec-ttl)
                 (map color)
                 (filter alive?)
                 (map u/translate)
                 (map u/model-to-points)))

(defn update [shots] (sequence xform shots))

(defn handle-input [shots ship input]
  (if (and
       (= input [f-key :key-down])
       (< (count shots) max-shots))
    (concat shots (create (first ship)))
    shots))


