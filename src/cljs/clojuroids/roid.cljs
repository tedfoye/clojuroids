(ns clojuroids.roid
  (:require [clojuroids.util :as u]))

(def size-lg 40)
(def size-md 20)
(def size-sm 10)

(def lg-roid {:sides 9 :size size-lg :vel 1 :rot 2})
(def md-roid {:sides 7 :size size-md :vel 3 :rot 8})
(def sm-roid {:sides 5 :size size-sm :vel 5 :rot 15})

(defn make-model [sides size]
  (for [i (range sides)]
    [(-> i (* u/degree-max) (/ sides))
     (+ size (rand-int size))]))

(defn roid-seq [xfn yfn params]
  (lazy-seq
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
           (roid-seq xfn yfn params)))))

(def update-xform (comp (map u/translate) (map u/model-to-points)))

(defn update [state]
  (let [roids (get-in state [:objects :roids])
        roids (sequence update-xform roids)]
    (assoc-in state [:objects :roids] roids)))

(defn break-lg [x y]
  (let [n (inc (rand-int 4))]
    (take n (roid-seq (fn [] x)
                      (fn [] y)
                      md-roid))))

(defn break-md [x y]
  (let [n (rand-int 4)]
    (take n (roid-seq (fn [] x)
                      (fn [] y)
                      sm-roid))))

(defn break-roid [roid]
  (if (seq roid)
    (let [{:keys [size x y]} roid]
      (condp = size
        size-lg (break-lg x y) 
        size-md (break-md x y) 
        []))))

(defn create-roids [n]
  (take n (roid-seq #(rand-int u/width)
                    #(rand-int u/height)
                    lg-roid)))
