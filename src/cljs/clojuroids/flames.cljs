(ns clojuroids.flames
  (:require [clojuroids.util :as u]))

(def color-1 "#ffff99")
(def color-2 "#DC143C")
(def flame-count 6)
(def ship-flame-count 3)
(def flame-spread 50)
(def default-ttl 10)
(def ship-ttl 2)
(def opposite-dir (/ u/degree-max 2))

(defn angle-fn [] (rand-int u/degree-max))

(defn ship-angle-fn [ship]
  (fn []
    (+ (:angle ship)
       opposite-dir
       (u/roid-rnd flame-spread))))

(defn create [obj angle-fn ttl]
  (lazy-seq
   (let [{:keys [x y]} obj
         model [[0 1] [128 1] [256 1] [384 1]]
         flame {:model model 
                :rect (u/rect model)
                :x x
                :y y 
                :vel [(* (+ 5 (u/roid-rnd 2)) (u/cos (angle-fn)))
                      (* (+ 5 (u/roid-rnd 2)) (u/sin (angle-fn)))] 
                :angle (mod (angle-fn) u/degree-mask)
                :rot 0
                :ttl (+ ttl (rand-int 5))}]
     (cons flame (create obj angle-fn ttl)))))

(defn take-inject [n f]
  (fn [f1]
    (fn
      ([] (f1))
      ([result] (f1 result))
      ([result input]
         (f1 result (take n (f input)))))))

(def xform (comp (map #(assoc % :ttl (dec (:ttl %))))
                 (map #(assoc % :color (if (< (:ttl %) 3) color-2 color-1)))
                 (filter #(> (:ttl %) 0))
                 (map u/translate)
                 (map u/model-to-points)))

(defn update [flames] (sequence xform flames))

(def flames-xform (comp (take flame-count)
                        (map #(create % angle-fn default-ttl))))

(defn create-flames [obj]
  (sequence (take-inject flame-count #(create % angle-fn default-ttl)) obj))

(def ship-flames-xform (comp (take ship-flame-count)
                             (map #(create % ship-angle-fn ship-ttl))))

(def ship-flame-fn (fn [obj] (create obj ship-angle-fn ship-ttl)))

(defn create-ship-flames [ship]
  (sequence (take-inject ship-flame-count ship-flame-fn) ship))
