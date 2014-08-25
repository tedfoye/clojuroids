(ns clojuroids.flames
  (:require [clojuroids.util :as u]))

(def color-1 "#ffff99")
(def color-2 "#DC143C")

(defn create [obj angle ttl]
  (let [{:keys [x y]} obj
        model [[0 1] [128 1] [256 1] [384 1]]
        flame {:model model 
               :rect (u/rect model)
               :x x
               :y y 
               :vel [(* (+ 5 (u/roid-rnd 2)) (u/cos angle))
                     (* (+ 5 (u/roid-rnd 2)) (u/sin angle))] 
               :angle (mod angle u/degree-mask)
               :rot 0
               :ttl (+ ttl (rand-int 5))}]
    (u/model-to-points flame)))

(def xform (comp (map #(assoc % :ttl (dec (:ttl %))))
                 (map #(assoc % :color (if (< (:ttl %) 3) color-2 color-1)))
                 (filter #(> (:ttl %) 0))
                 (map u/translate)
                 (map u/model-to-points)))

(defn update [flames] (sequence xform flames))

(defn create-flames [obj flames]
  (let [new-flames (for [i (range 6)]
                     (create (first obj) (rand-int u/degree-max) 10))]
    (concat flames new-flames)))

(defn create-ship-flames [ship flames]
  (let [new-flames (for [i (range 3)]
                     (create ship (+ (:angle ship) 256 (u/roid-rnd 50)) 2))]
    (concat flames new-flames)))
