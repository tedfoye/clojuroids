(ns clojuroids.flames
  (:require [clojuroids.util :as u]))

(def color-1 "#ffff99")
(def color-2 "#DC143C")
(def flame-count 6)
(def ship-flame-count 3)
(def flame-spread 50)
(def default-ttl 10)
(def ship-ttl 2)
(def ttl-rnd 5)
(def color-change-ttl 3)
(def opposite-dir (/ u/degree-max 2))
(def model [[0 1] [128 1] [256 1] [384 1]])
(def velocity 5)
(def velocity-rnd 2)

(defn angle-fn [] (rand-int u/degree-max))

(defn ship-angle-fn [ship]
  (fn []
    (+ (:angle ship)
       opposite-dir
       (u/roid-rnd flame-spread))))

(defn velocity-2d [angle-fn]
  (let [angle (mod (angle-fn) u/degree-mask)
        x (+ velocity (u/roid-rnd velocity-rnd))
        y (+ velocity (u/roid-rnd velocity-rnd))
        vx (* x (u/cos (angle-fn)))
        vy (* y (u/sin (angle-fn)))]
    [vx vy]))

(defn create [obj angle-fn ttl]
  (lazy-seq
   (let [{:keys [x y]} obj
         flame {:model model 
                :rect (u/rect model)
                :x x
                :y y 
                :vel (velocity-2d angle-fn) 
                :angle (mod (angle-fn) u/degree-mask)
                :rot 0
                :ttl (+ ttl (rand-int ttl-rnd))}]
     (cons flame (create obj angle-fn ttl)))))


(defn dec-ttl [{ttl :ttl :as obj}]
  (assoc obj :ttl (dec ttl)))

(defn color [{ttl :ttl :as obj}]
  (let [c (if (< ttl color-change-ttl) color-2 color-1)]
    (assoc obj "color" c)))

(defn alive? [{ttl :ttl}] (> ttl 0))

(def xform (comp (map dec-ttl)
                 (map color)
                 (filter alive?)
                 (map u/translate)
                 (map u/model-to-points)))

(defn update [state]
  (let [flames (get-in state [:objects :flames])]
    (assoc-in state [:objects :flames] (sequence xform flames))))

(defn create-n [n f ttl]
  (fn [input]
    (take n (create input f ttl))))

(defn create-n2 [f ttl]
  (fn [input]
    (create input f ttl)))

(def flame-xform (comp
                   (mapcat (create-n2 angle-fn default-ttl))
                   (take flame-count)))

(defn create-flames [state obj]  
  (let [flames (get-in state [:objects :flames])
        tmp (sequence flame-xform obj)
        flames (concat flames (sequence flame-xform obj))]    
    (assoc-in state [:objects :flames] flames)))

(defn create-ship-flames [ship]
  (take ship-flame-count (create ship (ship-angle-fn ship) ship-ttl)))
