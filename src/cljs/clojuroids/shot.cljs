(ns clojuroids.shot
  (:require [clojuroids.util :refer [cos sin width height]]))

(defn create [ship]
  (let [{:keys [posx posy angle]} ship vel 20]
    {:points [[0 2] [128 2] [256 2] [384 2]]
     :posx (+ posx (* 12 (cos angle)))
     :posy (+ posy (* 12 (sin angle)))
     :vel [(* vel (cos angle)) (* vel (sin angle))]
     :angle angle
     :rot 0
     :ttl 40
     :color "#ADD8E6"}))

(defn translate [shot]
  (let [{:keys [posx posy] [velx vely] :vel} shot
        x (- (mod (+ posx 50 velx) (+ width 100)) 50) 
        y (- (mod (+ posy 50 vely) (+ height 100)) 50)]
    (assoc shot :posx x :posy y)))

(def xform (comp (map #(assoc % :ttl (dec (:ttl %))))
                 (map #(assoc % :color (if (< (:ttl %) 20) "#4682B4" "#ADD8E6" )))
                 (filter #(> (:ttl %) 0))
                 (map translate)))

(defn update [shots]
  (sequence xform shots))

(defn handle-input [shots ship input]
  (if (and (= input [70 :key-down]) (< (count shots) 2))
    (concat shots [(create ship)])
    shots))


