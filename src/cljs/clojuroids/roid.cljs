(ns clojuroids.roid
  (:require
   [clojuroids.util :refer [degree-mask degree-max cos sin width height]]))

(defn rotation [min max]
  (let [r1 (range (bit-not (dec max)) (bit-not (dec min)))
        r2 (range min (inc max))]
    (rand-nth (into r1 r2))))

(def large-roid {:sides 9 :size 50 :vel (inc (rand-int 3)) :rot (rotation 1 2)})
(def medium-roid {:sides 7 :size 20 :vel (inc (rand-int 3)) :rot (rotation 1 2)})
(def small-roid {:sides 5 :size 8 :vel (+ 5 (rand-int 3)) :rot (rotation 11 15)})

(defn roid-seq [xfn yfn params]
  (let [{:keys [sides size vel rot]} params
        angle (rand-int degree-max)]
    (cons {:points (for [i (range sides)]
                     [(-> i (* degree-max) (/ sides) (+ (rand-int 30)))
                      (+ size (rand-int (int (/ size 2))))])
           :size size
           :posx (xfn) 
           :posy (yfn) 
           :vel [(* vel (cos angle)) (* vel (sin angle))] 
           :angle 0
           :rot rot}
          (lazy-seq (roid-seq xfn yfn params)))))

(defn translate [roid]
  (let [{:keys [posx posy angle rot] [velx vely] :vel} roid
        x (- (mod (+ posx 50 velx) (+ width 100)) 50) 
        y (- (mod (+ posy 50 vely) (+ height 100)) 50)
        rot (mod (+  angle rot) degree-mask)]
    (assoc roid :posx x :posy y :angle rot)))

(defn update[roids]
  (map translate roids))

(defn break-roid [roid]
  (let [{:keys [size posx posy]} roid]
    (condp = size
      50 (take (inc (rand-int 3)) (roid-seq (fn [] posx) (fn [] posy) medium-roid))
      20 (take (rand-int 3) (roid-seq  (fn [] posx) (fn [] posy) small-roid))
      [])))

(defn create-roids []
  (take 4 (roid-seq #(rand-int width) #(rand-int height) large-roid)))
