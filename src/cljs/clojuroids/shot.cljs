(ns clojuroids.shot
  (:require [clojuroids.util :as u]))

(def max-shots 2)
(def f-key 70)
(def shot-velocity 20)
(def offset-to-tip-of-ship 12)
(def time-to-live 30)
(def ttl-change 20)
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

(def xform (comp (map #(assoc % :ttl (dec (:ttl %))))
                 (map #(assoc % :color (if (> (:ttl %) ttl-change) color-1 color-2 )))
                 (filter #(> (:ttl %) 0))
                 (map u/translate)
                 (map u/model-to-points)))

(defn update [shots] (sequence xform shots))

(defn handle-input [shots ship input]
  (if (and (= input [f-key :key-down]) (< (count shots) max-shots))
    (concat shots (create (first ship)))
    shots))


